package com.heima.article.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {

@Autowired
private ApArticleContentMapper articleContentMapper;

@Autowired
private Configuration configuration;

@Autowired
private FileStorageService fileStorageService;

@Autowired
private ApArticleMapper apArticleMapper;

    @Test
    @Transactional
    @Rollback(value = false)
    public void createStaticUrlTest() throws Exception {
        //1.查询文章内容,在article_content表中,根据文章id查询
        ApArticleContent articleContent = articleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                .eq(ApArticleContent::getArticleId, 1390536764510310401l));
        if (articleContent != null && StringUtils.isNotBlank(articleContent.getContent())){
            //2.查询出文章内容后,通过configuration获取freemarker模板对象(指定模板)
            Template template = configuration.getTemplate("article.ftl");
            //3.通过freemarker创建html,参数一:数据模型类似map、参数二，输出流,可以指定输出位置或者把数据存放在内存(字符串格式)
            //4.TODO:传过去的数据模型需要是一个集合,是根据集合内的为不为空,再判断集合中的对象的type字段的值,而数据库内容是string
            List<Map> list = JSON.parseArray(articleContent.getContent(), Map.class);
            HashMap<String, Object> model = new HashMap<>();
            model.put("content",list);//参数一
            StringWriter out = new StringWriter();//参数二,字符串格式暂存内存
            template.process(model,out);
            //5.生成后把文件上传到minio中
            //6.参数三需要字节输入流,因此需要对out进行转换
            ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
            String path = fileStorageService.uploadHtmlFile("", articleContent.getArticleId() + ".html", inputStream);

            //6.对ApArticle表进行更新操作
            apArticleMapper.update(null,Wrappers.<ApArticle>lambdaUpdate()
                    .eq(ApArticle::getId,articleContent.getArticleId())
                    .set(ApArticle::getStaticUrl,path));
        }

    }

}

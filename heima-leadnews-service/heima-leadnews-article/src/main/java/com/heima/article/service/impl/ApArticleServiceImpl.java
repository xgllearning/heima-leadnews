package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    //定义常量-单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleMapper apArticleMapper;

    /**
     * 根据参数加载文章列表
     *
     * @param dto
     * @param loadType 1为加载更多,2为加载更新
     * @return
     */
    public ResponseResult load(ArticleHomeDto dto, Short loadType) {
        //第一步要对参数进行校验
        //1.判断分页size是否有值，没有值或者超过最大值要设置初值
        Integer size = dto.getSize();
        if (size == null || size <= 0) {
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        //2.对dto中size重新赋值
        dto.setSize(size);
        //3.判断最大时间和最小时间是否有效
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
        //4.判断频道是否有效
        if (StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        //5.判断加载类型,均与查询的加载类型不相等
        if(!ArticleConstants.LOADTYPE_LOAD_MORE.equals(loadType)&&!ArticleConstants.LOADTYPE_LOAD_NEW.equals(loadType)){
            loadType=ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //6.查询
        List<ApArticle> apArticleList = apArticleMapper.loadArticleList(dto, loadType);
        //7.结果封装返回
        return ResponseResult.okResult(apArticleList);
    }
}

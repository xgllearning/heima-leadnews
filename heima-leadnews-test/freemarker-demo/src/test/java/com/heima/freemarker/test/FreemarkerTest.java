package com.heima.freemarker.test;

import com.heima.freemarker.FreemarkerApp;
import com.heima.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SpringBootTest(classes = FreemarkerApp.class)
@RunWith(SpringRunner.class)
public class FreemarkerTest {
//注入configuration---freemarker依赖
    @Autowired
    private Configuration configuration;

    @Test
    public void name() throws Exception {
        //通过configuration获取freemarker模板对象
        // TODO:需要提前在配置文件中配置template-loader-path: classpath:/templates  #指定模板文件存放的位置
        Template template = configuration.getTemplate("02-list.ftl");

        Map map = extracted();

        //调用process，输出模板文件到指定位置
        //参数一：数据模型(Model)--类似于map
        //参数二：输出位置
        template.process(map,new FileWriter("d:/02-list.html"));
    }

    private Map extracted() {
        //使用map代替数据模型
        Map<Object, Object> map = new HashMap<>();
        //创建一个集合stus，存入两个对象stu
        //------------------------------------
        Student stu1 = new Student();
        stu1.setName("小强");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        //小红对象模型数据
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        //将两个对象模型数据存放到List集合中
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向model中存放List集合数据
        map.put("stus",stus);
        //------------------------------------
        //创建Map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        //遍历hashmap方法1
        Set<String> key = stuMap.keySet();//获取到键值
        for (String s : key) {
            //遍历键值后根据键值取数据
            Student student = stuMap.get(s);
        }
        //获取的是键值对
        Set<Map.Entry<String, Student>> entrySet = stuMap.entrySet();
        for (Map.Entry<String, Student> entry : entrySet) {
            Student student = entry.getValue();
        }
        // 3.1 向model中存放Map数据
        map.put("stuMap", stuMap);

        map.put("today",new Date());
        map.put("point",21877895765754l);
        //将json传到freemarker，再转为对象
//        Map<String, String> map1 = new HashMap<>();
//        map.put("name","xm");
//        map.put("age","25");
//        String jsonString = JSON.toJSONString(map1);
//        map.put("json",jsonString);
        return map;
    }


}
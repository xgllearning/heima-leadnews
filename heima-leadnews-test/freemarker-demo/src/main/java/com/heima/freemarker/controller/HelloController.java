package com.heima.freemarker.controller;


import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller//需要返回的是视图，是页面，而不是json，所以需要用controller,而不是restController
public class HelloController {

    /**
     * 合成model.addAttribute
     * 第一个参数：插值表达式的值
     * 第二个参数：真实数据，需要填充进插值表达式的值
     */
    @GetMapping("/basic")
    public String test(Model model){//返回必须是String,model是mvc传递的

        //Hello ${name} <br>
        model.addAttribute("name","freemarkerTest");
        //姓名：${stu.name}<br/>年龄：${stu.age}
        Student student = new Student();
        student.setAge(18);
        student.setName("小明");
        model.addAttribute("stu",student);
        return "freemarker";//templates/freemarker.ftl
    }


    @GetMapping("/test")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //设置modelAndView操作的模板文件
        modelAndView.setViewName("freemarker");
        //Hello ${name} <br>
        modelAndView.addObject("name","freemarkerTest");
        //姓名：${stu.name}<br/>年龄：${stu.age}
        Student student = new Student();
        student.setAge(18);
        student.setName("小明");
        modelAndView.addObject("stu",student);
        return modelAndView;
    }

    @GetMapping("/list")
    public String list(Model model){
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
        model.addAttribute("stus",stus);
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
        model.addAttribute("stuMap", stuMap);

        model.addAttribute("today",new Date());
        model.addAttribute("point",21877895765754l);
        //将json传到freemarker，再转为对象
        Map<String, String> map = new HashMap<>();
        map.put("name","xm");
        map.put("age","25");
//        String jsonString = JSON.toJSONString(map);
//        model.addAttribute("json",jsonString);
        return "02-list";
    }

}

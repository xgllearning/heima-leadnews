package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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



}

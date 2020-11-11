package com.nhb.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Random;

/**
 * @author luck_nhb
 * @version 1.0
 * @description
 * @date 2020/11/11 09:48
 */
@Controller
public class IndexController {
    private static String[] userNames = {"色批","极其猥琐","信波哥得永生","宁缺毋滥","黑胖","大波牛"};

    @GetMapping("/index")
    public String index(Model model){
        Random random = new Random();
        String userName = userNames[random.nextInt(6)];
        model.addAttribute("userid",userName);
        return "index";
    }
}

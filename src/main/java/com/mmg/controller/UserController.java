package com.mmg.controller;

import org.springmvc.stereotype.Controller;
import org.springmvc.ui.ModelMap;
import org.springmvc.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    @RequestMapping("/")
    public String login(ModelMap modelMap) {
        modelMap.addAttribute("username", "Hello SpringMVC");
        return "login";
    }
}

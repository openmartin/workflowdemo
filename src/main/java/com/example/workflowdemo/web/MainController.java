package com.example.workflowdemo.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
public class MainController {

    @RequestMapping(value = "/login")
    public String login(HttpServletRequest request, Locale locale, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        if (request.getMethod().equals("GET")) {
            return "loginForm";
        } else {
            String lang = request.getParameter("lang");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            Cookie cookie = new Cookie("clientlanguage", lang);
            cookie.setMaxAge(100000);
            cookie.setPath("/");
            response.addCookie(cookie);

            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);

            try{
                subject.login(token);
            } catch (AuthenticationException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "登录失败");
                return "redirect:/login";
            }



            return "redirect:/home";
        }
    }

    @RequestMapping(value = "/home")
    public String home(){
        return "home";
    }

    @RequestMapping(value = "/")
    public String index(){
        return "redirect:/home";
    }

}

package com.mitrais.rms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @GetMapping("/error")
    public ModelAndView errorPage(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("error", HttpStatus.valueOf((int) status).toString().split(" "));
        modelAndView.setViewName("errors");

        return modelAndView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

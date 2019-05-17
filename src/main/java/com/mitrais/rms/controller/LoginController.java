package com.mitrais.rms.controller;

import com.mitrais.rms.helper.SecurityContext;
import com.mitrais.rms.model.User;
import com.mitrais.rms.model.UsrDetails;
import com.mitrais.rms.service.StorageService;
import com.mitrais.rms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("account")
public class LoginController {
    @Autowired
    private ConnectionFactoryLocator factoryLocator;
    @Autowired
    private UserService userService;
    @Autowired
    private StorageService storageService;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @GetMapping("login")
    public String login(){
        return "login";
    }

    @GetMapping("signup")
    public ModelAndView create(NativeWebRequest request){
        ModelAndView modelAndView=new ModelAndView();
        User user=new User();
        ProviderSignInAttempt session= (ProviderSignInAttempt) sessionStrategy.getAttribute(request, ProviderSignInAttempt.SESSION_ATTRIBUTE);


        if(session!=null) {
            Connection connection=session.getConnection(factoryLocator);
            user.setName(connection.getDisplayName());
            modelAndView.addObject("provider_name", StringUtils.capitalize(connection.getKey().getProviderId()));
            modelAndView.addObject("connection", connection);
            modelAndView.addObject("user",user);

            if(!(SecurityContextHolder.getContext().getAuthentication()instanceof AnonymousAuthenticationToken)){
                User current=userService.findUserById(((UsrDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().intValue());

                Class usrClass = current.getClass();
                try {
                    Field field = usrClass.getDeclaredField(connection.getKey().getProviderId());
                    field.setAccessible(true);
                    field.set(current,connection.getKey().getProviderUserId());
                    userService.saveUser(current);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                return new ModelAndView("redirect:/users");
            }
        }else {
            modelAndView.addObject("user", user);
        }
        modelAndView.setViewName("signup");
        return modelAndView;
    }

    @PostMapping("signup")
    public ModelAndView store(@Validated(User.Create.class) User user, BindingResult bindingResult, @RequestParam(required = false) MultipartFile image){
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("signup");
        } else {
            if(image.getSize()>0)
                user.setPicture(storageService.store("users",image));
            user.setRole(User.Role.ROLE_USER);
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("signup");
            modelAndView.setViewName("redirect:/account/signup");
        }
        return modelAndView;
    }
}

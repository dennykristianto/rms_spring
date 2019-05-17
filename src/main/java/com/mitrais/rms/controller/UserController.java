package com.mitrais.rms.controller;

import com.mitrais.rms.model.User;
import com.mitrais.rms.model.UsrDetails;
import com.mitrais.rms.service.StorageService;
import com.mitrais.rms.service.UserService;
import com.sun.net.httpserver.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.Collections;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StorageService storageService;

    @GetMapping
    public ModelAndView allUsers(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("users",userService.findAll());
        modelAndView.setViewName("users");

        return modelAndView;
    }

    @GetMapping("details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or principal.id==#id")
    public ModelAndView userDetails(@PathVariable int id){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("user",userService.findUserById(id));
        modelAndView.addObject("is_current", ((UsrDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().equals((long)id));
        modelAndView.setViewName("user_details");
        return modelAndView;
    }

    @PostMapping("details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or principal.id==#id")
    public ModelAndView userUpdate(@PathVariable int id,@Validated(User.BasicUpdate.class) User user, BindingResult bindingResult, @RequestParam(required = false) MultipartFile image){
        ModelAndView modelAndView=new ModelAndView();
        User userInDb=userService.findUserById(id);

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("user_details");
        } else {
            userInDb.setName(user.getName());
            userInDb.setEmail(user.getEmail());
            userInDb.setAddress(user.getAddress());
            userInDb.setUsername(user.getUsername());
            modelViewConstructor(image, modelAndView, userInDb, "User successfully updated!");
        }
        return modelAndView;
    }

    @PostMapping("disconnect/{provider}")
    public String disconnectProvider(@PathVariable String provider, HttpServletRequest request){
        if(!(SecurityContextHolder.getContext().getAuthentication()instanceof AnonymousAuthenticationToken)){
            User current=userService.findUserById(((UsrDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().intValue());

            Class usrClass = current.getClass();
            try {
                Field field = usrClass.getDeclaredField(provider);
                field.setAccessible(true);
                field.set(current,null);
                userService.saveUser(current);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "redirect:"+ request.getHeader("Referer");
    }

    private void modelViewConstructor(@RequestParam(required = false) MultipartFile image, ModelAndView modelAndView, User userInDb, String s) {
        if(image.getSize()>0)
            userInDb.setPicture(storageService.store("users",image));
        userInDb.setRole(User.Role.ROLE_USER);
        userService.saveUser(userInDb);
        modelAndView.addObject("successMessage", s);
        modelAndView.addObject("user", new User());
        modelAndView.setViewName("user_details");
        modelAndView.setViewName("redirect:/users");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "password/{id}",
            method = {RequestMethod.PATCH,RequestMethod.PUT})
    public ResponseEntity userUpdatePassword(@RequestBody User user,@PathVariable int id){
        User userInDb=userService.findUserById(id);
        userInDb.setPassword(user.getPassword());
        userService.saveUser(userInDb);
        return ResponseEntity.ok().headers(getHeaders()).body(Collections.singletonMap("response","User password update!"));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("create")
    public ModelAndView create(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("user",new User());
        modelAndView.setViewName("user_details");
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("create")
    public ModelAndView store(@Validated(User.Create.class) User user, BindingResult bindingResult, @RequestParam(required = false) MultipartFile image){
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("user_details");
        } else {
            modelViewConstructor(image, modelAndView, user, "User has been registered successfully");
        }
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity userDelete(@PathVariable int id){
        userService.deleteUser(userService.findUserById(id));
        return ResponseEntity.ok().headers(getHeaders()).body(Collections.singletonMap("response","User successfully deleted!"));
    }

    public HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        return headers;
    }
}

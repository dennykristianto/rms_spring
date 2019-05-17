package com.mitrais.rms.helper;

import com.mitrais.rms.model.User;
import com.mitrais.rms.model.UsrDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;

public class Helper {
    public static String getGreetings(){
        LocalTime time=LocalTime.now();
        if(time.getHour()>0 && time.getHour()<12)
            return "Morning";
        if(time.getHour()>=12 && time.getHour()<16)
            return "Afternoon";
        if(time.getHour()>=16 && time.getHour()<21)
            return "Evening";
        else
            return "Night";
    }

    public static boolean isLoggedInUser(User user){
        return ((UsrDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId().equals((long)user.getId());
    }
}

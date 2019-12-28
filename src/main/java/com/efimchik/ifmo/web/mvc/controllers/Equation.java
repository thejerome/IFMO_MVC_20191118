package com.efimchik.ifmo.web.mvc.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class Equation {
    @PutMapping("/calc/equation")
    public ResponseEntity Put_Equation(@RequestBody String equation, HttpSession session){
        int status;
        if(badlyFormatted(equation)){
            status = 400;
        }
        else {
            if(session.getAttribute("equation") == null){
                status = 201;
            }
            else status = 200;
            session.setAttribute("equation", equation);
        }
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    private boolean badlyFormatted(String equation){
        Pattern pattern = Pattern.compile("([a-z]{2})");
        Matcher matcher = pattern.matcher(equation);
        return matcher.find();
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity Delete(HttpSession session){
        session.setAttribute("equation", null);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }
}

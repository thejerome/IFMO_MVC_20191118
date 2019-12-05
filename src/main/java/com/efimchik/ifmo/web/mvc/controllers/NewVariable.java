package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class NewVariable {

    @PutMapping("/calc/{variableName}")
    public ResponseEntity newVariable(HttpSession session, @PathVariable String variableName, @RequestBody String value){
        if (isGoodValue(value)){
            if (session.getAttribute(variableName) != null){
                session.setAttribute(variableName, value);
                return new ResponseEntity(HttpStatus.valueOf(200));
            } else {
                session.setAttribute(variableName, value);
                return new ResponseEntity(HttpStatus.valueOf(201));
            }
        } else {
            return new ResponseEntity(HttpStatus.valueOf(403));
        }
    }

    @DeleteMapping("/calc/{variableName}")
    public ResponseEntity deleteVar(HttpSession session, @PathVariable String variableName) {
        session.removeAttribute(variableName);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean isGoodValue(String value){
        return value.matches("[a-z]") || Integer.parseInt(value) >= -10000 && Integer.parseInt(value) <= 10000;
    }

}

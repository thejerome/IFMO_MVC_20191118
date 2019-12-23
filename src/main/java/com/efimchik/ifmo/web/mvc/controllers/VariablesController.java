package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class VariablesController {

    @PutMapping("/calc/{varName}")
    public ResponseEntity<String> putVar(HttpSession session, @PathVariable String varName, @RequestBody String value) {
        if (isGoodFormatted(value)) {
            Object oldValue = session.getAttribute(varName);
            session.setAttribute(varName, value);
            if (oldValue != null) {
                return new ResponseEntity<>("good, value is saved", HttpStatus.valueOf(200));
            }
            else {
                return new ResponseEntity<>("good, value is saved in first time", HttpStatus.valueOf(201));
            }
        } else {
            return new ResponseEntity<>("something bad with variable value", HttpStatus.valueOf(403));
        }
    }

    @DeleteMapping("/calc/{varName}")
    public ResponseEntity deleteVar(HttpSession session, @PathVariable String varName) {
        session.removeAttribute(varName);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean isGoodFormatted(String body) {
        if (body.charAt(0) >= 'a' && body.charAt(0) <= 'z' && body.length() == 1)
            return true;
        try{
            int value = Integer.parseInt(body);
            return value >= -10000 && value <= 10000;
        } catch (Exception e){
            return false;
        }
    }

}

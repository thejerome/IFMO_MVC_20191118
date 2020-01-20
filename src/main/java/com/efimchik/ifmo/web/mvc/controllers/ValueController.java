package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

@RestController
public class ValueController {

    @PutMapping("/calc/{path}")
    public ResponseEntity<String> doPut(HttpSession session, @PathVariable String path, @RequestBody String body) {
        if (goodFormat(body)) {
            Object newPath = session.getAttribute(path);
            session.setAttribute(path, body);
            if (newPath != null) {
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            }
            else {
                return new ResponseEntity<>(HttpStatus.valueOf(201));
            }
        } else {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }
    }

    @DeleteMapping("/calc/{var}")
    public ResponseEntity deleteVar(HttpSession session, @PathVariable String var) {
        session.removeAttribute(var);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean goodFormat(String variable) {
        char c = variable.charAt(0);
        if (Pattern.matches("[a-z]", Character.toString(c)))
            return true;
        try {
            return Integer.parseInt(variable) >= -10000 && Integer.parseInt(variable) <= 10000;
        } catch (Exception e){
            return false;
        }
    }

}

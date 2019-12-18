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
        if (!isGoodFormatted(value))
            return new ResponseEntity<>("something bad with variable value", HttpStatus.FORBIDDEN);
         else {
            Object sessionAttribute = session.getAttribute(varName);
            session.setAttribute(varName, value);
            if (sessionAttribute == null) {
                return new ResponseEntity<>("good, value is saved in first time", HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>("good, value is saved", HttpStatus.OK);
            }
        }
    }

    @DeleteMapping("/calc/{varName}")
    public ResponseEntity deleteVar(HttpSession session, @PathVariable String varName) {
        session.removeAttribute(varName);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private boolean isGoodFormatted(String body) {
        if (body.charAt(0)>='a' && body.charAt(0)<='z') {
            return true;
        }
        return Integer.parseInt(body) >= -10000 && Integer.parseInt(body) <= 10000;
    }
}

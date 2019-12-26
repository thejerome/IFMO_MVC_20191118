package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class VariablesContr {

    @PutMapping("/calc/{name}")
    public ResponseEntity<String> putVariable(HttpSession session, @PathVariable String name, @RequestBody String value) {
        if ((value.charAt(0) >= 'a') && (value.charAt(0) <= 'z')) {
            return getStringResponseEntity(session, name, value);
        } else if (Math.abs(Integer.parseInt(value)) < 10000) {
            return getStringResponseEntity(session, name, value);
        } else {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }

    }

    private ResponseEntity<String> getStringResponseEntity(HttpSession session, @PathVariable String name, @RequestBody String value) {
        if (session.getAttribute(name) == null) {
            session.setAttribute(name, value);
            return new ResponseEntity<>(HttpStatus.valueOf(201));
        } else {
            session.setAttribute(name, value);
            return new ResponseEntity<>(HttpStatus.valueOf(200));
        }
    }

    @DeleteMapping("/calc/{varName}")
    public ResponseEntity deleteVar(HttpSession session, @PathVariable String varName) {
        session.removeAttribute(varName);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }


}
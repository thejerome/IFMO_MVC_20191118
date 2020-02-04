package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class VariableController {

    @PutMapping("/calc/{key}")
    public ResponseEntity<String> putVariable(@PathVariable String key, @RequestBody String value, HttpSession session) {
        if (goodFormatted(value)) {
            if (session.getAttribute(key) != null) {
                session.setAttribute(key, value);
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            } else {
                session.setAttribute(key, value);
                return new ResponseEntity<>(HttpStatus.valueOf(201));
            }

        } else {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }
    }

    @DeleteMapping("/calc/{key}")
    public ResponseEntity<String> deleteVariable(@PathVariable String key, HttpSession session) {
        session.removeAttribute(key);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean goodFormatted(String variable) {
        char c = variable.charAt(0);
        if (c >= 'a' && c <= 'z')
            return true;
        try {
            return Integer.parseInt(variable) > -10000 && Integer.parseInt(variable) < 10000;
        } catch (Exception e){
            return false;
        }
    }
}
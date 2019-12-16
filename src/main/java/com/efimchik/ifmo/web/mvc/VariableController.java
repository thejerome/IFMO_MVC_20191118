package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class VariableController {

    @PutMapping("/calc/{name}")
    public ResponseEntity<String> putVariable(HttpSession session, @PathVariable String name, @RequestBody String value) {
        if (!((value.charAt(0) >= 'a' && value.charAt(0) <= 'z') || (Integer.valueOf(value) > -10000 && Integer.valueOf(value) < 10000)))
            return new ResponseEntity<>("Illegal variable!", HttpStatus.valueOf(403));

        if (session.getAttribute(name) == null) {
            session.setAttribute(name, value);
            return new ResponseEntity<>("Variable created!", HttpStatus.valueOf(201));
        } else {
            session.setAttribute(name, value);
            return new ResponseEntity<>("Variable replaced!", HttpStatus.valueOf(200));
        }

    }

    @DeleteMapping("/calc/{name}")
    public ResponseEntity deleteVariable(HttpSession session, @PathVariable String name) {
        session.removeAttribute(name);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }
}
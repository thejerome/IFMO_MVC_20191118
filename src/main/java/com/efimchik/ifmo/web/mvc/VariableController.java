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
    public ResponseEntity<String> putVariable(
            @PathVariable String key,
            @RequestBody String value,
            HttpSession session
    ) {
        if ((value.charAt(0) >= 'a' && value.charAt(0) <= 'z') || (Integer.parseInt(value) > -10000 && Integer.parseInt(value) < 10000)) {
            if (session.getAttribute(key) != null) {
                session.setAttribute(key, value);
                return new ResponseEntity<>("Variable replaced!", HttpStatus.valueOf(200));
            } else {
                session.setAttribute(key, value);
                return new ResponseEntity<>("Variable created!", HttpStatus.valueOf(201));
            }

        } else {
            return new ResponseEntity<>("Illegal variable!", HttpStatus.valueOf(403));
        }
    }

    @DeleteMapping("/calc/{key}")
    public ResponseEntity<String> deleteVariable(
            @PathVariable String key,
            HttpSession session
    ) {
        session.removeAttribute(key);
        return new ResponseEntity("Variable deleted", HttpStatus.valueOf(204));
    }
}

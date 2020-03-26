package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class Vars {

    @PutMapping(value = "/calc/{var}")
    public ResponseEntity<String> put(@PathVariable String var, @RequestBody String val, HttpSession session) {
        if (check(val)) {
            if (session.getAttribute(var) == null) {
                session.setAttribute(var, val);
                return new ResponseEntity<>(HttpStatus.valueOf(201));
            } else {
                session.setAttribute(var, val);
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            }

        } else {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }
    }

    @DeleteMapping(value = "/calc/{var}")
    public ResponseEntity<String> delete(@PathVariable String var, HttpSession session) {
        session.setAttribute(var, null);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    private boolean check(String str) {
        boolean bool1;
        boolean bool2;
        try {
            bool1 = Integer.parseInt(str) >= -10000;
            bool2 = Integer.parseInt(str) <= 10000;
        } catch (NumberFormatException e) {
            return str.charAt(0) >= 'a' && str.charAt(0) <= 'z';
        }
        return bool1 && bool2;
    }
}

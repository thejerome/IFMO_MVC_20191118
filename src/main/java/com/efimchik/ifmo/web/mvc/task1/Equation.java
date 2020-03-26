package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class Equation {

    @PutMapping(value = "/calc/equation")
    public ResponseEntity<String> put(HttpSession session, @RequestBody String eq) {
        if (!check(eq)) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        if (session.getAttribute("equation") == null) {
            session.setAttribute("equation", eq);
            return new ResponseEntity<>(HttpStatus.valueOf(201));
        }
        else {
            session.setAttribute("equation", eq);
            return new ResponseEntity<>(HttpStatus.valueOf(200));
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity<String> delete(HttpSession session) {
        session.setAttribute("equation", null);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    private boolean check(String eq) {
        boolean op = false;
        for (char c : eq.toCharArray()) {
            if (c >= 'A' && c <= 'Z')
                return false;
            if (c == '+' || c == '-' || c == '/' || c == '*')
                op = true;
        }
        return op;
    }
}

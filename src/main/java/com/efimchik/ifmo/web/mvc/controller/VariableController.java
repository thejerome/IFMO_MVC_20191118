package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.util.Check;
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

    @PutMapping("/calc/{variable}")
    public ResponseEntity<String> putVariable(@RequestBody String value, @PathVariable String variable, HttpSession session) {
        if (Check.isNameVarGood(variable)) {
            if (Check.isVarInRange(value)) {
                Object old = session.getAttribute(variable);
                session.setAttribute(variable, value);
                if (old == null) {
                    return new ResponseEntity<>("new variable added", HttpStatus.valueOf(201));
                } else {
                    return new ResponseEntity<>("var changed", HttpStatus.valueOf(200));
                }
            } else {
                return new ResponseEntity<>("Wrong range of var", HttpStatus.valueOf(403));
            }
        } else {
            return new ResponseEntity<>("Bad formatted var", HttpStatus.valueOf(400));
        }
    }

    @DeleteMapping("/calc/{variable}")
    public ResponseEntity deleteVar(HttpSession session, @PathVariable String variable) {
        session.removeAttribute(variable);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }
}

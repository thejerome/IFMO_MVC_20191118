package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.util.Check;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class EquationController {

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEquation(@RequestBody String equation, HttpSession session) {
        if (Check.isEquationGood(equation)) {
            Object old = session.getAttribute("equation");
            session.setAttribute("equation", equation);
            if (old == null) {
                return new ResponseEntity<>("New equation", HttpStatus.valueOf(201));
            } else {
                return new ResponseEntity<>("Equation changed", HttpStatus.valueOf(200));
            }
        } else {
            return new ResponseEntity<>("Equation wrong", HttpStatus.valueOf(400));
        }
    }

    @DeleteMapping("calc/equation")
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }
}

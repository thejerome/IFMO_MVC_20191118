package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class NewEquation {
    @PutMapping("/calc/equation")
    public ResponseEntity newEquation(HttpSession session, @RequestBody String equation){
        if (isGoodEquation(equation)) {
            if (session.getAttribute("equation") != null) {
                session.setAttribute("equation", equation);
                return new ResponseEntity(HttpStatus.valueOf(200));
            } else {
                session.setAttribute("equation", equation);
                return new ResponseEntity(HttpStatus.valueOf(201));
            }
        } else {
            return new ResponseEntity(HttpStatus.valueOf(400));
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean isGoodEquation(String equation){
        boolean containsMathSymbols = false;
        boolean containsUpperCase = false;
        for (char c: equation.toCharArray()) {
            if (Character.toString(c).matches("[A-Z]")){
                containsUpperCase = true;
            } else if (Character.toString(c).matches("[-+*/]")) {
                containsMathSymbols = true;
            }
        }
        return containsMathSymbols && !containsUpperCase;
    }
}

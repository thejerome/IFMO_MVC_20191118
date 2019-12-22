package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class EquationController{
    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEquation(HttpSession session, @RequestBody String equation){

        String text = "Illegal equation";
        int code = 400;
        equation = equation.replace(" ", "");
        if (equation.indexOf('*') == -1 && equation.indexOf('/') == -1 && equation.indexOf('+') == -1 && equation.indexOf('-') == -1)
            return new ResponseEntity<>(text, HttpStatus.valueOf(code));
        if (session.getAttribute("equation") == null) {
            text = "Equation created";
            code = 201;
        }
        else {
            text = "Equation replaced";
            code = 200;
        }
        session.setAttribute("equation", equation);
        return new ResponseEntity<>(text, HttpStatus.valueOf(code));
    }
    @DeleteMapping("/calc/equation")
    public ResponseEntity deleteEquation(HttpSession session){
        session.removeAttribute("equation");
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}
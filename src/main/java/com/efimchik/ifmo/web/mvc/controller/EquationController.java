package com.efimchik.ifmo.web.mvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@SessionAttributes("equation")
public class EquationController {

    @PutMapping("/calc/equation")
        public ResponseEntity setEquation(@RequestBody String equation, ModelMap model)  {
        if (checkbadformat(equation)) {
            return new ResponseEntity(HttpStatus.valueOf(400));
        }
        else {
            Object attr = model.getAttribute("equation");
            model.addAttribute("equation", equation);
            if (attr == null)
                return new ResponseEntity(HttpStatus.valueOf(201));
            else return new ResponseEntity(HttpStatus.valueOf(200));
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity DeleteRequest(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean checkbadformat(String str) {
        for (int i=0; i<str.length(); i++) {
            if(isOperator(str.charAt(i)))
                return false;
            if(str.charAt(i)<='Z' && str.charAt(i)>='A')
                return true;
        }
        return true;
    }

    private boolean isOperator(char c) {
        return (c == '*' || c == '/' || c == '+' || c == '-');
    }
}
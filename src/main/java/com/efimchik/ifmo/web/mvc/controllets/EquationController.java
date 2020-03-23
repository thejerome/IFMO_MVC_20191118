package com.efimchik.ifmo.web.mvc.controllers;

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
    public ResponseEntity<String> putEquation(HttpSession session, @RequestBody String equation) {
        boolean isOkExpression = false;

        for (int i = 0; i < equation.length(); i++) {
            char temp = equation.charAt(i);
            if (temp == '/' ||
                temp == '*' ||
                temp == '+' ||
                temp == '-' ||
                temp == '(' ||
                temp == ')') {
                isOkExpression = true;
                break;
            }
        }

        if (!isOkExpression) {
            return new ResponseEntity<String>("Bad equation", HttpStatus.valueOf(400));
        } else {
            if (session.getAttribute("equation") == null) {
                session.setAttribute("equation", equation);
                return new ResponseEntity<String>("Bad equation", HttpStatus.valueOf(201));
            } else {
                session.setAttribute("equation", equation);
                return new ResponseEntity<String>("Bad equation", HttpStatus.valueOf(200));
            }
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity<String>("Bad equation", HttpStatus.valueOf(204));
    }
}
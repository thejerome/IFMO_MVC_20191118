package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

@RestController
public class EquationController {
    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEq(HttpSession httpSession, @RequestBody String body){
        if (!goodInput(body)) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        } else {
            if (httpSession.getAttribute("equation") == null) {
                httpSession.setAttribute("equation", body);
                return new ResponseEntity<>(HttpStatus.valueOf(201));
            } else {
                httpSession.setAttribute("equation", body);
                return new ResponseEntity<>( HttpStatus.valueOf(200));
            }
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity delEq(HttpSession httpSession){
        httpSession.setAttribute("equation", null);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    private boolean goodInput(String equation) {
        int indexZero = 0;
        for (int i = 0; i < equation.length(); ++i) {
            char c = equation.charAt(i);
            if (!Pattern.matches("[A-Z]", Character.toString(c))) {
                if (isOperator(c)) {
                    indexZero++;
                }
            } else {
                return false;
            }
        }
        return indexZero != 0;
    }

    private boolean isOperator(char c){
        return c =='+' || c == '-' || c =='/' || c == '*';
    }

}

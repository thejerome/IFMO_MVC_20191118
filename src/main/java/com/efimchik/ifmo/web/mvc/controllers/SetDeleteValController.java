package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.regex.Pattern;

@Controller
public class SetDeleteValController {
    @RequestMapping(value = "/calc/{var:[a-z]|equation}", method = RequestMethod.PUT)
    public ResponseEntity setVar(@PathVariable String var, @RequestBody String body, HttpSession session) {

        String value = body.replaceAll("\\s","");
        if("equation".equals(var) && !isValidEquation(body)) {
            return ResponseEntity.status(HttpStatus.valueOf(400)).body(null);
        } else if (isInteger(value) && (Integer.parseInt(value) > 10000 || Integer.parseInt(value) < -10000)){
            return ResponseEntity.status(HttpStatus.valueOf(403)).body(null);
        }

        Enumeration<String> e = session.getAttributeNames();
        while (e.hasMoreElements()) {
            if(var.equals(e.nextElement())) {
                session.setAttribute(var, value);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(null);
            }
        }
        session.setAttribute(var,value);
        return ResponseEntity.status(HttpStatus.valueOf(201)).body(null);
    }

    @RequestMapping(value = "/calc/{var:[a-z]|equation}", method = RequestMethod.DELETE)
    public ResponseEntity delVariable(@PathVariable String var, HttpSession session) {
        session.removeAttribute(var);
        return ResponseEntity.status(HttpStatus.valueOf(204)).body(null);
    }

    private boolean isValidEquation(String equation) {
        int ind = 0;
        for (int i = 0; i < equation.length(); i++) {
            char c = equation.charAt(i);
            if (!Pattern.matches("[A-Zа-яА-Я]", Character.toString(c))) {
                if (isOperator(c)) {
                    ind++;
                }
            } else {
                return false;
            }
        }
        return ind != 0;
    }

    private boolean isOperator(char op){
        return op =='+' || op == '-' || op =='/' || op == '*';
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}

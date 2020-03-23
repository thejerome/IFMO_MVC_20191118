package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;
import java.util.*;
import com.efimchik.ifmo.web.mvc.Equation;

@Controller
public class ResultController {
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session) {
        try {
            String equation = String.valueOf(session.getAttribute("equation"));
            Map<String, String> variables = new HashMap<String, String>();
            for(int i = 0; i < equation.length(); i++) {
                if (equation.charAt(i) >= 'a' && equation.charAt(i) <= 'z') {
                    String name = String.valueOf(equation.charAt(i));
                    String value = String.valueOf(session.getAttribute(name));
                    variables.put(name, value);
                }
            }

            Equation equation1 = new Equation(variables);

            return new ResponseEntity<String>(
                    Integer.toString(equation1.calculate(equation)),
                    HttpStatus.valueOf(200)
            );
        } catch (NullPointerException ex) {
            return new ResponseEntity<String>(
                    ex.getMessage(),
                    HttpStatus.valueOf(409)
            );
        }
    }
}

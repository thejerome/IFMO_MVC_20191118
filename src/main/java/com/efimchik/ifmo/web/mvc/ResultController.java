package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ResultController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session) {

        try {
            String equation_str = String.valueOf(session.getAttribute("equation"));

            Map<String, String> variables = new HashMap<String, String>();

            for (int i=0; i < equation_str.length(); ++i) {
                if (equation_str.charAt(i) >= 'a' && equation_str.charAt(i) <= 'z') {
                    String varNameStr = String.valueOf(equation_str.charAt(i));
                    String varValueStr = String.valueOf(session.getAttribute(varNameStr));

                    variables.put(varNameStr, varValueStr);
                }
            }

            Equation equation = new Equation(variables);

            return new ResponseEntity<>(
                    Integer.toString(equation.calculate(equation_str)),
                    HttpStatus.valueOf(200)
            );
        } catch (NullPointerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(409));
        }
    }
}

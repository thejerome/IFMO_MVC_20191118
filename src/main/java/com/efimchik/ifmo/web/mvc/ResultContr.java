 package com.efimchik.ifmo.web.mvc;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;
import static org.springframework.http.HttpStatus.valueOf;

@Controller
public class ResultContr{

    @GetMapping("/calc/result")
    public ResponseEntity<String> getCalcResult(HttpSession session) {
        String equation = (String) session.getAttribute("equation");
        if (equation == null) {
            return new ResponseEntity<>(valueOf(409));
        }
        else {
            String result;
            equation = equation.replaceAll("\\s", " ");
            String result1 = CalcResult.getExpression(equation, session);
            try {
                result = CalcResult.counting(result1);
            } catch (Exception e) {
                return new ResponseEntity<>(valueOf(409));
            }
            return new ResponseEntity<>(result, valueOf(200));
        }
    }
}
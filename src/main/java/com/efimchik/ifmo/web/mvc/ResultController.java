package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;



@Controller
public class ResultController{
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session)  {
        try {
            if (session == null)
                throw new IllegalArgumentException("Empty session!");
            String equation = session.getAttribute("equation").toString();
            if (equation == null)
                throw new IllegalArgumentException("Equation does not exist!");
            String varList = session.getAttribute("varList").toString();
            if (varList == null)
                throw new IllegalArgumentException("There are no variables at all!");

            //insert values in expression
            StringBuilder equation2 = new StringBuilder();
            for (int i = 0; i < equation.length(); i++){
                char var = equation.charAt(i);
                if (isLetter(equation.substring(i, i + 1))) {
                    String value = session.getAttribute(equation.substring(i, i + 1)).toString();
                    while (isLetter(value)) {
                        value = session.getAttribute(String.valueOf(value)).toString();
                    }
                    equation2.append(value);
                }
                else
                    equation2.append(var);
            }
            equation = equation2.toString();
            if (!Pattern.matches("^[0-9*+-/()]+$", equation)){
                System.out.println(equation);
                throw new IllegalArgumentException("Value is missing!");
            }
            return new ResponseEntity<>(String.valueOf(CalcUtil.eval(equation)), HttpStatus.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(409));
        }
    }
    private boolean isLetter(String s){
        return s.charAt(0)>='a' && s.charAt(0)<='z';
    }
}
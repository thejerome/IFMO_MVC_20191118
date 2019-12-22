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
                throw new IllegalArgumentException("Session is empty");
            String equation = session.getAttribute("equation").toString();
            if (equation == null)
                throw new IllegalArgumentException("Equation does not exist");
            String varList = session.getAttribute("varList").toString();
            if (varList == null)
                throw new IllegalArgumentException("Can't find variables");

            StringBuilder STB = new StringBuilder();
            String val;
            char varname;

            for (int i = 0; i < equation.length(); i++){
                varname = equation.charAt(i);
                if (isLet(equation.substring(i, i + 1))) {
                    val = session.getAttribute(equation.substring(i, i + 1)).toString();
                    while (isLet(val)) {
                        val = session.getAttribute(String.valueOf(val)).toString();
                    }
                    STB.append(val);
                }
                else
                    STB.append(varname);
            }
            equation = STB.toString();
            if (!Pattern.matches("^[0-9*+-/()]+$", equation)){
                System.out.println(equation);
                throw new IllegalArgumentException("There is no variables");
            }
            return new ResponseEntity<>(String.valueOf(CalcUtil.cal(equation)), HttpStatus.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(409));
        }
    }
    private boolean isLet(String s){
        return s.charAt(0)>='a' && s.charAt(0)<='z';
    }
}
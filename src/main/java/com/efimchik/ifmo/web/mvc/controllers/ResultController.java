package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.helpers.CalcHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ResultController {
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session) {
        try {
            String equationString = String.valueOf(session.getAttribute("equation"));
            System.out.println("equation: " + equationString);
            Map<String, String> vars = new HashMap<>();
            Enumeration<String> e = session.getAttributeNames();

            if (e.hasMoreElements()) {
                while (e.hasMoreElements()) {
                    String name = e.nextElement();
                    vars.put(name, (String) session.getAttribute(name));
                }
            } else {
                return new ResponseEntity<>(HttpStatus.valueOf(409));
            }
            String parameter = "";

            StringBuilder numericEquation = new StringBuilder();
            for (int i = 0; i < equationString.length(); i++) {
                if (CalcHelper.isLetter(equationString.charAt(i))) {
                    if (vars.containsKey(Character.toString(equationString.charAt(i)))) {
                        parameter = vars.get(Character.toString(equationString.charAt(i)));
                    } else {
                        return new ResponseEntity<>(HttpStatus.valueOf(409));
                    }
                    while (CalcHelper.isLetter(parameter.charAt(0))) {
                        if (vars.containsKey(Character.toString(parameter.charAt(0)))) {
                            parameter = vars.get(Character.toString(parameter.charAt(0)));
                        } else {
                            return new ResponseEntity<>(HttpStatus.valueOf(409));
                        }
                    }
                    numericEquation.append(parameter);
                } else {
                    numericEquation.append(equationString.charAt(i));
                }
            }
            System.out.println("numeric equation: " + numericEquation);
            return new ResponseEntity<String>(String.valueOf(CalcHelper.calculation(numericEquation.toString())), HttpStatus.valueOf(200));
        } catch (NullPointerException e) {
            return new ResponseEntity<String>(Arrays.toString(e.getStackTrace()), HttpStatus.valueOf(409));
        }
    }
}

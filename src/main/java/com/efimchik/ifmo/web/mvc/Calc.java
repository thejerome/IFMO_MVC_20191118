package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.*;

public class Calc {
    public static ResponseEntity<String> calculateResult(HttpSession session) {

        String equation = String.valueOf(session.getAttribute("equation"));
        equation = equation.replaceAll("\\s+", "");
        boolean error = false;
        int i = 0;

        while (i < equation.length()) {
            if (equation.charAt(i) >= 'a' && equation.charAt(i) <= 'z') {
                String value = String.valueOf(session.getAttribute(String.valueOf(equation.charAt(i))));
                while (value.charAt(0) >= 'a' && value.charAt(0) <= 'z') {
                    if (session.getAttribute(String.valueOf(equation.charAt(i))) == null) {
                        error = true;
                        break;
                    }
                    value = String.valueOf(session.getAttribute(value));
                }
                if (error) {
                    break;
                }
                equation = equation.replace(String.valueOf(equation.charAt(i)), value);
            }
            i++;
        }

        i = 1;
        while (i < equation.length()) {
            if (equation.charAt(i) == '-' && equation.charAt(i - 1) >= '0' && equation.charAt(i - 1) <= '9') {
                equation = equation.replace(String.valueOf(equation.charAt(i)), "=");
            }
            i++;
        }
        String result;
        if (error) {
            int status = 409;
            return new ResponseEntity<>(HttpStatus.valueOf(status));
        } else {

            CalcMethodUtil calcMethodUtil = new CalcMethodUtil();
            List<String> expression = calcMethodUtil.parse(equation);

            result = calc(expression);
        }

        int status = 200;
        return new ResponseEntity<>(result.toString(), HttpStatus.valueOf(status));
    }

    private static String calc(List<String> postfix) {
        Stack<Integer> stack = new Stack<>();
        String addition = "+";
        String beEqual = "=";
        String multiplication = "*";
        String division = "/";

        for (String x : postfix) {
            if (addition.equals(x)) {
                stack.push(stack.pop() + stack.pop());
            } else if (beEqual.equals(x)) {
                Integer b = stack.pop();
                Integer a = stack.pop();
                stack.push(a - b);
            } else if (multiplication.equals(x)) {
                stack.push(stack.pop() * stack.pop());
            } else if (division.equals(x)) {
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a / b);
            } else {
                stack.push(Integer.valueOf(x));
            }
        }

        return String.valueOf(stack.pop());
    }
}

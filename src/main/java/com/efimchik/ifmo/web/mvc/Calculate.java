package com.efimchik.ifmo.web.mvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


public class Calculate {

    public static ResponseEntity<String> calculateResult(HttpSession session) {

        String equation = String.valueOf(session.getAttribute("equation"));
        equation = equation.replaceAll("\\s+", "");
        boolean error = false;

        for (int i = 0; i < equation.length(); i++) {
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
        }

        for (int i = 1; i < equation.length(); i++) {
            if (equation.charAt(i) == '-' && equation.charAt(i - 1) >= '0' && equation.charAt(i - 1) <= '9') {
                equation = equation.replace(String.valueOf(equation.charAt(i)), "=");
            }
        }
        String result;
        if (error) {
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        } else {

            ParserUtil n = new ParserUtil();
            List<String> expression = n.parse(equation);

            result = calc(expression);
        }
        return new ResponseEntity<>(result.toString(), HttpStatus.valueOf(200));
    }

    private static String calc(List<String> postfix) {
        Deque<Integer> stack = new ArrayDeque<Integer>();
        for (String x : postfix) {
            if ("+".equals(x)) {
                stack.push(stack.pop() + stack.pop());
            } else if ("=".equals(x)) {
                Integer b = stack.pop();
                Integer a = stack.pop();
                stack.push(a - b);
            } else if ("*".equals(x)) {
                stack.push(stack.pop() * stack.pop());
            } else if ("/".equals(x)) {
                int b = stack.pop();
                int a = stack.pop();
                stack.push(a / b);
            } else stack.push(Integer.valueOf(x));
        }

        return String.valueOf(stack.pop());
    }
}
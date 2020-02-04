package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Controller
public class ResultController {
    private Map<String, String> variables;
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session) {
        try {
            String equation_str = String.valueOf(session.getAttribute("equation"));
            Map<String, String> variables = new HashMap<>();
            for (int i=0; i < equation_str.length(); ++i) {
                if (equation_str.charAt(i) >= 'a' && equation_str.charAt(i) <= 'z') {
                    String varNameStr = String.valueOf(equation_str.charAt(i));
                    String varValueStr = String.valueOf(session.getAttribute(varNameStr));
                    variables.put(varNameStr, varValueStr);
                }
            }
            this.variables = variables;
            return new ResponseEntity<>(
                    Integer.toString(calculate(equation_str)),
                    HttpStatus.valueOf(200)
            );
        } catch (NullPointerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(409));
        }
    }

    //

    public Integer calculate(String str) {
        String strWithoutSpaces = str.replace(" ", "");
        String RPN = expressionToRPN(strWithoutSpaces);
        Stack<Integer> result = new Stack<>();
        for (int i=0; i < RPN.length(); ++i) {
            if (RPN.charAt(i) >= '0' && RPN.charAt(i) <= '9') {
                result.push(RPN.charAt(i) - '0');
            }
            else if (isVariable(RPN.charAt(i))) {
                result.push(placeVariable(RPN.charAt(i)));
            }
            else if (isOperator(RPN.charAt(i))) {
                result.push( doOperator(result.pop(), result.pop(), RPN.charAt(i)) );
            }
        }
        return result.pop();
    }

    private Integer placeVariable(char c) {
        String value = variables.get(String.valueOf(c));
        if (value.charAt(0) >= 'a' && value.charAt(0) <= 'z') {
            return placeVariable(value.charAt(0));
        }
        return Integer.parseInt(value);
    }

    private int priority(Character c) {
        if (c.equals('*')) {
            return 3;
        }
        if (c.equals('/')) {
            return 3;
        }
        if (c.equals('+') || c.equals('-')) {
            return 2;
        }
        if (c.equals('(')) {
            return 1;
        }
        if (c.equals(')')) {
            return -1;
        }
        else return 0;
    }

    private String expressionToRPN(String str) {
        StringBuilder current = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        for (int i=0; i < str.length(); i++) {
            if (priority(str.charAt(i)) == 0) {
                current.append(str.charAt(i));
            } else if (str.charAt(i) == '(') {
                operators.push(str.charAt(i));
            } else if (priority(str.charAt(i)) >= 2) {
                if (!(operators.isEmpty()) && priority(operators.peek()) >= priority(str.charAt(i))) {
                    current.append(operators.pop());
                }
                operators.push(str.charAt(i));
            } else if (str.charAt(i) == ')') {
                while (operators.peek() != '(') {
                    current.append(operators.pop());
                }
                operators.pop();
            }
        }
        while(!operators.isEmpty()) {
            current.append(operators.pop());
        }
        return String.valueOf(current);
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isVariable(Object element) {
        return variables.containsKey(String.valueOf(element)) || (element instanceof Integer);
    }

    private Integer doOperator(int right, int left, char operator) {
        if (operator == '+')
            return left + right;
        else if (operator == '-')
            return left - right;
        else if (operator == '*')
            return left * right;
        else if (operator == '/')
            return left / right;
        else
            throw new IllegalArgumentException();
    }
}
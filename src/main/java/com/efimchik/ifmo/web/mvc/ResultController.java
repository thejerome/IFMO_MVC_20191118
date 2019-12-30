package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/calc/result")
public class ResultController {
    @GetMapping()
    public ResponseEntity<String> doGet(HttpSession sesh) {
        Map<String, Object> params = (HashMap<String, Object>) sesh.getAttribute("vars");

        if (sesh.getAttribute("equation") == null) {
            return new ResponseEntity<>("No equation", HttpStatus.CONFLICT);
        }
        else {
            String equation = (String) sesh.getAttribute("equation");
            if (hasVars((String) sesh.getAttribute("equation")) == params.size()) {
                while (hasVars(equation) > 0) {
                    StringBuilder withValues = new StringBuilder();
                    for (int i = 0; i < equation.length(); i++) {
                        char elem = equation.charAt(i);
                        if (Character.isLetter(elem)) {
                            String result = params.get(Character.toString(elem)).toString();
                            withValues.append(result);
                        } else {
                            withValues.append(elem);
                        }
                    }
                    equation = withValues.toString();
                }
                ArrayDeque<String> rpn = shuntingYard(equation);
                System.out.println(equation);
                return new ResponseEntity<>(String.valueOf(calculate(rpn)), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("No vars", HttpStatus.CONFLICT);
            }
        }
    }

    private ArrayDeque<String> shuntingYard(String infix) {
        ArrayDeque<String> postfix = new ArrayDeque<>();
        ArrayDeque<String> operStack = new ArrayDeque<>();
        StringBuilder temp = new StringBuilder();
        int lastOper = 0;
        boolean negative = false;
        char token;
        String top;

        for (int i = 0; i < infix.length(); i++) {
            token = infix.charAt(i);
            if (Character.isDigit(token)) {
                temp.append(token);
            }
            if (!Character.isDigit(token) || i == infix.length() - 1) {
                if (negative) {
                    postfix.offerLast("-" + temp.toString());
                    negative = false;
                }
                else {
                    postfix.offerLast(temp.toString());
                }
                temp.delete(0, temp.length());
            }

            if (isOper(token)) {
                if ((lastOper == i-1 || operStack.peekLast() == "(") && token == '-' || token == '-' && i == 0) {
                    negative = true;
                }
                else {
                    while (operStack.peekLast() != null && precedence(token) <= precedence(operStack.peekLast().charAt(0)) &&
                            !operStack.peekLast().equals("(")) {
                        postfix.offerLast(operStack.pollLast());
                    }
                    operStack.offerLast(Character.toString(token));
                    lastOper = i;
                }
            }

            if (token == '(') {
                operStack.offerLast(Character.toString(token));
            }

            if (token == ')') {
                top = operStack.peekLast();
                while (!"(".equals(top)) {
                    postfix.offerLast(operStack.pollLast());
                    top = operStack.peekLast();
                }
                operStack.pollLast();
            }
        }

        while (!operStack.isEmpty()) {
            postfix.offerLast(operStack.pollLast());
        }
        return postfix;
    }

    private int precedence(char oper) {
        if (oper == '/' || oper == '*') {
            return 2;
        }
        else {
            return 1;
        }
    }

    private boolean isOper(char input) {
        return input == '-' || input == '+' || input == '*' || input == '/';
    }

    private int calculate(ArrayDeque<String> input) {
        String elem;
        Integer operand1;
        Integer operand2;
        Integer value;
        ArrayDeque<Integer> result = new ArrayDeque<>();
        for (String s : input) {
            elem = s;
            if (elem.length() == 1 && isOper(elem.charAt(0))) {
                operand2 = result.pollLast();
                operand1 = result.pollLast();
                if ("+".equals(elem)) {
                    value = operand1 + operand2;
                    result.offerLast(value);
                }
                if ("-".equals(elem)) {
                    value = operand1 - operand2;
                    result.offerLast(value);
                }
                if ("*".equals(elem)) {
                    value = operand1 * operand2;
                    result.offerLast(value);
                }
                if ("/".equals(elem)) {
                    value = operand1 / operand2;
                    result.offerLast(value);
                }
            }
            else {
                if (!"".equals(elem)) {
                    result.offerLast(Integer.valueOf(elem));
                }
            }
        }
        // may produce npe but it won't
        return result.pollLast();
    }

    private int hasVars(String input) {
        String temp= "";

        for (int i = 0; i < input.length(); i++) {
            if(temp.indexOf(input.charAt(i)) == -1 && Character.isLetter(input.charAt(i))){
                temp = temp + input.charAt(i);
            }
        }
        return temp.length();
    }
}
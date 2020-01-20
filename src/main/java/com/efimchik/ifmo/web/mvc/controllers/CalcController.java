package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.xml.ws.spi.http.HttpContext;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;


@RestController
public class CalcController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession httpSession){
        String equation = (String) httpSession.getAttribute("equation");
        if (equation != null) {
            equation = equation.replaceAll("\\s", "");
            try {
                String result = buildRPN(equation);
                return new ResponseEntity<>(solve(result, httpSession), HttpStatus.valueOf(200));
            } catch (IllegalArgumentException e){
                return new ResponseEntity<>("", HttpStatus.valueOf(409));
            }
        } else {
            return new ResponseEntity<>("", HttpStatus.valueOf(409));
        }
    }

    private static boolean isOperator(String c) {
        return  "-".equals(c) || "+".equals(c) || "*".equals(c) || "/".equals(c);
    }

    private static boolean isOperand(String c) {
        return !(isOperator(c)) && !("(".equals(c)) && !(")".equals(c));
    }

    private static boolean higherPriority(String op1, String op2) {
        char opearator1 = op1.charAt(0);
        char opearator2 = op2.charAt(0);
        return (opearator1 == '/' || opearator1 == '*') || ((opearator2 == '+' || opearator2 == '-') && (opearator1 == '+' || opearator1 == '-'));

    }

    private String solve(String equation, HttpSession httpSession) {
        Stack<String> calc = new Stack<>();
        StringTokenizer st = new StringTokenizer(equation, " ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (isOperator(token))
                calc.push(Integer.toString(calculate(calc.pop(), calc.pop(), token)));
            else if (isNumber(token)) {
                calc.push(token);
            } else if (isOperand(token)) {
                String s = (String) httpSession.getAttribute(token);
                if (s == null) {
                    throw new IllegalArgumentException();
                }
                while (!isNumber(s)) {
                    s = (String) httpSession.getAttribute(s);
                    if (s == null) {
                        throw new IllegalArgumentException();
                    }
                }
                calc.push(s);
            }
        }
        return calc.pop();
    }

    private boolean isNumber(String s) {
        if (s.charAt(0) == '-' && s.length() == 1)
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)) && !(i == 0 && s.charAt(i) == '-'))
                return false;
            else if (i == 0 && s.charAt(i) == '-') {
            }
        }
        return true;
    }

    private void forOperator (String c, ArrayDeque<String> deque, StringBuilder operands){
        if (deque.peek() != null) {
            while (higherPriority(deque.peek(), c)) {
                String op = deque.pop();
                operands.append(op).append(' ');
                if (deque.peek() == null)
                    break;
            }
        }
    }

    private String buildRPN(String equation) {
        StringBuilder operands = new StringBuilder();
        ArrayDeque<String> deque = new ArrayDeque<>();
        for (int i = 0; i < equation.length(); i++) {
            String c = Character.toString(equation.charAt(i));
            if (isOperand(c))
                operands.append(c).append(' ');
            else if (isOperator(c)) {
                forOperator(c, deque, operands);
                deque.push(c);
            } else if ("(".equals(c))
                deque.push(c);
            else if (")".equals(c)) {
                while (!Objects.equals(deque.peek(), "(")) {
                    String op = deque.pop();
                    operands.append(op).append(' ');
                    if (deque.peek() == null)
                        break;
                }
                deque.pop();
            }
        }
        while (deque.peek() != null) {
            String op = deque.pop();
            operands.append(op).append(' ');
        }
        operands.setLength(operands.length() - 1);
        return operands.toString();
    }

    private static int calculate (String num1, String num2, String op) {
        char operator = op.charAt(0);
        int number1 = Integer.parseInt(num2);
        switch (operator) {
            case '*':
                return number1 * Integer.parseInt(num1);
            case '/':
                return number1 / Integer.parseInt(num1);
            case '+':
                return number1 + Integer.parseInt(num1);
            case '-':
                return number1 - Integer.parseInt(num1);
            default:
                return 0;
        }
    }

}

package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.StringTokenizer;

@Controller
public class GetResultController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getCalcResult(HttpSession session) {
        String equation = (String) session.getAttribute("equation");
        if (equation == null) {
            return new ResponseEntity<>("equation is not define", HttpStatus.valueOf(409));
        }
        String result;
        equation = equation.replaceAll("\\s", "");
        String equationInRPN = getReversePolishNotation(equation);
        try {
            result = getResult(session, equationInRPN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("some variable is not defined", HttpStatus.valueOf(409));
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf(200));
    }


    private String getResult(HttpSession session, String equationInRPN) {
        if (equationInRPN == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(equationInRPN, ".");
        ArrayDeque<String> calc = new ArrayDeque<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (isNumber(token)) {
                calc.push(token);
                continue;
            }
            if (isVar(token)) {
                String valueOfVar;
                valueOfVar = getValueOfVar(session, token);
                calc.push(valueOfVar);
                continue;
            }
            if (isOperator(token)) {
                String rhs = calc.pop();
                String lhs = calc.pop();
                calc.push(calcSimpleEquation(Integer.parseInt(lhs), Integer.parseInt(rhs), token));
            }
        }
        return calc.getFirst();
    }

    private String getReversePolishNotation(String equation) {
        if (equation == null)
            return null;
        StringBuilder equationInRPN = new StringBuilder();
        ArrayDeque<String> opStack = new ArrayDeque<>();
        StringTokenizer st = new StringTokenizer(equation, "+-*/()", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (isNumber(token) || isVar(token)) {
                equationInRPN.append(token);
                equationInRPN.append('.');
            }
            if ("(".equals(token)) {
                opStack.push(token);
            }
            if (")".equals(token)) {
                while (!Objects.equals(opStack.peek(), "(")) {
                    String op = opStack.pop();
                    equationInRPN.append(op);
                    equationInRPN.append('.');
                }
                opStack.pop();
            }
            if (isOperator(token)) {
                if (opStack.peek() != null) {
                    while (priorityOfOperator(opStack.peek()) >= priorityOfOperator(token)) {
                        String op = opStack.pop();
                        equationInRPN.append(op);
                        equationInRPN.append('.');
                        if (opStack.peek() == null)
                            break;
                    }
                }
                opStack.push(token);
            }
        }
        while (opStack.peek() != null) {
            String op = opStack.pop();
            equationInRPN.append(op);
            equationInRPN.append('.');
        }
        equationInRPN.setLength(equationInRPN.length() - 1);
        return equationInRPN.toString();
    }

    private String calcSimpleEquation(int lhs, int rhs, String op) {
        char c = op.charAt(0);
        switch (c) {
            case '+':
                return String.valueOf(lhs + rhs);
            case '-':
                return String.valueOf(lhs - rhs);
            case '*':
                return String.valueOf(lhs * rhs);
            case '/':
                return String.valueOf(lhs / rhs);
            default:
                return "";
        }
    }

    private boolean isVar(String str) {
        if (str.length() != 1)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!(str.charAt(i) >= 'a' && str.charAt(i) <= 'z'))
                return false;
        }
        return true;
    }

    private boolean isNumber(String str) {
        if (str.charAt(0) == '-' && str.length() == 1)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (i == 0 && str.charAt(i) == '-')
                continue;
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String getValueOfVar(HttpSession session, String a) {
        String s = (String) session.getAttribute(a);
        if (s == null) {
            throw new IllegalArgumentException();
        }
        while (!isNumber(s)) {
            s = (String) session.getAttribute(s);
            if (s == null) {
                throw new IllegalArgumentException();
            }
        }
        return s;

    }

    private boolean isOperator(String op) {
        char c = op.charAt(0);
        return c == '+' || c == '-' || c == '/' || c == '*';
    }

    private int priorityOfOperator(String op) {
        char c = op.charAt(0);
        if (c == '*' || c == '/')
            return 2;
        else if (c == '+' || c == '-')
            return 1;
        else
            return 0;
    }

}
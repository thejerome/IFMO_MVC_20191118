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
public class GetCalculateController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getCalcResult(HttpSession session) {
        if (session.getAttribute("equation") == null) {
            return new ResponseEntity<>("advise you to double-check, equation is not define", HttpStatus.valueOf(409));
        }
        String result;
        String equationInRPN = getReversePolishNotation(((String) session.getAttribute("equation")).replaceAll("\\s", ""));
        try {
            result = getResult(session, equationInRPN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("advise you to double-check, some variable is not defined", HttpStatus.valueOf(409));
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf(200));
    }


    private String getResult(HttpSession session, String tokenStr) {
        if (tokenStr == null) return null;
        StringTokenizer str = new StringTokenizer(tokenStr, ".");
        ArrayDeque<String> calc = new ArrayDeque<>();
        do {
            String tokenizer = str.nextToken();
            if (numberValue(tokenizer)) {
                calc.push(tokenizer);
                continue;
            }
            if (varValue(tokenizer)) {
                String a = tokenizer;
                while (a.charAt(0) >= 'a' && a.charAt(0) <= 'z'){
                    a = (String) session.getAttribute(a);
                    if (a == null)
                        throw new IllegalArgumentException();
                }
                if (tokenizer.charAt(0) >= 'a' && tokenizer.charAt(0) <= 'z'){
                    calc.push(a);
                } else {
                    calc.push(tokenizer);
                }
                continue;
            }
            if (isOperator(tokenizer)) {
                String op2 = calc.pop();
                String op1 = calc.pop();
                calc.push(String.valueOf(calcSimpleEquation(Integer.parseInt(op1), Integer.parseInt(op2), tokenizer)));
            }
        } while (str.hasMoreTokens());
        return calc.pop();
    }

    private String getReversePolishNotation(String equation) {
        if (equation == null) return null;
        StringBuilder rpn = new StringBuilder();
        ArrayDeque<String> signStack = new ArrayDeque<>();
        StringTokenizer st = new StringTokenizer(equation, "+-*/()", true);
        do {
            String token = st.nextToken();
            int i = 0;
            boolean numberOrVarValue = true;
            do {
                if (!Character.isDigit(token.charAt(i)) && !(token.charAt(i) >= 'a' && token.charAt(i) <= 'z')) {
                    numberOrVarValue = false;
                }
                i++;
            } while (i < token.length());

            if ("(".equals(token)) {
                signStack.push(token);
            } else if (")".equals(token)) {
                do {
                    String op = signStack.pop();
                    rpn.append(op);
                    rpn.append('.');
                } while (!Objects.equals(signStack.peek(), "("));
                signStack.pop();
            } else if (numberOrVarValue) {
                rpn.append(token);
                rpn.append('.');
            } else if (isOperator(token)) {
                if (signStack.peek() != null) {
                    while (priorityOfOperator(signStack.peek()) >= priorityOfOperator(token)) {
                        String op = signStack.pop();
                        rpn.append(op);
                        rpn.append('.');
                        if (signStack.peek() == null)
                            break;
                    }
                }
                signStack.push(token);
            }
        } while (st.hasMoreTokens());
        do {
            String op = signStack.pop();
            rpn.append(op);
            rpn.append('.');
        } while (signStack.peek() != null);
        rpn.setLength(rpn.length() - 1);
        return rpn.toString();
    }

    private boolean numberValue(String str) {
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

    private boolean varValue(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!(str.charAt(i) >= 'a' && str.charAt(i) <= 'z') || str.length() != 1) {
                return false;
            }
        }
        return true;
    }

    private int calcSimpleEquation(int op1, int op2, String op) {
        char c = op.charAt(0);
        switch (c) {
            case '+':
                return op1 + op2;
            case '-':
                return op1 - op2;
            case '*':
                return op1 * op2;
            case '/':
                return op1 / op2;
            default:
                return 0;
        }
    }

    private boolean isOperator(String op) {
        char c = op.charAt(0);
        return c == '+' || c == '-' || c == '/' || c == '*';
    }

    private int priorityOfOperator(String op) {
        char c = op.charAt(0);
        if (c == '*' || c == '/') return 2;
        else if (c == '+' || c == '-') return 1;
        else return 0;
    }

}
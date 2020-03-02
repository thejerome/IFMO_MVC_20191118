package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayDeque;
import java.util.Deque;

@Controller
@RequestMapping(value = "/calc/result")
public class ResultController {
    private final static String SESSION_VARIABLE = "variable_";
    private final static String SESSION_EXPRESSION = "expression";

    @GetMapping
    @ResponseBody
    public ResponseEntity<String> doGet(HttpSession session) {
        String expression = getExpression(session);
        if (expression.isEmpty() || !checkVariables(session, expression)) {
            return new ResponseEntity<>("", HttpStatus.valueOf(StatusCode.UNCALCULATED.getCode()));
        }
        int result;
        result = evaluate(expression, session);
        return new ResponseEntity<>(String.valueOf(result), HttpStatus.valueOf(StatusCode.CALCULATED.getCode()));
    }

    private int evaluate(String expression, HttpSession session) {
        char[] tokens = expression.toCharArray();
        Deque<Integer> values = new ArrayDeque<>();
        Deque<Character> ops = new ArrayDeque<>();

        for (char token : tokens) {
            if (token == ' ') {
                continue;
            }

            if (token >= '0' && token <= '9') {
                values.offerFirst(Character.getNumericValue(token));
            } else if (token == '(') {
                ops.offerFirst(token);
            } else if (token == ')') {
                while (ops.getFirst() != '(') {
                    values.offerFirst(applyOperation(ops.removeFirst(), values.removeFirst(), values.removeFirst()));
                }
                ops.pollFirst();
            } else if (token == '+' || token == '-' || token == '*' || token == '/') {
                while (!ops.isEmpty() && hasPrecedence(token, ops.peekFirst())) {
                    values.offerFirst(applyOperation(ops.pollFirst(), values.removeFirst(), values.removeFirst()));
                }
                ops.offerFirst(token);
            } else {
                values.offerFirst(getVariableValue(session, Character.toString(token)));
            }
        }

        while (!ops.isEmpty()) {
            values.offerFirst(applyOperation(ops.pollFirst(), values.removeFirst(), values.removeFirst()));
        }

        return values.removeFirst();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    private int applyOperation(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
            default:
                return 0;
        }
    }

    private int getVariableValue(HttpSession session, String variable) {
        String value = (String) session.getAttribute(SESSION_VARIABLE + variable);
        if (value.matches("[a-z]")) {
            return getVariableValue(session, value);
        }
        return Integer.parseInt(value);
    }

    private String getExpression(HttpSession session) {
        if (session.getAttribute(SESSION_EXPRESSION) != null) {
            return (String) session.getAttribute(SESSION_EXPRESSION);
        }
        return "";
    }

    private boolean checkVariables(HttpSession session, String expression) {
        String[] vars = expression.split("[()\\s\\d*\\-+/]");
        for (String var : vars) {
            if (!var.isEmpty() && session.getAttribute(SESSION_VARIABLE + var) == null) {
                return false;
            }
        }
        return true;
    }
}

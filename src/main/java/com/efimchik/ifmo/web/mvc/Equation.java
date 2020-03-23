package com.efimchik.ifmo.web.mvc;

import java.util.*;

public class Equation {
    private Map<String, String> variables;

    public Equation(Map<String, String> variables) {
        this.variables = variables;
    }

    public Integer calculate(String string) {
        String stringNoSpaces = string.replace(" ", "");
        String postfix = toPostfix(stringNoSpaces);

        Stack<Integer> result = new Stack<Integer>();
        for (int i = 0; i < postfix.length(); i++) {
            if (postfix.charAt(i) >= '0' && postfix.charAt(i) <= '9') {
                result.push(postfix.charAt(i) - '0');
            } else if (isVariable(postfix.charAt(i))) {
                result.push(replaceVariable(postfix.charAt(i)));
            } else if (isOperator(postfix.charAt(i))) {
                result.push(doOperator(result.pop(), result.pop(), postfix.charAt(i)));
            }
        }

        return result.pop();
    }

    private Integer replaceVariable(char c) {
        String value = variables.get(String.valueOf(c));
        if (value.charAt(0) >= 'a' && value.charAt(0) <= 'z') {
            return replaceVariable(value.charAt(0));
        }
        return Integer.parseInt(value);
    }

    private int priority(char c) {
        switch (c) {
            case '*':
            case '/':
                return 3;
            case '+':
            case '-':
                return 2;
            case '(':
                return 1;
            case ')':
                return -1;
            default:
                return 0;
        }
    }

    private String toPostfix(String string) {
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<Character>();

        for (int i = 0; i < string.length(); i++) {
            if (priority(string.charAt(i)) == 0) {
                result.append(string.charAt(i));
            } else if (string.charAt(i) == '(') {
                operators.push(string.charAt(i));
            } else if (priority(string.charAt(i)) >= 2) {
                if (!operators.isEmpty() && priority(operators.peek()) >= priority(string.charAt(i))) {
                    result.append(operators.pop());
                }
                operators.push(string.charAt(i));
            } else if (string.charAt(i) == ')') {
                while (operators.peek() != '(') {
                    result.append(operators.pop());
                }
                operators.pop();
            }
        }

        while (!operators.isEmpty()) {
            result.append(operators.pop());
        }

        return result.toString();
    }

    private boolean isOperator(char c) {
        return (c == '+' ||
                c == '-' ||
                c == '*' ||
                c == '/');
    }

    private boolean isVariable(Object object) {
        return variables.containsKey(object.toString()) || (object instanceof Integer);
    }

    private Integer doOperator(int right, int left, char operator) {
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                return left / right;
            default:
                throw new IllegalArgumentException();
        }
    }
}
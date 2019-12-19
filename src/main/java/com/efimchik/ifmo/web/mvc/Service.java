package com.efimchik.ifmo.web.mvc;

import javax.servlet.http.HttpSession;
import java.util.Stack;

abstract class Service {
    private static String expressionParser(String expr) {
        StringBuilder current = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        int priority;
        for (int i = 0; i < expr.length(); i++) {
            priority = getPriority(expr.charAt(i));
            if (priority == 0) current.append((expr.charAt(i)));
            else if (priority == 1) stack.push((expr.charAt(i)));
            else if (priority > 1) {
                current.append(' ');
                if ((expr.charAt(i) == ('-')) && (i == 0)) {
                    current.append("0 ");
                }
                while (!stack.empty()) {
                    if (getPriority(stack.peek()) >= priority) current.append(stack.pop());
                    else break;
                }
                stack.push((expr.charAt(i)));
            } else if (priority == -1) {
                current.append(' ');
                while (getPriority(stack.peek()) != 1) current.append(stack.pop());
                stack.pop();
            }
        }
        while (!stack.empty()) current.append(stack.pop());
        return current.toString();
    }


    private static Integer answer(String rpn) {
        StringBuilder operand = new StringBuilder();
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < rpn.length(); i++) {

            if (rpn.charAt(i) == ' ') continue;
            if (getPriority(rpn.charAt(i)) == 0) {
                while (rpn.charAt(i) != ' ' && getPriority(rpn.charAt(i)) == 0) {
                    operand.append(rpn.charAt(i++));
                    if (i == rpn.length()) break;
                }
                stack.push(Integer.parseInt(operand.toString()));
                operand = new StringBuilder();
            }
            if (getPriority(rpn.charAt(i)) > 1) {
                Integer a = stack.pop();
                Integer b = stack.pop();

                if (rpn.charAt(i) == '+') stack.push(b + a);
                else if (rpn.charAt(i) == '-') stack.push(b - a);
                else if (rpn.charAt(i) == '*') stack.push(b * a);
                else if (rpn.charAt(i) == '/') stack.push(b / a);
            }
        }
        return stack.pop();

    }

    private static int getPriority(char token) {
        if (token == '*' || token == '/') return 3;
        else if (token == '+' || token == '-') return 2;
        else if (token == '(') return 1;
        else if (token == ')') return -1;
        else return 0;
    }

    static String calculate(String Expression) {
        System.out.println();
        return String.valueOf(answer(expressionParser(Expression)));
    }

    static boolean isNotEquation(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            if ('*' <= expression.charAt(i) && '/' >= expression.charAt(i)) return false;
        }
        return true;
    }

    static boolean isVar(String value) {
        if (value.charAt(0) >= 'a' && value.charAt(0) <= 'z')
            return true;
        else
            return (Integer.parseInt(value) <= 10000 && Integer.parseInt(value) >= -10000);
    }

    static String map(HttpSession session) {
        String expression = (String) session.getAttribute("equation");
        for (int i = 0; i < 10; i++) {
            for (char symbol : expression.toCharArray()) {
                if (symbol >= 'a' && symbol <= 'z') {
                    if (session.getAttribute(String.valueOf(symbol)) != null)
                        expression = expression.replace(String.valueOf(symbol), String.valueOf(session.getAttribute(String.valueOf(symbol))));
                    else break;
                }
            }
        }
        return expression;
    }

}

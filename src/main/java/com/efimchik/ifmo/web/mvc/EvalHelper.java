package com.efimchik.ifmo.web.mvc;

import java.util.Map;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalHelper {
    public static boolean isInteger(String stringToCheck) {
        try {
            Integer.parseInt(stringToCheck);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')';
    }

    public static int getPrecedence(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    public static boolean isOperand(char ch) {
        return (ch >= 'a' && ch <= 'z') ||
                (ch >= 'A' && ch <= 'Z') ||
                (ch >= '0' && ch <='9');
    }

    public static String infixToPostfix(String infix) {
        Stack<Character> stack = new Stack<Character>();
        StringBuilder postfix = new StringBuilder(infix.length());
        char c;

        for (int i = 0; i < infix.length(); i++) {
            c = infix.charAt(i);

            if (EvalHelper.isOperand(c)) {
                postfix.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek() != '(')
                    return null;
                else if(!stack.isEmpty())
                    stack.pop();
            } else if (isOperator(c)) {
                if (!stack.isEmpty() &&
                        getPrecedence(c) <= getPrecedence(stack.peek())) {
                    postfix.append(stack.pop());
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        return postfix.toString();
    }

    public static String delimitString(String equation) {
        StringJoiner joiner = new StringJoiner(",");
        for (char ch: equation.toCharArray()) {
            joiner.add(Character.toString(ch));
        }
        return joiner.toString();
    }

    private static int performOperation(int leftOperand,
                                        int rightOperand,
                                        String operator) {
        switch(operator) {
            case "+":
                return leftOperand + rightOperand;
            case "-":
                return leftOperand - rightOperand;
            case "*":
                return leftOperand * rightOperand;
            case "/":
                return leftOperand / rightOperand;
            default:
                return 0;
        }
    }

    public static int evaluate(String equation) {
        String[] arrOfStr = equation.split(",");
        Stack<Integer> stack = new Stack<Integer>();

        for (String a : arrOfStr) {
            if (EvalHelper.isInteger(a)) {
                stack.push(Integer.parseInt(a));
            } else {
                int rightOperand = stack.pop();
                int leftOperand = stack.pop();
                int res = performOperation(leftOperand, rightOperand, a);
                stack.push(res);
            }
        }
        return stack.pop();
    }

    public static String getNumericEquation(String equation,
                                     Map<String, String> map) {
        StringBuilder numEquation = new StringBuilder();
        String parameter = "";

        for(int i = 0; i < equation.length(); ++i) {
            if(equation.charAt(i) >= 'a' && equation.charAt(i) <= 'z') {
                if(map.containsKey(Character.toString(equation.charAt(i)))) {
                    parameter = map.get(Character.toString(equation.charAt(i)));
                } else {
                    return null;
                }
                while(parameter.charAt(0) >= 'a' && parameter.charAt(0) <= 'z') {
                    if(map.containsKey(Character.toString(parameter.charAt(0)))) {
                        parameter = map.get(Character.toString(parameter.charAt(0)));
                    }
                    else {
                        return null;
                    }
                }
                numEquation.append(parameter);
            } else {
                numEquation.append(equation.charAt(i));
            }
        }
        System.out.println(numEquation.toString());
        return numEquation.toString();
    }
    public static boolean isExpression(String s) {
        Pattern pattern = Pattern.compile("^[(]*[a-z0-9]?([-+/*][(]*[a-z0-9][)]*)*$");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    public static boolean isValid(String value) {
        Pattern pattern = Pattern.compile("^[a-z]$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}

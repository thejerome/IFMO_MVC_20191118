package com.efimchik.ifmo.web.mvc.helpers;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static boolean isValid(String variable) {
        char c = variable.charAt(0);
        if (c >= 'a' && c <= 'z')
            return true;
        try {
            return Integer.parseInt(variable) > -10000 && Integer.parseInt(variable) < 10000;
        } catch (Exception e){
            return false;
        }
    }
    public static boolean isExpression(String s) {
        Pattern pattern = Pattern.compile("^[(]*[a-z0-9]?([-+/*][(]*[a-z0-9][)]*)*$");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    public static boolean isValue(String val) {
        Pattern pattern = Pattern.compile("^[a-z]$");
        Matcher matcher = pattern.matcher(val);
        return matcher.matches();
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    public static boolean isAnyOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')';
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static int precedence(char ch) {
        if (ch == '+' || ch == '-')
            return 1;
        return 2;
    }

    public static int calculate(int left, int right, char op) {
        switch (op) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                return left / right;
            default:
                return 0;
        }
    }

    public static String toPostfix(String infix) {
        char temp;
        System.out.println("infix: " + infix);
        StringBuilder result = new StringBuilder();
        StringBuilder hlp = new StringBuilder();

        for (int i = 0; i < infix.length(); i++) {
            if ('(' == infix.charAt(i)) {
                hlp.append(infix.charAt(i));
            } else if (')' == infix.charAt(i)) {
                temp = hlp.substring(hlp.length() - 1).charAt(0);
                while ('(' != temp) {
                    result.append(" ").append(temp);
                    hlp.setLength(hlp.length() - 1);
                    temp = hlp.substring(hlp.length() - 1).charAt(0);
                }
                hlp.setLength(hlp.length() - 1);
            } else if (isOperator(infix.charAt(i))) {
                while (hlp.length() != 0) {
                    temp = hlp.substring(hlp.length() - 1).charAt(0);
                    if (isOperator(temp) && (precedence(infix.charAt(i)) <= precedence(temp))) {
                        result.append(" " + temp + " ");
                        hlp.setLength(hlp.length() - 1);
                    } else {
                        break;
                    }
                }
                result.append(" ");
                hlp.append(infix.charAt(i));
            } else
                result.append(infix.charAt(i));
        }
        while (hlp.length() != 0) {
            result.append(" " + hlp.substring(hlp.length() - 1));
            hlp.setLength(hlp.length() - 1);
        }

        if (infix.charAt(0) == '-') {
            int index = result.lastIndexOf("-");
            result.deleteCharAt(index).deleteCharAt(index-1);
            result.insert(1,'-');
        }
        System.out.println("!!!!" + result.toString() + "!!!!");
        return result.toString();
    }

    public static int calculation(String st) {
        System.out.println("string: " + st);
        int left;
        Stack<Integer> stack = new Stack<>();
        String equation = toPostfix(st);
        StringTokenizer stringTokenizer = new StringTokenizer(equation);
        String tmp;
        while (stringTokenizer.hasMoreTokens()) {
            tmp = stringTokenizer.nextToken();
            if (isOperator(tmp.charAt(0)) && 1 == tmp.length()) {
                int right = stack.pop();
                left = stack.pop();
                left = calculate(left, right, tmp.charAt(0));
                stack.push(left);
            } else {
                left = Integer.parseInt(tmp);
                stack.push(left);
            }
        }
        return stack.pop();
    }
}

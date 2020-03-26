package com.efimchik.ifmo.web.mvc;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

public class EquationSolver {
    public static String getPostfixEquation(String equation) {
        Stack<Character> tempStack = new Stack<>();
        StringBuilder newEquation = new StringBuilder(equation.length());
        for (char token: equation.toCharArray()) {
            if (isAlpha(token) || isNumeric(Character.toString(token)))
                newEquation.append(token);
            else if (token == ')') {
                while (tempStack.peek() != '(') newEquation.append(tempStack.pop());
                if (tempStack.peek() != '(')
                    return null;
                else
                    tempStack.pop();
            }
            else if (token == '(') tempStack.push(token);
            else if (!isNotOpToCalc(token)) {
                if (!tempStack.isEmpty() && (prior(token) <= prior(tempStack.peek())))
                    newEquation.append(tempStack.pop());
                tempStack.push(token);
            }
        }
        while (!tempStack.isEmpty()) newEquation.append(tempStack.pop());
        return newEquation.toString();
    }

    public static String getResult(String notPostfixEquation, Map<String, String> map) throws NoSuchElementException {
        String equation = getPostfixEquation(notPostfixEquation);
        Stack<Integer> tempStack = new Stack<>();
        if (equation != null) {
            for (char token : equation.toCharArray()) {
                if (isAlpha(token)) tempStack.push(Integer.parseInt(resolve(token, map)));
                else if (isNumeric(token)) tempStack.push(Integer.parseInt(String.valueOf(token)));
                else tempStack.push(calc(tempStack.pop(), tempStack.pop(), token));
            }
        }
        return String.valueOf(tempStack.pop());
    }

    private static Integer calc(int r, int l, char o) {
        if (isNotOpToCalc(o)) return null;
        int res;
        if ('+' == o) return l + r;
        else if ('-' == o) res = l - r;
        else if ('*' == o) res = l * r;
        else res =  l / r;
        return res;
    }

    private static String resolve(char token, Map<String, String> map) throws NoSuchElementException {
        try {
            if (isAlpha(token)) {
                String val = map.get(Character.toString(token));
                if (isNumeric(val)) return val;
                return resolve(val.charAt(0), map);
            }
            return map.get(token);
        } catch (Exception e) {
            throw new NoSuchElementException();
        }
    }

    private static int prior(char o) {
        if (isNotOpToCalc(o)) return 0;
        if (o == '+' || o == '-') return 3;
        else return 5;
    }

    public static boolean isNumeric(String num) {
        try {
            Integer.parseInt(num);
            return true;
        } catch (Exception e) {return false;}
    }

    public static boolean isNumeric(char num) {
        try {
            Integer.parseInt(String.valueOf(num));
            return true;
        } catch (Exception e) {return false;}
    }

    private static boolean isNotOpToCalc(char ch) {
        return ch != '+' && ch != '-' && ch != '*' && ch != '/';
    }

    private static boolean isAlpha(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

}

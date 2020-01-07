package com.efimchik.ifmo.web.mvc;

import java.util.*;

public class CalcMethodUtil {
    private static String delimiters = "() +=*/";

    private static boolean isDelimiters (String variable) {
        int i = 0;

        while (i < delimiters.length()) {
            if (variable.charAt(0) == delimiters.charAt(i)) {
                return true;
            }
            i++;
        }
        return false;
    }


    private static int priority (String variable) {
        String openBracket = "(";
        String addition = "+";
        String subtraction = "-";

        if (openBracket.equals(variable)) {
            return 1;
        }
        if (addition.equals(variable) || subtraction.equals(variable)) {
            return 2;
        }
        return 3;
    }

    public static List<String> parse (String infixNotation) {
        List<String> postfix = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        StringTokenizer tokenizer = new StringTokenizer(infixNotation, delimiters, true);
        String current;
        while (tokenizer.hasMoreTokens()) {

            current = tokenizer.nextToken();
            String openBracket = "(";
            String closeBracket = ")";

            if (isDelimiters(current) && openBracket.equals(current)) {
                stack.push(current);
            } else if (isDelimiters(current) && closeBracket.equals(current)) {
                while (!stack.peek().equals(openBracket)) {
                    postfix.add(stack.pop());
                }
                stack.pop();

            } else if (isDelimiters(current) && closeBracket.equals(current) && !stack.isEmpty()) {
                postfix.add(stack.pop());
            } else if (isDelimiters(current)) {
                while (!stack.isEmpty() && (priority(stack.peek()) >= priority(current))) {
                    postfix.add(stack.pop());
                }
                stack.push(current);
            } else {
                postfix.add(current);
            }
        }

        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }
        return postfix;
    }
}

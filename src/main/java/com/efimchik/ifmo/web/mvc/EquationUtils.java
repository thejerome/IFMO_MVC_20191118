package com.efimchik.ifmo.web.mvc;

import java.util.*;

public class EquationUtils {

    private static String delimiters = "() +=*/";

    private static int priority (String variable) {
        if ("(".equals(variable)) {
            return 1;
        }
        if ("+".equals(variable) || "-".equals(variable)) {
            return 2;
        }
        return 3;
    }

    private static boolean isDelimiters (String variable) {
        for(int i = 0; i < delimiters.length(); i++){
            if (variable.charAt(0) == delimiters.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> parse (String infixNotation) {
        Stack<String> stack = new Stack<>();
        List<String> postfix = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(infixNotation, delimiters, true);
        String current;
        while (tokenizer.hasMoreTokens()) {
            current = tokenizer.nextToken();

            if (isDelimiters(current) && "(".equals(current)) {
                stack.push(current);
            } else if (isDelimiters(current) && ")".equals(current)) {
                while (!stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                stack.pop();
            } else if (isDelimiters(current) && ")".equals(current) && !stack.isEmpty()) {
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

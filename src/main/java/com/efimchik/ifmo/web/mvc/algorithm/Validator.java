package com.efimchik.ifmo.web.mvc.algorithm;

import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isValid(String expr) {
        return checkParentheses(expr) && checkExpr(expr);
    }

    public static boolean isValid(Map<String, String> vars) {
        String expr = vars.remove("expr");
        StringTokenizer tokenizer = new StringTokenizer(expr, "(+-*/)0123456789 ");
        while (tokenizer.hasMoreTokens()) {
            if (vars.get(tokenizer.nextToken()) == null) {
                return false;
            }
        }
        vars.put("expr", expr);
        return true;
    }

    public static int validateValue(String val) {
        try {
            int value = Integer.parseInt(val);
            if (value < -10000 || value > 10000) {
                return 403;
            }
            return 200;
        } catch (NumberFormatException e) {
            if (val != null && !val.isEmpty()) {
                if (Character.isAlphabetic(val.charAt(0))) {
                    return 200;
                }
            }
            return 403;
        }
    }

    private static boolean checkParentheses(String str) {
        Stack<Character> stack = new Stack<>();
        for (char c :
                str.toCharArray()) {
            if (c == ')') {
                if (stack.isEmpty()) {
                    return false;
                } else {
                    stack.pop();
                }
            } else if (c == '(') {
                stack.push(c);
            }
        }
        System.out.println("[+] Checked parentheses.");
        return stack.isEmpty();
    }

    private static boolean checkExpr(String expr) {
        Pattern isExpr = Pattern.compile("^\\(*[A-Za-z0-9]([+\\-*/]\\(*[A-za-z0-9]\\)*)*\\)*$");
        StringTokenizer tokenizer = new StringTokenizer(expr, "() ");
        StringBuilder builder = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            builder.append(tokenizer.nextToken());
        }
        String cleanExpression = builder.toString();
        System.out.println("[+] Checked expression.");
        return isExpr.matcher(cleanExpression).matches();
    }
}

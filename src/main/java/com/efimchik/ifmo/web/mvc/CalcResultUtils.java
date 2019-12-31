package com.efimchik.ifmo.web.mvc;

import javax.servlet.http.HttpSession;
import java.util.Stack;

public class CalcResultUtils {
    private static boolean isLetter(String s){
        for (int i = 0; i < s.length(); ++i){
            if (Character.isLetter(s.charAt(i))){
                return true;
            }
        }
        return false;
    }

    private static boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '/' || c == '*' || c =='('|| c ==')');
    }

    private static int getPriority(char sym) {
        switch (sym)
        {
            case '(': return 0;
            case ')': return 1;
            case '+': return 2;
            case '-': return 3;
            case '*':
            case '/':
                return 4;
            default: return 5;
        }
    }

    private static String getValue(HttpSession session1, String str) {
        String eq = (String) session1.getAttribute(str);
        if (null != eq) {
            while (isLetter(eq)) {
                eq = (String) session1.getAttribute(eq);
                if (eq == null) {
                    throw new IllegalArgumentException();
                }
            }
            return eq;
        } else {
            return null;
        }

    }

    public static String getExpression(String output, HttpSession session2)
    {
        if (output == null){
            return null;
        }

        StringBuilder expr = new StringBuilder();

        for (int i = 0; i < output.length(); i ++) {
            String symbol = Character.toString(output.charAt(i));
            String valueOfVar = getValue(session2, symbol);
            if (!isLetter(symbol)) {
                expr.append(symbol);
            }
            else if (valueOfVar == null) return null;
            else if (valueOfVar.charAt(0) == '-'){
                expr.append(valueOfVar.substring(1)).append("0000");
            }
            else {
                expr.append(valueOfVar);
            }
            expr.append(' ');
        }

        StringBuilder out = new StringBuilder();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expr.length(); i++)
            if (expr.charAt(i) >= '0' && expr.charAt(i) <= '9') {
                i = getLength(expr.toString(), out, i);
                out.append(" ");
                i--;
            } else if (expr.charAt(i) == ')') {
                char s = operators.pop();
                while (s != '(') {
                    out.append(s).append(' ');
                    s = operators.pop();
                }
            } else if (expr.charAt(i) == '(')
                operators.push(expr.charAt(i));
            else if (isOperator(expr.charAt(i))) {
                if (operators.size() > 0 && getPriority(expr.charAt(i)) <= getPriority(operators.peek())) {
                    out.append(operators.pop()).append(" ");
                }
                operators.push(expr.charAt(i));
            }
        while (operators.size() > 0) {
            out.append(operators.pop()).append(" ");
        }

        return out.toString();
    }
    private static int getLength(String new_input, StringBuilder out, int length) {
        int i = length;
        while (new_input.charAt(i) != ' ' && !isOperator(new_input.charAt(i))) //Смотрим до разделения, чтобы получить число
        {
            out.append(new_input.charAt(i));
            i++;

            if (i == new_input.length()) {
                break;
            }
        }
        return i;
    }

    public static String counting(String input)
    {
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < input.length(); i++)
        {

            if (input.charAt(i) >= '0' && input.charAt(i)<= '9')
            {
                StringBuilder a = new StringBuilder();
                i = getLength(input, a, i);
                stack.push(Integer.parseInt(a.toString()));
                i--;
            }
            else if (isOperator(input.charAt(i)))
            {
                int a = stack.pop();
                if (a >= 100000) a = -a / 10000;
                int b = stack.pop();
                if (b >= 100000) b = -b / 10000;

                if (input.charAt(i) == '+') stack.push(b + a);
                else if (input.charAt(i) == '-') stack.push(b - a);
                else if (input.charAt(i) == '*') stack.push(b * a);
                else if (input.charAt(i) == '/') stack.push(b / a);
            }
        }

        return Integer.toString(stack.peek());
    }
}
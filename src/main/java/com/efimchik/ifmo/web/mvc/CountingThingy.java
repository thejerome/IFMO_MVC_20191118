package com.efimchik.ifmo.web.mvc;
import java.util.ArrayDeque;
import java.util.Stack;
import java.util.regex.Pattern;


public class CountingThingy {
    
    private static int compute(ArrayDeque<String> polQ) {
        Stack<Integer> finalThingy = new Stack<>();

        while (!polQ.isEmpty()) {
            String q = polQ.peek();

            if (Pattern.matches("^[0-9]+$", q)) {
                finalThingy.push(Integer.parseInt(q));
            } else {
                char op = polQ.peek().charAt(0);
                int a = finalThingy.pop();
                int b = finalThingy.pop();
                int res = 0;
                switch (op) {
                    case  ('+'):
                        res = a + b;
                        break;
                    case ('-'):
                        res = a - b;
                        break;
                    case ('/'):
                        res = a / b;
                        break;
                    case ('*'):
                        res = a * b;
                        break;
                    default:
                        ///codacy, fuck you
                        break;
                }
                finalThingy.push(res);
            }

            polQ.poll();
        }

        return finalThingy.pop();
    }
    public static boolean ifEqCorrect (String s)
    {
        if (s.indexOf('*') != -1 || s.indexOf('/') != -1 || s.indexOf('+') != -1 || s.indexOf('-') != -1)
        {
            return  true;
        }
        else
        {
            return false;
        }
    }
    public static boolean ifCorrectCh(String s)
    {
        if (((s.charAt(0) >= 'a' && s.charAt(0) <= 'z')))
        {
            return true;
        }
        else
        {
            return  false;
        }
    }
    private static ArrayDeque<String> toPolNotation(String eq) {
        ArrayDeque<String> polQ = new ArrayDeque<>();
        Stack<Character> eqStack = new Stack<>();
        int i = 0;
        while (i < eq.length()) {
            char ch = eq.charAt(i);

            if (Character.isDigit(ch)) {
                String num = "";
                while (Character.isDigit(eq.charAt(i))) {
                    num = num + eq.charAt(i);
                    i=i+1;
                }
                polQ.offer(num);
                i=i-1;
            }
            switch (ch) {
                case  ('('):
                    eqStack.push(ch);
                    break;
                case (')'):
                    while (eqStack.peek() != '(')
                        polQ.offer(eqStack.pop().toString());
                    eqStack.pop();
                    break;
                default:
                    while (!((eqStack.peek() != '(')&&(eqStack.peek() == '-' || eqStack.peek() == '+')&&(ch == '/' || ch == '*'))) {
                        String offering=eqStack.pop().toString();
                        polQ.offer(offering);
                    }
                    eqStack.push(ch);
                    break;
            }
            i=i+1;
        }
        while (!eqStack.empty()) {
            polQ.offer(eqStack.pop().toString());
        }
        return polQ;
    }
    public static boolean ifCorrect(String s)
    {
        if (((Integer.valueOf(s)*Integer.valueOf(s)<100000000)||(s.charAt(0) >= 'a' && s.charAt(0) <= 'z')))
        {
            return true;
        }
        else
        {
            return  false;
        }
    }
    public static int process(String toCalc) {
        StringBuffer temp = new StringBuffer('(' + toCalc + ')');

        int i = 1;
        while (i < temp.length()) {
            if (temp.charAt(i) == '-' && !Character.isDigit(temp.charAt(i - 1)))
                temp.insert(i, '0');
            i++;
        }
        toCalc=temp.toString();
        return compute(toPolNotation(toCalc));
    }
}
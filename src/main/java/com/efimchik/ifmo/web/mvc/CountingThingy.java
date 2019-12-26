package com.efimchik.ifmo.web.mvc;
import java.util.ArrayDeque;
import java.util.Stack;
import java.util.regex.Pattern;
public class CountingThingy
{
    public static boolean checkIfGood (String s)
    {
        if ((s.indexOf('+') == 1 || s.indexOf('-') == 1 || s.indexOf('*') == 1 || s.indexOf('/') == 1))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private static int calculate(ArrayDeque<String> q) {
        int result=0;
        Stack<Integer> notation = new Stack<>();
        while (!q.isEmpty())
        {
            String s = q.peek();
            if (Pattern.matches("^[0-9]+$", s))
            {
                notation.push(Integer.parseInt(s));
            }
            else
            {
                char op = q.peek().charAt(0);
                int a = notation.pop();
                int b = notation.pop();
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
                notation.push(res);
            }
            q.poll();
        }
        result=notation.pop();
        return result;
    }
    public static int process(String target)
    {
        StringBuffer temp = new StringBuffer('(' + target + ')');
        int i = 1;
        boolean retardedCheck=checkIfGood(target);
        if (retardedCheck)
        {
            while (i < temp.length()) {
                if (temp.charAt(i) == '-' && !Character.isDigit(temp.charAt(i - 1)))
                    temp.insert(i, '0');
                i++;
            }
            String s = temp.toString();
            return calculate(temp);
        }
        else
        {
            ///we fucked up
        }
    }
    public static boolean checkIfGood (String val)
    {
        if (((Integer.valueOf(val)*Integer.valueOf(val)<100000000)||((val.charAt(0) >= 'a' && val.charAt(0) <= 'z'))))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private static ArrayDeque<String> polskaNotation(StringBuffer target) {
        String eq=target.toString();
        ArrayDeque<String> q = new ArrayDeque<>();
        Stack<Character> finalThingy = new Stack<>();
        int i = 0;
        while (i < eq.length()) {
            char ch = eq.charAt(i);
            if (Character.isDigit(ch)) {
                String num = "";
                while (Character.isDigit(eq.charAt(i))) {
                    num += eq.charAt(i);
                    i=i+1;
                }
                q.offer(num);
                i=i-1;
            }
            else
                switch (ch) {
                    case  ('('):
                        finalThingy.push(ch);
                        break;
                    case (')'):
                        while (finalThingy.peek() != '(')
                            q.offer(finalThingy.pop().toString());
                        finalThingy.pop();
                        break;
                    default:
                        while (!((finalThingy
                                .peek() == '+' || finalThingy.peek() == '-')&&(ch == '*' || ch == '/'))&&(finalThingy.peek() != '('))
                            q.offer(finalThingy.pop().toString());
                        finalThingy.push(ch);
                        break;
                }
            i=i+1;
        }
        while (!finalThingy.empty()) {
            String offering="";
            offering+=finalThingy.pop().toString();
            q.offer(offering);
        }
        return q;
    }


}
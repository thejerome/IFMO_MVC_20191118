package com.efimchik.ifmo.web.mvc.Calc;

import java.util.HashMap;
import java.util.StringJoiner;

public class CalcUtil {
    public static String MakeEquation(HashMap<String, String> li) {
        HashMap<String, String> vars = new HashMap<>();
        String equation = "";
        for (String keys : li.keySet()) {
            if (keys.equals("equation")) equation = li.get(keys);
            else if (vars.containsKey(li.get(keys))) vars.put(keys, vars.get(li.get(keys)));
            else vars.put(keys, li.get(keys));
        }
        equation = equation.replaceAll(" ", "");
        return delimitString(equation, vars);
    }

    private static String delimitString(String equation, HashMap<String, String> s) {
        StringJoiner joiner = new StringJoiner("");
        for (char ch: equation.toCharArray()) {
            String sh = Character.toString(ch);
            if (s.containsKey(sh)) joiner.add(s.get(sh));
            else
            joiner.add(Character.toString(ch));
        }
        return joiner.toString();
    }


    public static String calc(String str) {
        int result = 0;
        int tmp = 0;
        String operators = "+ ";
        for (int i=1; i<str.length(); i++) {
            String subs;
            int j;
            if (str.charAt(i) == '(') {
                int endIndex = i+1;
                int cnt = 1;
                while (cnt > 0) {
                    if (str.charAt(endIndex) == '(') cnt++;
                    else if (str.charAt(endIndex) == ')') cnt--;
                    endIndex++;
                }
                subs = calc(str.substring(i,endIndex-1));
                j = endIndex;
            }
            else {
                j = i;
                while (j < str.length() && (isOperator(str.charAt(j)) || j==1)) j++;
                subs = str.substring(i, j);
            }
            if (j == str.length()) {
                if (operators.charAt(1) == ' ') tmp = Integer.parseInt(subs);
                else tmp = solveNPath(operators.charAt(1), tmp, Integer.parseInt(subs));
                result = solveNPath(operators.charAt(0), result, tmp);
                break;
            }
            if (operators.charAt(1) == '*' || operators.charAt(1) == '/') {
                tmp = solveNPath(operators.charAt(1), tmp, Integer.parseInt(subs));
                if (str.charAt(j) == '+' || str.charAt(j) == '-') {
                    result = solveNPath(str.charAt(j), result, tmp);
                    tmp = 0;
                    operators = str.charAt(j) + " ";
                }
                else operators = operators.replace(operators.charAt(1),str.charAt(j));
            }
            else {
                if (str.charAt(j) == '*' || str.charAt(j) == '/') {
                    tmp += Integer.parseInt(subs);
                    operators = operators.replace(operators.charAt(1),str.charAt(j));
                }
                else {
                    if (operators.charAt(0) == '+') result += Integer.parseInt(subs);
                    else result -= Integer.parseInt(subs);
                    operators = operators.replace(operators.charAt(0),str.charAt(j));

                }
            }
            i=j;
        }
        return String.valueOf(result);
    }

    private static int solveNPath(char operator,int leftOperand,
                                        int rightOperand
                                        ) {
        switch(operator) {
            case '+':
                return leftOperand + rightOperand;
            case '-':
                return leftOperand - rightOperand;
            case '*':
                return leftOperand * rightOperand;
            case '/':
                return leftOperand / rightOperand;
            default:
                return 0;
        }
    }

    private static boolean isOperator(char c) {
        return !(c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')');
    }
}

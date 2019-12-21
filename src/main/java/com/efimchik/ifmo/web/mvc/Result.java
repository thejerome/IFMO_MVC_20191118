package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import javax.servlet.http.HttpSession;


@Controller
public class Result {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getCalcResult(HttpSession session) {
        String equation = (String) session.getAttribute("equation");

        if (equation == null) {
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        }
        int result_1;
        equation = equation.replaceAll("\\s", "");
        //System.out.println(getResult(session, equation));
        String newExpr = getExpression(equation);

        System.out.println(newExpr);
        try {
            //System.out.println(getResult(session, newExpr));
            result_1 = counting(getNumbers(session, newExpr));
            //System.out.println(result_1);
            //result = getResult(session, newExpr);
        } catch (IllegalArgumentException e) {
            System.out.print("Error");
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        }
        return new ResponseEntity<>(Integer.toString(result_1), HttpStatus.valueOf(200));
    }



    private static boolean isDelimeter(char c)
    {
        return (c == ' ' || c == '=');
    }

    private boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '/' || c == '*');
    }

    private boolean isVar(String str) {
        if (str.length() != 1)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!(str.charAt(i) >= 'a' && str.charAt(i) <= 'z'))
                return false;
        }
        return true;
    }

    private boolean isNumber(String str) {
        if (str.charAt(0) == '-' && str.length() == 1)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (i == 0 && str.charAt(i) == '-')
                continue;
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String getValue(HttpSession session, String str) {
        String s = (String) session.getAttribute(str);
        if (s == null) {
            throw new IllegalArgumentException();
        }
        while (!isNumber(s)) {
            s = (String) session.getAttribute(s);
            if (s == null) {
                throw new IllegalArgumentException();
            }
        }
        return s;
    }


    private String getExpression(String input)
    {
        StringBuilder output = new StringBuilder();
        Stack<Character> operStack = new Stack<>();



        for (int i = 0; i < input.length(); i++)
        {

            if (isDelimeter(input.charAt(i)))
                continue;


            if (input.charAt(i) >= 'a' && input.charAt(i) <= 'z' || input.charAt(i) >= '0' && input.charAt(i) <= '9')
            {

                while (!isDelimeter(input.charAt(i)) && !isOperator(input.charAt(i)))
                {
                    output.append(input.charAt(i));
                    i++;

                    if (i == input.length()) break;
                }

                output.append(".");
                i--;
            }

            if (isOperator(input.charAt(i)) | input.charAt(i) == '(' | input.charAt(i) == ')')
            {
                if (input.charAt(i) == '(')
                    operStack.push(input.charAt(i));
                else if (input.charAt(i) == ')')
                {
                    char s = operStack.pop();

                    while (s != '(')
                    {
                        output.append(s).append('.');
                        s = operStack.pop();
                    }
                }
                else
                {
                    if (operStack.size() > 0 && getPriority(input.charAt(i)) <= getPriority(operStack.peek()))
                        output.append(operStack.pop().toString()).append(".");



                    operStack.push(input.charAt(i));

                }
            }
        }

        while (operStack.size() > 0)
            output.append(operStack.pop()).append(".");

        StringBuilder result = new StringBuilder();
        for (int i = 0 ; i < output.length(); i ++){
            if (output.charAt(i) != ')'){
                result.append(output.charAt(i));}

        }
        return result.toString();
    }





    private static int getPriority(char symbol) {
        switch (symbol)
        {
            case '(': return 0;
            case ')': return 1;
            case '+': return 2;
            case '-': return 3;
            case '*': return 4;
            case '/': return 4;
            default: return 5;
        }
    }

    private int counting(String input)
    {
        //System.out.print(input);
        int result = 0;
        Stack<Integer> temp = new Stack<>();
        boolean if_neg = false;
        for (int i = 0; i < input.length(); i++)
        {
            if (input.charAt(i) == '-' && input.charAt(i+1) != ' ') {
                i ++;

                if_neg = true;
            };

            if (input.charAt(i) >= '0' && input.charAt(i)<= '9')
            {
                String a = "";

                while (!isDelimeter(input.charAt(i)) && !isOperator(input.charAt(i)))
                {
                    a += input.charAt(i);
                    i++;
                    if (i == input.length()) break;
                }
                if (if_neg){
                    temp.push(0 - Integer.parseInt(a));
                    if_neg = false;
                    System.out.println(Integer.parseInt(a));
                }
                else{
                    System.out.println(Integer.parseInt(a));
                    temp.push(Integer.parseInt(a));
                }

                System.out.println(temp);
                i--;
            }
            else if (isOperator(input.charAt(i)))
            {
                int a = temp.pop();

                int b = temp.pop();


                switch (input.charAt(i))
                {
                    case '+': result = b + a; break;
                    case '-': result = b - a; break;
                    case '*': result = b * a; break;
                    case '/': result = b / a; break;
                    default: result = 0; break;
                }
                temp.push(result);
            }
        }


        return (temp.peek());
    }





    private String getNumbers(HttpSession session, String equation) {
        if (equation == null) {
            return null;
        }
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < equation.length(); i ++) {
            String symbol = Character.toString(equation.charAt(i));
            //System.out.println(token);
            if (isNumber(symbol)) {
                //System.out.println(token);
                output.append(symbol);
                output.append(' ');
                continue;
            }
            if (isVar(symbol)) {
                String valueOfVar;
                valueOfVar = getValue(session, symbol);
                output.append(valueOfVar);
                output.append(' ');
                continue;
            }
            if (isOperator(symbol.charAt(0))) {
                output.append(symbol.charAt(0));
                output.append(' ');
            }
        }
        //System.out.println(output);
        return output.toString();




    }










}

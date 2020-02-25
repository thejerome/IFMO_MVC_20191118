package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class getAnswerController {
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> e = session.getAttributeNames();

        while (e.hasMoreElements()) {
            String name = e.nextElement();
            map.put(name, (String) session.getAttribute(name));
        }

        String equation = map.get("equation");
        equation = equation.replaceAll("\\s", "");
        try {
            if (equation != null) {
                    System.out.println(map);
    //                System.out.println("mapped: " + mapVariables(equationToPostfix(equation), map)) ;
    //                System.out.println("eval: " + evalPostfix(mapVariables(equationToPostfix(equation), map)));
                    String mappedExpression = mapVariables(equationToPostfix(equation), map);
                    if ("409".equals(mappedExpression)) {
                        return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
                    } else {
                        return ResponseEntity.status(HttpStatus.valueOf(200)).body(evalPostfix(mappedExpression));
                    }
            } else {
                    return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
            }
        } catch (IllegalArgumentException exception) {
                return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
        }
    }

    private static String equationToPostfix(String expr) {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i <expr.length() ; i++) {
            char ch = expr.charAt(i);
            if(getPrecedence(ch)>0){
                while(!stack.isEmpty() && (getPrecedence(stack.peek())>=getPrecedence(ch))) {
                    result.append(stack.pop());
                }
                stack.push(ch);
            } else if(ch==')'){
                char x = stack.pop();
                while(x!='('){
                    result.append(x);
                    x = stack.pop();
                }
            }else if(ch=='('){
                stack.push(ch);
            }else{
                result.append(ch);
            }
        }
        for (int i = 0; i <=stack.size() ; i++) {
            result.append(stack.pop());
        }
        return result.toString();
    }

    private static int getPrecedence(char ch){
        switch (ch){
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private static int doMath(int l, int r, String op) {
        switch (op) {
            case "+":
                return l + r;
            case "-":
                return l - r;
            case "*":
                return l * r;
            case "/":
                return l / r;
            default:
                return 0;
        }
    }

    private static String mapVariables(String expr, Map<String, String> map) {
        StringBuilder numericEquation = new StringBuilder();
        for (int i = 0; i < expr.length(); ++i) {
            char cur = expr.charAt(i);
            if (cur >= 'a' && cur <= 'z') {
                String newVal = map.get(String.valueOf(cur));
                if (newVal == null) {
                    return "409";
                }
                while (!isNumber(newVal)){
                    newVal =  map.get(newVal);
                    if (newVal == null) {
                        return "409";
                    }
                }
                numericEquation.append(newVal);
                numericEquation.append("_");
            } else {
                numericEquation.append(expr.charAt(i));
                numericEquation.append("_");
            }
        }
        return numericEquation.toString();
    }

    private static boolean isNumber(String s) {
        if (s.charAt(0) == '-' && s.length() == 1)
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (!isInteger(s.charAt(i)) && !(i == 0 && s.charAt(i) == '-'))
                return false;
        }
        return true;
    }

    private static String evalPostfix(String expr)
    {
        Stack<Integer> stack=new Stack<>();
        String[] exprList = expr.split("_");
        System.out.println(Arrays.toString(exprList));
        for (String str : exprList) {
            if (" ".equals(str)) {
                continue;
            } else if (isInteger(str)) {
                stack.push(Integer.parseInt(str));
            } else {
                int rightVal = stack.pop();
                int leftVal = stack.pop();
                stack.push(doMath(leftVal, rightVal, str));
            }
        }
        int res = stack.pop();
        System.out.println("res: " + res);
        return Integer.toString(res);
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private static boolean isInteger(Character ch) {
        try {
            Integer.parseInt(String.valueOf(ch));
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}

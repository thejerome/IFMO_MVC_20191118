package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;



@Controller
public class ResultController {
    @GetMapping("/calc/result")
    public ResponseEntity<String> doGet(HttpSession session){
        if (session == null) {
            return new ResponseEntity<>("session is null", HttpStatus.valueOf(409));
        } else {
            String equation = (String) session.getAttribute("equation");
            if (equation == null) {
                return new ResponseEntity<>("equation is null", HttpStatus.valueOf(409));
            } else {
                if (isAllVarsDefined(equation, session)) {
                    return new ResponseEntity<>(String.valueOf(result(equation.replaceAll("\\s", ""), session)), HttpStatus.valueOf(200));
                } else {
                    return new ResponseEntity<>("some vars are undefined", HttpStatus.valueOf(409));
                }
            }
        }
    }

    private boolean isAllVarsDefined(String equation, HttpSession session) {
        for (int i = 0; i < equation.length(); ++i) {
            char c = equation.charAt(i);
            if (c >= 'a' && c <= 'z') {
                String var = String.valueOf(c);
                if (session.getAttribute(var) == null)
                    return false;
            }
        }
        return true;
    }

    private int result(String eq, HttpSession session) {
        Map<String, String> attributes = new HashMap<>();
        Enumeration<String> en = session.getAttributeNames();
        while (en.hasMoreElements()) {
            String el = en.nextElement();
            attributes.put(el, session.getAttribute(el).toString());
        }
        Map<Character, Integer> normalMap = new HashMap<>();

        for (Map.Entry<String, String> attributesEntry : attributes.entrySet()) {
            if (!(attributesEntry.getKey().equals("equation"))) {
                String val = attributesEntry.getValue();
                if (isVariable(val.charAt(0))) {
                    for (Map.Entry<String, String> attrEntry : attributes.entrySet()) {
                        if (val.equals(attrEntry.getKey())) {
                            val = attrEntry.getValue();
                        }
                    }
                }
                int valInt = Integer.parseInt(val);
                normalMap.put(attributesEntry.getKey().charAt(0), valInt);
            }
        }
        return calc(eq, normalMap);
    }

    private int calc(String eq, Map<Character, Integer> normalMap) {
        Stack<Character> stackOperators = new Stack<>();
        Stack<Integer> stackVars = new Stack<>();
        for (int i = 0; i < eq.length(); i++) {
            char eqChar = eq.charAt(i);

            if (isVariable(eqChar))
                stackVars.push(normalMap.get(eqChar));

            else if (isSecondaryOperator(eqChar)) {
                while ((stackVars.size() > 1) && (!stackOperators.empty()) && (isSecondaryOperator(stackOperators.peek()) || isPriorityOperator(stackOperators.peek()))) {
                    operation(stackVars, stackOperators);
                }
                stackOperators.push(eqChar);

            } else if (isPriorityOperator(eqChar)) {
                while ((stackVars.size() > 1) && (!stackOperators.empty()) && (isPriorityOperator(stackOperators.peek()))) {
                    operation(stackVars, stackOperators);
                }
                stackOperators.push(eqChar);

            } else if (eqChar == '(') {
                stackOperators.push(eqChar);

            } else if (eqChar == ')') {
                while (stackVars.size() > 1 && stackOperators.peek() != '(') {

                    operation(stackVars, stackOperators);
                }
                stackOperators.pop();
            } else {
                stackVars.push(Integer.parseInt(String.valueOf(eqChar)));
            }
        }

        while (!stackOperators.isEmpty() && stackVars.size() > 1)
            operation(stackVars, stackOperators);

        return stackVars.pop();
    }


    private static boolean isPriorityOperator(char oper) {
        return oper == '*' || oper == '/';
    }

    private static boolean isSecondaryOperator(char oper) {
        return oper == '+' || oper == '-';
    }

    private static boolean isVariable(char oper) {
        return oper >= 'a' && oper <= 'z';
    }

    private static void operation(Stack<Integer> var, Stack<Character> oper) {
        int var1 = var.pop();
        int var2 = var.pop();
        switch (oper.pop()) {
            case '+':
                var.push(var1 + var2);
                break;
            case '-':
                var.push(var2 - var1);
                break;
            case '*':
                var.push(var1 * var2);
                break;
            case '/':
                var.push(var2 / var1);
                break;
            default:
                break;
        }
    }
}

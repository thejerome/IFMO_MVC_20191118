package com.efimchik.ifmo.web.mvc;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Pattern;

@Controller
public class CalcController {
    @RequestMapping(value = "/calc/result", method = RequestMethod.GET)
    public ResponseEntity<String> makeSomeMagic(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String nextElement = attributeNames.nextElement();
            map.put(nextElement, session.getAttribute(nextElement).toString());
        }

        final String[] equation = {map.remove("equation")};


        map.forEach((key, value) -> {
            if (Character.isAlphabetic(value.charAt(0))) {
                equation[0] = equation[0].replace(key, map.get(value));
            }
            equation[0] = equation[0].replace(key, value);
        });

        String eq = equation[0].replace(" ", "");
        Stack<Character> opers = new Stack<>();
        Stack<Integer> nums = new Stack<>();
        int negativeTrigger = 1;
        StringTokenizer stringTokenizer = new StringTokenizer(eq, "+-*/()", true);
        while (stringTokenizer.hasMoreElements()) {
            String nextToken = stringTokenizer.nextToken();
            if (nextToken.matches("[+-/*]")) {
                if (nums.isEmpty()) {
                    negativeTrigger = -1;
                } else if (opers.isEmpty() || isHeStronger(nextToken.charAt(0), opers.peek())) {
                    opers.push(nextToken.charAt(0));
                } else {
                    nums.push(calc(opers.pop(), nums.pop(), nums.pop()));
                    opers.push(nextToken.charAt(0));
                }
            } else if (nextToken.matches("\\d+")) {
                nums.push(Integer.parseInt(nextToken) * negativeTrigger);
                negativeTrigger = 1;
            }  else if (nextToken.matches("[a-z]")) {
                return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
            } else {
                if ("(".equals(nextToken)) {
                    opers.push(nextToken.charAt(0));
                } else if (")".equals(nextToken)) {
                    while (!opers.isEmpty() && opers.peek() != '(') {
                        nums.push(calc(opers.pop(), nums.pop(), nums.pop()));
                    }
                    opers.pop();
                } else {
                    nums.push(calc(opers.pop(), nums.pop(), nums.pop()));
                }
            }
        }
        while (!opers.isEmpty()) {
            nums.push(calc(opers.pop(), nums.pop(), nums.pop()));
        }
        return ResponseEntity.status(HttpStatus.valueOf(200)).body((nums.peek()).toString());
    }

    @RequestMapping(value = "/calc/{variable:[a-z]|equation}", method = RequestMethod.PUT)
    public ResponseEntity<String> putSomeVariables(@PathVariable String variable, @RequestBody String requestBody, HttpSession session) {
        String value = requestBody.replaceAll(" ","");
        if("equation".equals(variable) && !isValid(requestBody)) {
            return ResponseEntity.status(HttpStatus.valueOf(400)).body("400");
        } else if (isInt(value) && !(Integer.parseInt(value) <= 10000 && Integer.parseInt(value) >= -10000)){
            return ResponseEntity.status(HttpStatus.valueOf(403)).body("403");
        }

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            if(variable.equals(attributeNames.nextElement())) {
                session.setAttribute(variable, value);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body("200");
            }
        }
        session.setAttribute(variable,value);
        return ResponseEntity.status(HttpStatus.valueOf(201)).body("201");
    }

    @RequestMapping(value = "/calc/{pathVariable:[a-z]|equation}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFromSession(@PathVariable String pathVariable, HttpSession session) {
        session.removeAttribute(pathVariable);
        return ResponseEntity.status(HttpStatus.valueOf(204)).body("204");
    }

    private static boolean isValid(String equation) {
        int charPosition = 0;
        for (int i = 0; i < equation.length(); i++) {
            char ch = equation.charAt(i);
            if (!Pattern.matches("[A-Z]", Character.toString(ch))) {
                if (ch == '+' || ch == '-' || ch == '*'|| ch == '/') {
                    charPosition++;
                }
            } else {
                return false;
            }
        }
        return charPosition != 0;
    }

    private static Integer calc(Character operation, Integer num2, Integer num1) {
        return (operation == '+') ? num1 + num2 :
                (operation == '-') ? num1 - num2 :
                        (operation == '*') ? num1 * num2 :
                                (operation == '/') ? num1 / num2 : 0;
    }

    private static int getStrength(Character o) {
        return (o == '(' || o == ')') ? 0 : (o == '+' || o == '-') ? 1 : 2;
    }

    private static boolean isHeStronger(char o1, Character o2) {
        return getStrength(o1) > getStrength(o2);
    }

    private static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}

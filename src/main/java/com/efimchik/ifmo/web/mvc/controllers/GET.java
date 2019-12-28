package com.efimchik.ifmo.web.mvc.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class GET {
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session){
        if(session.getAttribute("equation") == null) {
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        }
        else try{
            String equation = (String) session.getAttribute("equation");
            Map<String, String> valueMap = (Map<String, String>) session.getAttribute("parameters");
            StringBuilder equation_ = new StringBuilder();
            for(int i =0; i<equation.length(); i++){
                if(Character.isLowerCase(equation.charAt(i))){
                    String value = valueMap.get(String.valueOf(equation.charAt(i)));
                    while (!isNumeric(value)){
                        value = valueMap.get(value);
                    }
                    equation_.append(value);
                }
                else equation_.append(equation.charAt(i));
            }
            equation = equation_.toString();
            equation = equation + "=";
            if('-' == equation.charAt(0)) equation = "0" + equation;
            equation = equation.replaceAll(" ", "");
            return new ResponseEntity<>(calc(equation), HttpStatus.valueOf(200));
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        }
    }

    private boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    private String calc(String str){
        Stack<Long> numStack = new Stack<Long>();
        Stack<Character> symStack = new Stack<Character>();
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i<str.length(); i++){
            char c = str.charAt(i);
            if(isNumber(c)){
                temp.append(c);
            }
            else{
                String temp_ = temp.toString();
                if(!temp_.isEmpty()){
                    long num = Long.parseLong(temp_);
                    numStack.push(num);
                    temp = new StringBuffer();
                }
                while (!symCompare(c, symStack) && !symStack.isEmpty()){
                    long b = numStack.pop();
                    long a = numStack.pop();
                    switch (symStack.pop()){
                        case '+':
                            numStack.push(a+b);
                            break;
                        case '-':
                            numStack.push(a-b);
                            break;
                        case '*':
                            numStack.push(a*b);
                            break;
                        case '/':
                            numStack.push(a/b);
                            break;
                        default:
                            break;
                    }
                }
                if(c != '='){
                    symStack.push(c);
                    if(c == ')'){
                        symStack.pop();
                        symStack.pop();
                    }
                }
            }
        }
        int result = Math.toIntExact(numStack.pop());
        String st = String.valueOf(result);
        return st;
    }

    private boolean isNumber(char ch){
        return (ch>='0' && ch<='9');
    }

    private boolean symCompare(char sym, Stack<Character> symStack){
        if(symStack.isEmpty()) return true;
        char top = symStack.peek();
        if(top == '(') return true;
        switch (sym){
            case '(': return true;
            case '*':
            case '/': return (top == '+' || top == '-');
            case '+':
            case '-':
            case ')':
            case '=':
                return false;
            default:
                break;
        }
        return true;
    }
}

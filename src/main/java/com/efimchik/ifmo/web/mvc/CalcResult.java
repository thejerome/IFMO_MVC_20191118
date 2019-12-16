package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class CalcResult {

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putCalcEquation(HttpSession session, @RequestBody String equation){
        if (!itOkay(equation)) {
            return new ResponseEntity<>("Badly formatted!", HttpStatus.valueOf(400));
        } else {
            Object prev = session.getAttribute("equation");
            session.setAttribute("equation", equation);
            if (prev == null) {
                return new ResponseEntity<>( HttpStatus.valueOf(201));
            } else {
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            }
        }
    }

    private boolean itOkay(String equ) {
        int flag = 0;
        for (int i = 0; i < equ.length(); ++i) {
            if (equ.charAt(i)>='A' && equ.charAt(i)<='Z'){
                return false;
            } else if (equ.charAt(i)=='+' || equ.charAt(i)=='-' || equ.charAt(i)=='/' || equ.charAt(i)=='*') {
                flag++;
            }
        }
        return flag != 0;
    }


    @DeleteMapping("/calc/equation")
    public ResponseEntity delEq(HttpSession session){
        session.setAttribute("equation", null);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    @PutMapping("/calc/{name}")
    protected ResponseEntity<String> doPut(HttpSession session, @PathVariable String name, @RequestBody String equation){
        if (itOkayToo(equation)){
            Object prev = session.getAttribute(name);
            session.setAttribute(name, equation);
            if (prev == null) {
                return new ResponseEntity<>( HttpStatus.valueOf(201));
            } else {
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            }
        } else {
            return new ResponseEntity<>("Incorrect value!", HttpStatus.valueOf(403));
        }
    }

    private boolean itOkayToo(String value) {
        if (value.charAt(0)>='a' && value.charAt(0)<='z') {
            return true;
        }
        return Integer.parseInt(value) >= -10000 && Integer.parseInt(value) <= 10000;
    }

    @DeleteMapping("/calc/{name}")
    protected ResponseEntity doDelete(HttpSession session, @PathVariable String name) {
        session.setAttribute(name, null);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    @GetMapping("/calc/result")
    public ResponseEntity<String> getCalcResult(HttpSession session){
        String equation = (String) session.getAttribute("equation");
        if (equation == null) {
            return new ResponseEntity<>("not enough data", HttpStatus.valueOf(409));
        } else {
            try {
                return new ResponseEntity<>( deci(session), HttpStatus.valueOf(200));
            } catch (Exception e){
                return new ResponseEntity<>("Incorrect inputs!", HttpStatus.valueOf(409));
            }
        }
    }

    private static boolean ope(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int prior(char op) {
        switch (op) {
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

    private static void calc(LinkedList<Integer> st, char op) {
        int x = st.removeLast();
        int y = st.removeLast();
        switch (op) {
            case '+':
                st.add(x + y);
                break;
            case '-':
                st.add(y - x);
                break;
            case '*':
                st.add(x * y);
                break;
            case '/':
                st.add(y / x);
                break;
            default:
                break;
        }
    }

    private static String deci(HttpSession session) {
        String equation = (String) session.getAttribute("equation");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < equation.length(); ++i) {
            if (equation.charAt(i) >= 'a' && equation.charAt(i) <= 'z') {
                String val = (String) session.getAttribute(String.valueOf(equation.charAt(i)));
                while (val.charAt(0) >= 'a' && val.charAt(0) <= 'z') {
                    val = (String) session.getAttribute(val);
                }
                sb.append(val);
            } else {
                sb.append(equation.charAt(i));
            }
        }
        equation = sb.toString();
        LinkedList<Integer> numb = new LinkedList<>();
        LinkedList<Character> op = new LinkedList<>();
        equation=equation.replaceAll("\\s+","");
        if (equation.charAt(0)=='-')
            numb.add(0);
        for (int i = 0; i < equation.length(); i++) {
            char p = equation.charAt(i);
            if (p == '(') {
                op.add('(');
                if (equation.charAt(i+1)=='-')
                    numb.add(0);
            }
            else if (p == ')') {
                while (op.getLast() != '(')
                    calc(numb, op.removeLast());
                op.removeLast();
            } else if (ope(p)) {
                while (!op.isEmpty() && prior(op.getLast()) >= prior(p))
                    calc(numb, op.removeLast());
                op.add(p);
            }  else {
                StringBuilder number = new StringBuilder();
                while (i < equation.length() && Character.isDigit(equation.charAt(i)))
                    number.append(equation.charAt(i++));
                --i;
                numb.add(Integer.parseInt(number.toString()));
            }
        }
        while (!op.isEmpty())
            calc(numb, op.removeLast());
        return String.valueOf(numb.get(0));
    }

}
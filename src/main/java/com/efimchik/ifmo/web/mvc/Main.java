package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.StringTokenizer;

@Controller
public class Main {
    @PutMapping("/calc/equation")
    public ResponseEntity<String> doPut(HttpSession session, @RequestBody String eq) {
        HttpStatus httpStatus;
        if (session.getAttribute("equation") != null) {
            httpStatus = HttpStatus.valueOf(200);
        } else {
            httpStatus = HttpStatus.valueOf(201);
        }
        if (!good(eq)) {
            httpStatus = HttpStatus.valueOf(400);
            return new ResponseEntity<>("...", httpStatus);
        } else
            session.setAttribute("equation", eq);
        return new ResponseEntity<>(httpStatus);
    }

    private boolean good(String eq) {
        int cnt = 0;
        for (char c : eq.toCharArray())
            if (c == '+' || c == '-' || c == '/' || c == '*') {
                cnt++;
            }
        return cnt != 0;
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity<String> doDelete(HttpSession httpSession) {
        httpSession.removeAttribute("equation");
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    @PutMapping("/calc/{var}")
    public ResponseEntity<String> doPut(HttpSession session, @PathVariable String var, @RequestBody String varVal) {
        HttpStatus httpStatus;
        if (session.getAttribute(var) != null) {
            httpStatus = HttpStatus.valueOf(200);
        } else {
            httpStatus = HttpStatus.valueOf(201);
        }
        if (!(varVal.charAt(0) >= 'a' && varVal.charAt(0) <= 'z')) {
            int i = Integer.parseInt(varVal);
            if (i < -10000 || i > 10000) {
                httpStatus = HttpStatus.valueOf(403);
                return new ResponseEntity<>("...", httpStatus);
            } else
                session.setAttribute(var, varVal);
        } else
            session.setAttribute(var, varVal);
        return new ResponseEntity<>(httpStatus);
    }

    @DeleteMapping("/calc/{var}")
    public ResponseEntity<String> doDelete(HttpSession session, @PathVariable String var) {
        session.removeAttribute(var);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    @GetMapping("/calc/result")
    public ResponseEntity<String> doGet(HttpSession session) {
        String equation = ((String)session.getAttribute("equation")).replaceAll("\\s", "");
        StringBuilder rpn = rPn(equation);
        ArrayDeque<String> cal = new ArrayDeque<>();
        try {
            calculation(session, rpn, cal);
        } catch (IllegalStateException e){
            return new ResponseEntity<>("...", HttpStatus.valueOf(409));
        }
        return new ResponseEntity<>(cal.getFirst(), HttpStatus.valueOf(200));
    }


    private StringBuilder rPn(String equation){
        StringBuilder sb = new StringBuilder();
        ArrayDeque<String> st = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(equation, "*/+-()", true);
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            if ( (t.charAt(0) >= 'a' && t.charAt(0) <= 'z') ||
                    Character.isDigit(t.charAt(0))) {
                sb.append(t).append('|');
            } else
            if (t.charAt(0) == '+' || t.charAt(0) == '-' || t.charAt(0) == '/' || t.charAt(0) == '*') {
                if (st.peek() != null) {
                    while (pr(st.peek().charAt(0)) >= pr(t.charAt(0))) {
                        String s = st.pop();
                        sb.append(s).append('|');
                        if (st.peek() == null)
                            break;
                    }
                }
                st.push(t);
            } else
            if ("(".equals(t)) {
                st.push(t);
            } else
            if (")".equals(t)) {
                while (!Objects.equals(st.peek(), "(")) {
                    String op = st.pop();
                    sb.append(op).append('|');
                }
                st.pop();
            }
        }
        while (st.peek() != null) {
            sb.append(st.pop()).append('|');
        }
        sb.setLength(sb.length() - 1);
        return sb;
    }

    private void calculation(HttpSession session,StringBuilder eq, ArrayDeque<String> calc){
        StringTokenizer tokenizer = new StringTokenizer(eq.toString(), "|");
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            if (t.charAt(0) == '+' || t.charAt(0) == '-' || t.charAt(0) == '/' || t.charAt(0) == '*') {
                String r = calc.pop();
                String l = calc.pop();
                calc.push(calculate(Integer.parseInt(l), Integer.parseInt(r), t.charAt(0)));
            }
            else if (t.length() == 1 && t.charAt(0) >= 'a' && t.charAt(0) <= 'z') {
                String s = (String) session.getAttribute(t);
                if (s == null){
                    throw new IllegalStateException();
                }
                while (s.charAt(0) >= 'a' && s.charAt(0) <= 'z') {
                    s = (String) session.getAttribute(s);
                    if (s == null){
                        throw new IllegalStateException();
                    }
                }
                calc.push(s);
            } else {
                calc.push(t);
            }
        }
    }

    private int pr(char c) {
        if (c == '*' || c == '/')
            return 2;
        else if (c == '+' || c == '-')
            return 1;
        else
            return 0;
    }

    private String calculate(int lhs, int rhs, char c) {
        switch (c) {
            case '/':
                return String.valueOf(lhs / rhs);
            case '*':
                return String.valueOf(lhs * rhs);
            case '+':
                return String.valueOf(lhs + rhs);
            case '-':
                return String.valueOf(lhs - rhs);
            default:
                return "impossible";
        }
    }
}
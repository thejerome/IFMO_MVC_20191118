package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class GetResult{

    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession httpSession){
        String equation = (String) httpSession.getAttribute("equation");
        if (equation != null) {
            try {
                return new ResponseEntity<>( calculation(equation, httpSession), HttpStatus.valueOf(200));
            } catch (IllegalArgumentException e){
                return new ResponseEntity<>("bad bad bad", HttpStatus.valueOf(409));
            }
        } else {
            return new ResponseEntity<>("bad bad bad", HttpStatus.valueOf(409));
        }
    }

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEq(HttpSession httpSession, @RequestBody String s){
        if (badFormatted(s)) {
            return new ResponseEntity<>("bad bad bad", HttpStatus.valueOf(400));
        } else {
            if (httpSession.getAttribute("equation") == null) {
                httpSession.setAttribute("equation", s);
                return new ResponseEntity<>("good", HttpStatus.valueOf(201));
            } else {
                httpSession.setAttribute("equation", s);
                return new ResponseEntity<>("good", HttpStatus.valueOf(200));
            }
        }
    }

    private boolean badFormatted(String s) {
        for (int i = 1; i<s.length(); ++i){
            char cur = s.charAt(i);
            char prev = s.charAt(i-1);
            if (Character.isLetter(cur) && Character.isLetter(prev))
                return true;
        }
        return false;
    }


    @DeleteMapping("/calc/equation")
    public ResponseEntity delEq(HttpSession httpSession){
        httpSession.setAttribute("equation", null);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    @PutMapping("/calc/{name}")
    protected ResponseEntity<String> doPut(HttpSession httpSession, @PathVariable String name, @RequestBody String s){
        if (!badFormattedVar(s)) {
            return new ResponseEntity<>("bad bad bad", HttpStatus.valueOf(403));
        } else {
            if (httpSession.getAttribute(name) == null) {
                httpSession.setAttribute(name, s);
                return new ResponseEntity<>("good", HttpStatus.valueOf(201));
            } else {
                return new ResponseEntity<>("good", HttpStatus.valueOf(200));
            }
        }
    }

    private boolean badFormattedVar(String s) {
        if (s.length() == 1 && s.charAt(0) >= 'a' && s.charAt(0) <= 'z')
            return true;
        try {
            int a = Integer.parseInt(s);
            return a>=-10000 && a<=10000;
        } catch (Exception e){
            return false;
        }
    }

    @DeleteMapping("/calc/{name}")
    protected ResponseEntity doDelete(HttpSession httpSession, @PathVariable String name) {
        httpSession.setAttribute(name, null);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private String getValue(String c, HttpSession session){
        if (c.charAt(0) >= 'a' && c.charAt(0) <= 'z'){
            String newVal = c;
            while (newVal.charAt(0) >= 'a' && newVal.charAt(0) <= 'z'){
                newVal = (String) session.getAttribute(newVal);
                if (newVal == null)
                    throw new IllegalArgumentException();
            }
            return newVal;
        } else {
            return c;
        }
    }

    private String calculation(String st, HttpSession session) {
        String lft;
        String rht;
        Stack<String> stack = new Stack<>();
        String equation = pol(st);
        StringTokenizer stringTokenizer = new StringTokenizer(equation);
        String tmp;
        while (stringTokenizer.hasMoreTokens()) {
            tmp = stringTokenizer.nextToken().trim();
            if (opr(tmp.charAt(0)) && 1 == tmp.length()) {
                rht = stack.pop();
                lft = stack.pop();
                rht = getValue(rht,session);
                lft = getValue(lft,session);
                int ans;
                switch (tmp.charAt(0)) {
                    case '+':
                        ans = Integer.parseInt(lft) + Integer.parseInt(rht);
                        break;
                    case '*':
                        ans = Integer.parseInt(lft) * Integer.parseInt(rht);
                        break;
                    case '-':
                        ans = Integer.parseInt(lft) - Integer.parseInt(rht);
                        break;
                    case '/':
                        ans = Integer.parseInt(lft) / Integer.parseInt(rht);
                        break;
                    default:
                        ans = -1;
                        break;
                }
                stack.push(String.valueOf(ans));
            } else {
                lft = tmp;
                stack.push(lft);
            }
        }
        return stack.pop();
    }


    private String pol(String src) {
        char tmp;
        char buffer;
        StringBuilder res = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            buffer = src.charAt(i);
            if (opr(buffer)) {
                while (sb.length() > 0) {
                    tmp = sb.substring(sb.length() - 1).charAt(0);
                    if (opr(tmp) && (priority(buffer) <= priority(tmp))) {
                        res.append(" ").append(tmp).append(" ");
                        sb.setLength(sb.length() - 1);
                    } else {
                        res.append(" ");
                        break;
                    }
                }
                res.append(" ");
                sb.append(buffer);
            } else if ('(' == buffer) {
                sb.append(buffer);
            }
            else if (')' == buffer) {
                tmp = sb.substring(sb.length() - 1).charAt(0);
                while ('(' != tmp) {
                    res.append(" ").append(tmp);
                    sb.setLength(sb.length() - 1);
                    tmp = sb.substring(sb.length() - 1).charAt(0);
                }
                sb.setLength(sb.length() - 1);
            } else
                res.append(buffer);
        }
        while (sb.length() > 0) {
            res.append(" ").append(sb.substring(sb.length() - 1));
            sb.setLength(sb.length() - 1);
        }
        return res.toString();
    }

    private boolean opr(char x) {
        return x == '+' || x == '-' || x == '*' || x == '/';
    }


    private int priority(char x) {
        if (x == '*' || x == '/')
            return 2;
        return 1;
    }

}
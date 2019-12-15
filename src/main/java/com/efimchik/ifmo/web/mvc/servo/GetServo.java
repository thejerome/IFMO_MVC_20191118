package com.efimchik.ifmo.web.mvc.servo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.el.ELProcessor;
import javax.servlet.http.HttpSession;


@Controller
public class GetServo {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getCalced(HttpSession s) {
        String equation = (String) s.getAttribute("equation");

        if (equation == null) {
            return new ResponseEntity<>("No equation", HttpStatus.valueOf(409));
        }

        String result;
        equation = equation.replaceAll("\\s", "");

        try {
            result = calcit(equation, s);
        } catch (Exception e) {
            return new ResponseEntity<>("Undefined varibables/another error", HttpStatus.valueOf(409));
        }

        return new ResponseEntity<>(result, HttpStatus.valueOf(200));
    }


    private boolean CheckHatAlpha(String str) {
        char[] buff = str.toCharArray();
        for (char x : buff) {
            if (x <= 'z' && x >= 'a')
                return true;
        }
        return false;
    }

    public static int floored(double x) {
        return (int) Math.floor(Math.abs(x));
    }

    private int eval(String str) throws NoSuchMethodException, ClassNotFoundException {
        ELProcessor elp = new ELProcessor();
        elp.defineFunction("", "floored", "com.efimchik.ifmo.web.mvc.servo.GetServo", "floored");
        Integer name = (Integer) elp.getValue(str, Integer.TYPE);
        return (int) name;
    }


    private int signof(int x) {
        if (x > 0) return 1;
        if (x < 0) return -1;
        return 0;
    }

    private String calcit(String str2, HttpSession s) throws IllegalArgumentException {
        String str = str2;
        int limit = 5;
        while (CheckHatAlpha(str) && limit > 0) {
            for (char x = 'a'; x <= 'z'; x++) {
                str = str.replaceAll(String.valueOf(x), "(" + (String) s.getAttribute(String.valueOf(x)) + ")");
            }
            limit--;
        }

        String str_ = str.replaceAll("[(]", "floored(");
        try {
            int evaled = eval(str);
            int evaled2 = eval(str_);
            return String.valueOf(evaled2 * signof(evaled));
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

}
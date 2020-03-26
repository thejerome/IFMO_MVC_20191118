package com.efimchik.ifmo.web.mvc;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@SessionScope
public class Equation {

    private String equation;

    private final Map<String, String> params = new HashMap<>();

    public String getParam(String param) {
        return params.get(param);
    }

    public String setParam(String param, String value) {
        if (!Pattern.matches("^[a-z]$", param)) {
            throw new IllegalArgumentException("param");
        }

        if (value == null) {
            return params.remove(param);
        }

        if (!Pattern.matches("^[a-z]|-?\\d+$", value)) {
            throw new IllegalArgumentException("value");
        }

        try {
            final long numeric = Long.parseLong(value);

            if (numeric < -10000 || numeric > 10000) {
                throw new OutOfRangeException("value");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return params.put(param, value);
    }

    public String getEquation() {
        return equation;
    }

    public String setEquation(String equation) {
        final String previous = this.equation;

        if (equation != null) {
            int counter = 0;
            for (char c : equation.toCharArray()) {
                if (c == '(') {
                    ++counter;
                } else if (c == ')') {
                    --counter;
                }
            }

            if (counter != 0 || !Pattern.matches("(([a-z]|-?\\d+)[+*/-])*([a-z]|-?\\d+)", equation.replaceAll("[()]", ""))) {
                throw new IllegalArgumentException("equation");
            }
        }

        this.equation = equation;
        return previous;
    }
}
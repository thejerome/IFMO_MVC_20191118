package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

// Что делать?? Берем сервлеты, меняем им сервлет-шапочки на спринг-шапочки и вместо resp.setStatus возвращаем ResponseEntity

@Controller
public class ResultController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session) {
        try {
            if (session.getAttribute("equation") == null)
                throw new IllegalArgumentException("Equation is missing!");

            String equation = session.getAttribute("equation").toString().replace(" ", "");

// Собираем переменные в мапу
            Map<String, String> sessionVars = new HashMap();
            Map<String, String> eqVars = new HashMap();

            varsToMaps(session, equation, sessionVars, eqVars);
            replaceValues(eqVars, sessionVars);

// Вставляем значения переменных в выражение
            for (int i = 0; i < equation.length(); i++) {
                if (equation.charAt(i) >= 'a' && equation.charAt(i) <= 'z')
                    equation = equation.replace(Character.toString(equation.charAt(i)),
                            eqVars.get(Character.toString(equation.charAt(i))));
            }

// Вычисляем и выводим результат выражения
            return new ResponseEntity<>(Integer.toString(CalculatorUtil.calculate(equation)), HttpStatus.valueOf(200));
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(409));
        }
    }

    private void varsToMaps(HttpSession session, String equation, Map<String, String> sessionVars, Map<String, String> eqVars) {

        Enumeration<String> attributes = session.getAttributeNames();

        while (attributes.hasMoreElements()) {
            String var = attributes.nextElement();
            sessionVars.put(var, session.getAttribute(var).toString());
        }
        sessionVars.remove("equation");

        for (int i = 0; i < equation.length(); i++) {
            if (equation.charAt(i) >= 'a' && equation.charAt(i) <= 'z')
                eqVars.put(Character.toString(equation.charAt(i)), "");
        }
    }

    private void replaceValues (Map<String, String> eqVars, Map<String, String> sessionVars)
            throws IllegalArgumentException {

        for (Map.Entry<String, String> var : eqVars.entrySet()) {
            String key = var.getKey();
            String val = sessionVars.get(key);

            if (val == null)
                throw new IllegalArgumentException("Value is missing!");

            while (!Pattern.matches("^[-0-9]+$", val)) {
                key = val;
                val = sessionVars.get(key);

                if (val == null)
                    throw new IllegalArgumentException("Value is missing!");
            }

            var.setValue(val);
        }
    }
}
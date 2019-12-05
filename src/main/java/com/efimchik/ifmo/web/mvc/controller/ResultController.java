package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.util.Parser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class ResultController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session) {

        String equation = (String) session.getAttribute("equation");
        String[] vars = (String[]) session.getValueNames();


        if (equation != null) {
            Map<String, Object> map = new HashMap<>();
            for (String s : vars) {
                if (!"equation".equals(s)) {
                    String value = (String) session.getAttribute(s);
                    if (value.matches("[a-zA-Z]+")) {
                        map.put(s, value);
                    } else {
                        int v = Integer.parseInt(value);
                        map.put(s, v);
                    }
                }
            }
            Set<String> variables = map.keySet();
            int i = 0;
            Map<String, String> matches = new HashMap<>();
            for (String s : variables) {
                matches.put(s, "x" + Integer.toString(i));
                i++;
            }

            for (String s : variables) {
                equation = equation.replaceAll(s, matches.get(s));
            }

            Integer test = 1;

            Map<String, Integer> mapa = new HashMap<>();
            for (String s : variables) {
                mapa.put(s, (int) (map.get(s).getClass() != test.getClass() ? map.get(map.get(s)) : map.get(s)));
            }

            double[] v = new double[variables.size()];
            i = 0;
            for (String s : variables) {
                v[i] = mapa.get(s);
                i++;
            }

            Parser estimator = new Parser();
            try {

                estimator.compile(equation);

                Integer result = (int) estimator.calculate(v);

                return new ResponseEntity<>(result.toString(), HttpStatus.valueOf(200));
            } catch (Exception e) {

                return new ResponseEntity<>("lack of data", HttpStatus.valueOf(409));
            }
        } else {

            return new ResponseEntity<>("null equation", HttpStatus.valueOf(409));
        }
    }

}

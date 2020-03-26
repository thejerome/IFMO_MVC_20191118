package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ResController {
    @RequestMapping(value = "/calc/result", method = RequestMethod.GET)
    public ResponseEntity<Integer> getResult(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> names = session.getAttributeNames();

        if(!names.hasMoreElements())
            return ResponseEntity.status(HttpStatus.valueOf(409)).body(409);
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, String.valueOf(session.getAttribute(name)));
        }

        String equation = map.get("equation");
        if (equation!=null) {
            try {
                String result = EquationSolver.getResult(equation, map);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(Integer.parseInt(result));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.valueOf(409)).body(409);
            }
        }
        return null;
    }
}
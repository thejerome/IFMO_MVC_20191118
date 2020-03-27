package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.calculator.SimpleCalculator;
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
@RequestMapping(value = "/calc/result")
public class SolverController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Integer> solveEquation(HttpSession session) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        try {
            if (!attributeNames.hasMoreElements()) return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                map.put(name, String.valueOf(session.getAttribute(name)));
            }
            return ResponseEntity.status(HttpStatus.valueOf(200)).body(Integer.parseInt(SimpleCalculator.getResult(map.get("equation"), map)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
        }
    }
}

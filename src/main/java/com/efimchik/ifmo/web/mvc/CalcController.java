package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CalcController {
    @GetMapping("/calc/result")
    public ResponseEntity<Integer> getResult(HttpSession session) {
        Map<String, String> hashMap = new HashMap<>();
        Enumeration<String> enumeration = session.getAttributeNames();

        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            hashMap.put(key, String.valueOf(session.getAttribute(key)));
        }

        String equation = hashMap.get("equation");
        try {
            if (equation != null) {
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(Integer.valueOf(new Calculator(equation, hashMap).solve()));
            } else {
                return ResponseEntity.status(HttpStatus.valueOf(409)).body(409);
            }
        } catch (Exception devisionByZeroForExample) {
            return ResponseEntity.status(HttpStatus.valueOf(409)).body(409);
        }
    }
}

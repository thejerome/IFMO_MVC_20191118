package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.calc.CalcUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/calc/result")
public class CalcController {
    @GetMapping()
    public ResponseEntity<String> getResult(HttpSession session) {
        if (session.getAttribute("equation") == null) {
            return new ResponseEntity<>("Hey man ya forgot to set equation", HttpStatus.CONFLICT);
        } else if (session.getAttribute("vars") == null && Pattern.matches("[a-z]", (String) session.getAttribute("equation"))) {
            return new ResponseEntity<>("Hey man ya forgot to provide enough variables", HttpStatus.CONFLICT);

        } else {
            String equation = (String) session.getAttribute("equation");
            Map<String, Object> varsMap = (HashMap<String, Object>) session.getAttribute("vars");
            try {
                int result = CalcUtil.parse(equation, varsMap);
                return new ResponseEntity<>(String.valueOf(result), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Hey man ya forgot to provide enough variables", HttpStatus.CONFLICT);
            }
        }
    }
}

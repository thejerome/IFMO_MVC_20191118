package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.algorithm.Calc;
import com.efimchik.ifmo.web.mvc.algorithm.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/calc")
public class CalcController {
    @GetMapping("/result")
    public ResponseEntity<String> getResult(HttpSession session) {
        Object attribute = session.getAttribute("expr");
        String body = "Lack of data!";
        int status = 200;
        if (attribute != null) {
            final Enumeration<String> attributeNames = session.getAttributeNames();
            Map<String, String> vars = new HashMap<>();
            while (attributeNames.hasMoreElements()) {
                String variableName = attributeNames.nextElement();
                vars.put(variableName, session.getAttribute(variableName).toString());
            }
            if (Validator.isValid(vars)) {
                System.out.println(vars);
                String equation = Calc.replaceVars(vars);
                int res = Calc.evaluate(equation);
                body = String.valueOf(res);
                return new ResponseEntity<>(body, HttpStatus.valueOf(status));
            }
        }
        status = 409;
        return new ResponseEntity<>(body, HttpStatus.valueOf(status));
    }

    @PutMapping("/equation")
    public ResponseEntity<String> putEquation(@RequestBody String expr, HttpSession session) {
        String body = "";
        int status;
        Object attribute = session.getAttribute("expr");
        if (Validator.isValid(expr)) {
            if (attribute == null) {
                status = 201;
            } else {
                status = 200;
            }
            session.setAttribute("expr", expr);
        } else {
            body = "Bad format";
            status = 400;
        }
        return new ResponseEntity<>(body, HttpStatus.valueOf(status));
    }

    @PutMapping("/{var}")
    public ResponseEntity<String> putVariable(@PathVariable(name = "var") String varName, @RequestBody String val, HttpSession session) {
        Object attribute = session.getAttribute(varName);
        String body = "";
        int validCode = Validator.validateValue(val);
        if (validCode == 200) {
            if (attribute == null) {
                validCode = 201;
            }
            session.setAttribute(varName, val);
        } else {
            body = "Bad format";
        }
        return new ResponseEntity<>(body, HttpStatus.valueOf(validCode));
    }

    @DeleteMapping("/{attr}")
    public ResponseEntity<String> deleteAttribute(@PathVariable(name="attr") String varName, HttpSession session) {
        session.removeAttribute(varName);
        return new ResponseEntity<>("DELETED", HttpStatus.valueOf(204));
    }
}

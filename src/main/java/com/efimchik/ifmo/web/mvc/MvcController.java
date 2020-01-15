package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Controller
@RequestMapping("/calc")
public class MvcController {

    @PutMapping(value = "/{path}")
    public ResponseEntity<String> putDispatcher(HttpSession httpSession, @PathVariable String path,
                                                @RequestBody String requestBody) {
        String body = requestBody.replace(" ", "");
        HttpStatus status = httpSession.getAttribute(path) == null ? HttpStatus.CREATED : HttpStatus.OK;
        if ("equation".equals(path)) {
            if (!isValidEquation(body)) {
                status = HttpStatus.BAD_REQUEST; // 400
            }
        } else if (isValidVarName(path)) {
            int isValid = isValidVarValue(body);
            if (isValid == 1) {
                status = HttpStatus.FORBIDDEN; // 403
            } else if (isValid == 2) {
                status = HttpStatus.BAD_REQUEST; // 400
            }
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        if (status.is2xxSuccessful()) {
            httpSession.setAttribute(path, body);
        }
        return new ResponseEntity<>(status.getReasonPhrase() + body, status);
    }

    private boolean isValidEquation(String equation) {
        Calc calc = new Calc();
        return calc.isCorrectEquation(equation);
    }

    private boolean isValidVarName(String varName) {
        return varName.length() == 1 && Character.isAlphabetic(varName.charAt(0));
    }

    private int isValidVarValue(String varValue) {
        try {
            int valueInt = Integer.parseInt(varValue);
            return valueInt >= -10000 && valueInt <= 10000 ? 0 : 1; // 0 - ok; 1 - exceeding value;
        } catch (NumberFormatException ignored) {
            if (isValidVarName(varValue)) {
                return 0; // ok
            } else {
                return 2; // bad format
            }
        }
    }

    @GetMapping(value = "/result")
    @ResponseBody
    public ResponseEntity<String> getAnswer(HttpSession httpSession) {
        HttpStatus status = HttpStatus.OK;
        if (httpSession.getAttribute("equation") != null) {
            String equation = httpSession.getAttribute("equation").toString();
            StringTokenizer stringTokenizer = new StringTokenizer(equation, "0123456789()+-*/");
            while (stringTokenizer.hasMoreElements()) {
                String some = stringTokenizer.nextToken();
                if (httpSession.getAttribute(some) == null) {
                    status = HttpStatus.CONFLICT; // 409
                    break;
                }
            }
        } else {
            status = HttpStatus.CONFLICT; // 409
        }

        String answer;
        if (status.is2xxSuccessful()) {
            // get equation with vars from session
            final Enumeration<String> attributeNames = httpSession.getAttributeNames();
            Map<String, String> attributesMap = new HashMap<>();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                attributesMap.put(attributeName, httpSession.getAttribute(attributeName).toString());
            }
            String equation = attributesMap.remove("equation");

            // replace vars with corresponding values
            for (Map.Entry<String, String> entry :
                    attributesMap.entrySet()) {
                String value = entry.getValue();
                if (isValidVarName(value)) {
                    value = attributesMap.get(value);
                }
                equation = equation.replace(entry.getKey(), value);
            }

            // solve the given equation
            Calc calc = new Calc();
            answer = String.valueOf(calc.getResult(equation));
        } else {
            answer = status.getReasonPhrase();
        }
        return new ResponseEntity<>(answer, status);
    }

    @DeleteMapping(path = "/{path}")
    public ResponseEntity<String> deleteDispatcher(HttpSession httpSession, @PathVariable String path) {
        httpSession.removeAttribute(path);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT.getReasonPhrase(), HttpStatus.NO_CONTENT); // 204
    }

}

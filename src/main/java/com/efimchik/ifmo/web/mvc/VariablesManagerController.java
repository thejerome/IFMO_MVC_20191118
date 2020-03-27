package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Controller
@RequestMapping(value = "/calc/{varName:[a-z]|equation}")
public class VariablesManagerController {
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<String> setVariable(@PathVariable String varName, @RequestBody String requestBody, HttpSession session) {
        if ("equation".equals(varName) && requestBody.matches("^[a-z0-9 ]*$"))
            return ResponseEntity.status(HttpStatus.valueOf(400)).body(null);
        else if (isNum(requestBody) && (Integer.parseInt(requestBody) > 10000 || Integer.parseInt(requestBody) < -10000))
            return ResponseEntity.status(HttpStatus.valueOf(403)).body(null);

        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            if (varName.equals(name)) {
                session.setAttribute(varName, requestBody);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(null);
            }
        }

        session.setAttribute(varName, requestBody);
        return ResponseEntity.status(HttpStatus.valueOf(201)).body(null);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Integer> deleteVar(@PathVariable String varName, HttpSession session) {

        if (session.getAttribute(varName) != null) {
            session.removeAttribute(varName);
            return ResponseEntity.status(HttpStatus.valueOf(204)).body(null);
        } return ResponseEntity.status(HttpStatus.valueOf(404)).body(null);
    }

    private static boolean isNum(String i) {
        if (i.charAt(0) == '-') return true;
        for (char ch: i.toCharArray()) {
            if (Character.isAlphabetic(ch)) return false;
        }
        return true;
    }
}

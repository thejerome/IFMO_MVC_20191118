package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class VarsController {

    @PutMapping("/calc/{variableName}")
    public ResponseEntity<String> setVariable(HttpSession session, @PathVariable String variableName, @RequestBody String value) {
        boolean isNumber = false;
        try {
            int varValue = Integer.parseInt(value);
            if (varValue < -10000 || varValue > 10000)
                return new ResponseEntity<>("Variable exceeds the range", HttpStatus.FORBIDDEN);
            isNumber = true;
        } catch (NumberFormatException ignored) {
        }
        if (session.getAttribute("vars") == null) {
            session.setAttribute("vars", new HashMap<String, Object>());
        }
        Map<String, Object> varsMap = (Map<String, Object>) session.getAttribute("vars");
        HttpStatus status;
        if (varsMap.containsKey(variableName))
            status = HttpStatus.OK;
        else
            status = HttpStatus.CREATED;
        varsMap.put(
                variableName,
                isNumber ? Integer.parseInt(value) : value
        );
        session.setAttribute("vars", varsMap);
        System.out.println(varsMap);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/calc/{variableName}")
    public ResponseEntity deleteVariable(HttpSession session, @PathVariable String variableName) {
        Map<String, Object> varsMap = (Map<String, Object>) session.getAttribute("vars");
        varsMap.remove(variableName);
        session.setAttribute("vars", varsMap);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}

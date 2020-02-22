package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.helpers.CalcHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class ValueController {
    @PutMapping("/calc/{key}")
    public ResponseEntity<String> setVar(@PathVariable String key, @RequestBody String bodyVal, HttpSession session) {
        ResponseEntity<String> responseEntity = null;
        String body = bodyVal.replaceAll("\\s","");
        if("equation".equals(key) && !CalcHelper.isExpression(body)) {
            return ResponseEntity.status(HttpStatus.valueOf(400)).body("bad format");
        } else if (CalcHelper.isInteger(body) && (Integer.parseInt(body) > 10000 || Integer.parseInt(body) < -10000)){
            return ResponseEntity.status(HttpStatus.valueOf(403)).body("bad value");
        }

        if (session.getAttribute(key) != null) {
            session.setAttribute(key, body);
            responseEntity = new ResponseEntity<>(HttpStatus.valueOf(200));
        } else {
            session.setAttribute(key, body);
            responseEntity = new ResponseEntity<>(HttpStatus.valueOf(201));
        }

        return responseEntity;
    }

    @DeleteMapping("/calc/{key}")
    public ResponseEntity<String> deleteVariable(@PathVariable String key, HttpSession session) {
        session.removeAttribute(key);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}

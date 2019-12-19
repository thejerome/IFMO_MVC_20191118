package com.efimchik.ifmo.web.mvc;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@Controller
public class WebController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> doGet(HttpSession session) {
        String equation = Service.map(session);
        try {
            return new ResponseEntity<>(Service.calculate(equation), HttpStatus.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.valueOf(409));
        }

    }

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEquation(HttpSession session, @RequestBody String equation) {
        if (Service.isNotEquation(equation)) {
            return new ResponseEntity<>("", HttpStatus.valueOf(400));
        } else {
            if (session.getAttribute("equation") == null) {
                session.setAttribute("equation", equation);
                return new ResponseEntity<>("", HttpStatus.valueOf(201));
            } else {
                session.setAttribute("equation", equation);
                return new ResponseEntity<>("", HttpStatus.valueOf(200));
            }
        }
    }


    @PutMapping("/calc/{name}")
    protected ResponseEntity<String> putVariable(HttpSession session, @PathVariable String name, @RequestBody String value) {
        if (!Service.isVar(value)) {
            return new ResponseEntity<>("", HttpStatus.valueOf(403));
        } else {
            if (session.getAttribute(name) == null) {
                session.setAttribute(name, value);
                return new ResponseEntity<>("", HttpStatus.valueOf(201));
            } else {
                return new ResponseEntity<>("", HttpStatus.valueOf(200));
            }
        }
    }


    @DeleteMapping("/calc/{name}")
    protected ResponseEntity deleteAttribute(HttpSession session, @PathVariable String name) {
        session.removeAttribute(name);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }


}

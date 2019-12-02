package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpSession;

@Controller
public class EquationController {

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEquation(HttpSession session, @RequestBody String equation) {
        if (isGoodFormatted(equation)) {
            Object oldValue = session.getAttribute("equation");
            session.setAttribute("equation", equation);
            if (oldValue != null)
                return new ResponseEntity<>("good, equation is saved", HttpStatus.valueOf(200));
            else
                return new ResponseEntity<>("good, equation is saved in first time", HttpStatus.valueOf(201));

        } else {
            return new ResponseEntity<>("Bad formatted equation", HttpStatus.valueOf(400));
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean isGoodFormatted(String body) {
        for (int i = 0; i < body.length(); ++i) {
            if (body.charAt(i) >= 'a' && body.charAt(i) <= 'z' &&
                    i < body.length() - 1 &&
                    (body.charAt(i + 1) >= 'a' && body.charAt(i + 1) <= 'z')) {
                return false;
            }
        }
        return true;
    }

}

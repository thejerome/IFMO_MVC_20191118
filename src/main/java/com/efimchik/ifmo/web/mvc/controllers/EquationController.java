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
        if (!isGoodFormatted(equation)) {
            return new ResponseEntity<>("Warning! It's badly formatted!", HttpStatus.BAD_REQUEST);
        } else {
            Object sessionAttribute = session.getAttribute("equation");
            session.setAttribute("equation", equation);
            if (sessionAttribute == null) {
                return new ResponseEntity<>("Good formatted!", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Not bad.", HttpStatus.OK);
            }
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.NO_CONTENT);
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

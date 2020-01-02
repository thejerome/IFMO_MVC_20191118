package com.efimchik.ifmo.web.mvc;

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
    public ResponseEntity<String> putEquation(HttpSession session, @RequestBody String newEquation) {

        boolean expressionFormatIsOk = false;
        for (int i=0; i < newEquation.length(); i++) {
            char tmp = newEquation.charAt(i);
            if ((tmp == '/') || (tmp == '*') || (tmp == '+') || (tmp == '-') || (tmp == '(') || (tmp == ')')) {
                expressionFormatIsOk = true;
                break;
            }
        }

        if (!expressionFormatIsOk) {
            return new ResponseEntity<>("Wrong equation!", HttpStatus.valueOf(400));
        }
        else {
            if (session.getAttribute("equation") == null) {
                session.setAttribute("equation", newEquation);
                return new ResponseEntity<>("Equation created!", HttpStatus.valueOf(201));
            }
            else {
                session.setAttribute("equation", newEquation);
                return new ResponseEntity<>("Equation replaced!", HttpStatus.valueOf(200));
            }
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity<String> deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity<>("Equation deleted", HttpStatus.valueOf(204));
    }
}

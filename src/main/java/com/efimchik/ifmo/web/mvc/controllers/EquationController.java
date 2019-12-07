package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/calc/equation")
public class EquationController {

    @PutMapping()
    public ResponseEntity<String> setEquation(HttpSession session, @RequestBody String equation) {
        if (isGoodFormatted(equation)) {
            if (session.getAttribute("equation") == null) {
                session.setAttribute("equation", equation);
                return new ResponseEntity<>("Nice new expression ya put here, ama glad to meet ya btw", HttpStatus.CREATED);
            }
            else {
                session.setAttribute("equation", equation);
                return new ResponseEntity<>("Glad to see old friend with the new expression", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("Nah, ya expression is too bad for me", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping()
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private boolean isGoodFormatted(String expression) {
        Matcher lettersMatcher = Pattern.compile("[A-Z]+").matcher(expression);
        Matcher operationsMatcher = Pattern.compile("[-+*/]").matcher(expression);
        return !lettersMatcher.find() && operationsMatcher.find();
    }
}

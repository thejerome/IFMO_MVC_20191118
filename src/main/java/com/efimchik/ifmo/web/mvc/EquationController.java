package com.efimchik.ifmo.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class EquationController {

    @RequestMapping(value = "/calc/result", method = RequestMethod.GET)
    public ResponseEntity<String> getResult(HttpSession session) {
        return Equation.calculateResult(session);
    }

    @RequestMapping(value = "/calc/{key}", method = RequestMethod.PUT)
    public ResponseEntity setVariable(HttpSession session, @PathVariable String key, @RequestBody String variable) {
        int status;
        char symbol = variable.charAt(0);

        if ((symbol >= 'a' && symbol <= 'z') ||
                (Integer.parseInt(variable) < 10000 && Integer.parseInt(variable) > -10000)) {
            if (session.getAttribute(key) != null) {
                status = 200;
                session.setAttribute(key,variable);
            } else {
                status = 201;
                session.setAttribute(key,variable);
            }
        } else {
            status = 403;
        }

        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    @RequestMapping(value = "/calc/equation", method = RequestMethod.PUT)
    public ResponseEntity putEquation(HttpSession session, @RequestBody String equation) {
        int status;
        boolean consistOfDel = false;

        for(int i = 0; i < equation.length(); i++){
            char symbol = equation.charAt(i);
            if (symbol == '(' || symbol == ')' ||
                    symbol == '+' || symbol == '-' ||
                    symbol == '*' || symbol == '/') {
                consistOfDel = true;
                break;
            }
        }

        if (!consistOfDel) {
            status = 400;
        } else if (session.getAttribute("equation") != null) {
            status = 200;
            session.setAttribute("equation", equation);
        } else {
            status = 201;
            session.setAttribute("equation", equation);
        }
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    @RequestMapping(value = "/calc/{key}", method = RequestMethod.DELETE)
    public ResponseEntity deleteVariable(HttpSession session, @PathVariable String key) {
        session.removeAttribute(key);
        int status = 204;
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    @RequestMapping(value = "/calc/equation", method = RequestMethod.DELETE)
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        int status = 204;
        return new ResponseEntity(HttpStatus.valueOf(status));
    }
}

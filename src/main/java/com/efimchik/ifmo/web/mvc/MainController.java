package com.efimchik.ifmo.web.mvc;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class MainController {

    @RequestMapping(value = "/calc/equation", method = RequestMethod.PUT)
    public ResponseEntity putEquation(HttpSession session, @RequestBody String equation) {

        int status;
        boolean consistOfDel = false;
        for (int i = 0; i < equation.length(); i++) {
            if (equation.charAt(i) == '/' || equation.charAt(i) == '*' || equation.charAt(i) == '-' || equation.charAt(i) == '+' || equation.charAt(i) == '(' || equation.charAt(i) == ')')
                consistOfDel = true;
        }
        if (!consistOfDel) {
            status = 400;
        } else {
            if (session.getAttribute("equation") != null) {
                status = 200;
            } else {
                status = 201;
            }
            session.setAttribute("equation", equation);
        }
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    @RequestMapping(value = "/calc/equation", method = RequestMethod.DELETE)
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        ResponseEntity res = new ResponseEntity(HttpStatus.valueOf(204));
        return res;
    }

    @RequestMapping(value = "/calc/result", method = RequestMethod.GET)
    public ResponseEntity<String> getResult(HttpSession session) {
        return Calculate.calculateResult(session);

    }

    @RequestMapping(value = "/calc/{key}", method = RequestMethod.PUT)
    public ResponseEntity setVariable(HttpSession session, @PathVariable String key, @RequestBody String variable) {

        int status;

        if ((variable.charAt(0) >= 'a' && variable.charAt(0) <= 'z') || (Integer.valueOf(variable) > -10000 && Integer.valueOf(variable) < 10000) ) {
            if (session.getAttribute(key) != null) {
                status = 200;
            } else {
                status = 201;
            }
            session.setAttribute(key,variable);
        } else {
            status = 403;
        }
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    @RequestMapping(value = "/calc/{key}", method = RequestMethod.DELETE)
    public ResponseEntity deleteVariable(HttpSession session, @PathVariable String key) {
        session.removeAttribute(key);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }


}

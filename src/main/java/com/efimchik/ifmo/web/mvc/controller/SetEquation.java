package com.efimchik.ifmo.web.mvc.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class SetEquation {
    @RequestMapping(value = "/calc/equation", method = RequestMethod.PUT)
    public ResponseEntity setEquation(HttpSession session, @RequestBody String newEquation) {

        int statusCode = -1;

        if ((newEquation.indexOf('+') == -1) && (newEquation.indexOf('/') == -1) &&
                (newEquation.indexOf('/') == -1) && (newEquation.indexOf('*') == -1)) {
            statusCode = 400;
        }

        if (((statusCode != -1) && (statusCode < 300)) || (statusCode == -1)) {
                final String oldEquation = (String) session.getAttribute("equation");
                session.setAttribute("equation", newEquation);
                return new ResponseEntity(HttpStatus.valueOf((oldEquation == null) ? 201 : 200));
        } else {
            return new ResponseEntity(HttpStatus.valueOf(statusCode));
        }
    }

    @RequestMapping(value = "/calc/equation", method = RequestMethod.DELETE)
    public ResponseEntity deleteEquation(HttpSession session){
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }


}

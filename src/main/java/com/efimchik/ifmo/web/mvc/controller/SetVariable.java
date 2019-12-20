package com.efimchik.ifmo.web.mvc.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class SetVariable {

    @RequestMapping(value = "/calc/{variableName}", method = RequestMethod.PUT)
    public ResponseEntity setVariable(HttpSession session, @PathVariable String variableName, @RequestBody String value){
        // отправляем отчёт
        int statusCode = -1;

        if (value != null) {
            while ((value.charAt(0) >= 'a') && (value.charAt(0) <= 'z')) {
                value = (String) session.getAttribute(value);
            }

            if ((Integer.valueOf(value) < -10000) || (Integer.valueOf(value) > 10000)) {
                statusCode = 403;
            }
        }
        else{
            statusCode = 204;
        }

        if (((statusCode != -1) && (statusCode < 300)) || (statusCode == -1)) {
            ResponseEntity re = new ResponseEntity(HttpStatus.valueOf((String)session.getAttribute(variableName) == null ? 201:200));
            session.setAttribute(variableName, value);
            return re;
        } else {
            return new ResponseEntity(HttpStatus.valueOf(statusCode));
        }
    }

    @RequestMapping(value = "/calc/{variableName}", method = RequestMethod.DELETE)
    public ResponseEntity deleteVariables(HttpSession session, @PathVariable String variableName){
        session.removeAttribute(variableName);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

}

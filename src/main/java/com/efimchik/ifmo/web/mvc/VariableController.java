package com.efimchik.ifmo.web.mvc;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpSession;

import static java.lang.Character.isLetter;

@Controller
public class VariableController {

    @PutMapping("/calc/{name}")
    public ResponseEntity<String> doPut(HttpSession thisSession, @PathVariable String name, @RequestBody String value){
        if( value != null && !goodFormatValue(value) )
                return new ResponseEntity<>("Значение переменной вышло за допускаемое",HttpStatus.valueOf(403));
            else {
                if (thisSession.getAttribute(name) != null)
                    return new ResponseEntity<>(HttpStatus.valueOf(200));
                else {
                    thisSession.setAttribute(name,value);
                    return new ResponseEntity<>(HttpStatus.valueOf(201));
                }
            }
    }


    private boolean goodFormatValue(String value) {
        Character symbol = value.charAt(0);
        if (isLetter(symbol) && value.length() == 1)
            return true;
        try {
            int a = Integer.parseInt(value);
            return Math.abs(a) <= 10000;
        } catch (Exception exp){
            return true;
        }
    }

    @DeleteMapping("/calc/{name}")
    public ResponseEntity doDelete(HttpSession thisSession, @PathVariable String name){
        thisSession.setAttribute(name,null);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }


}

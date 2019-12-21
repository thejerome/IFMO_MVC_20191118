package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class VarController {

    @PutMapping("/calc/{varName}")
    public ResponseEntity<String> putVar(HttpSession session, @PathVariable String varName, @RequestBody String value) {
        if (variableCheck(value)) {
            Object oldOne = session.getAttribute(varName);
            session.setAttribute(varName, value);
            if (oldOne != null) {
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            }
            else {
                return new ResponseEntity<>(HttpStatus.valueOf(201));
            }
        } else {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }
    }

    @DeleteMapping("/calc/{varName}")
    public ResponseEntity deleteVar(HttpSession session, @PathVariable String varName) {
        session.removeAttribute(varName);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private static boolean variableCheck(String expr){
        if (expr.charAt(0) >= 'a' && expr.charAt(0) <= 'z' && expr.length() == 1) return true;
        try {
            int num = Integer.parseInt(expr);
            return num <= 10000 && num >= -10000;
        }
        catch (Exception ex){ return false;}
    }

}

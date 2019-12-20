package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;


@Controller
public class VarsController {

    @PutMapping("/calc/{var}")
    public ResponseEntity doPut(HttpSession session, @PathVariable String var, @RequestBody String val){
        if (correctValue(val)) {
            if (session.getAttribute(var) == null) {
                session.setAttribute(var, val);
                return new ResponseEntity(HttpStatus.valueOf(201));
            } else {
                session.setAttribute(var, val);
                return new ResponseEntity(HttpStatus.valueOf(200));
            }
        } else {
            return new ResponseEntity("value isn't correct or out of range", HttpStatus.valueOf(403));
        }
    }


    @DeleteMapping("/calc/{var}")
    public ResponseEntity doDelete(HttpSession session, @PathVariable String var) {
        session.removeAttribute(var);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }



    private boolean correctValue(String val) {
        return (val.charAt(0) >= 'a' && val.charAt(0) <= 'z') || (Integer.parseInt(val) >= -10000 && Integer.parseInt(val) <= 10000);
    }
}

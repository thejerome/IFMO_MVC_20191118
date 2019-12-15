package com.efimchik.ifmo.web.mvc.servo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class VarServo {

    @PutMapping("/calc/{varName}")
    public ResponseEntity<String> putV(HttpSession session, @PathVariable String varName, @RequestBody String value) {
        if (!properV(value)) {
            return new ResponseEntity<>("Bad request", HttpStatus.valueOf(403));
        }

        Object oldValue = session.getAttribute(varName);
        session.setAttribute(varName, value);
        if (oldValue != null) {
            return new ResponseEntity<>("OK", HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity<>("OK, init", HttpStatus.valueOf(201));
        }

    }

    @DeleteMapping("/calc/{varName}")
    public ResponseEntity delV(HttpSession session, @PathVariable String varName) {
        session.removeAttribute(varName);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean properV(String str) {
        if (str.matches("[a-zA-Z]") && str.length() == 1)
            return true;
        int res = Integer.parseInt(str);
        return res >= -10000 && res <= 10000;
    }

}

package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;


@Controller
public class VarController {
    @RequestMapping(value = "/calc/{var:[a-z]|equation}", method = RequestMethod.PUT)
    public ResponseEntity set(@PathVariable String var, @RequestBody String body, HttpSession session) {

        if ("equation".equals(var) && body.matches("^[A-Za-z ]*$")) {
            return ResponseEntity.status(HttpStatus.valueOf(400)).body("invalid");
        } else if (Calculator.isDigit(body) && (Integer.parseInt(body) > 10000 || Integer.parseInt(body) < -10000)) {
            return ResponseEntity.status(HttpStatus.valueOf(403)).body("invalid");
        }
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            if (var.equals(name)) {
                session.setAttribute(var, body);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(null);
            }
        }
        session.setAttribute(var, body);
        return ResponseEntity.status(HttpStatus.valueOf(201)).body(null);
    }

    @RequestMapping(value = "/calc/{var:[a-z]|equation}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String var, HttpSession session) {
        session.removeAttribute(var);
        return ResponseEntity.status(HttpStatus.valueOf(204)).body(null);
    }
}

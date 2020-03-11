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
public class VarsController {
    @RequestMapping(value = "/calc/{url_key:[a-z]|equation}",
                    method = RequestMethod.PUT)
    public ResponseEntity<Integer> putValue(@PathVariable String url_key,
                                            @RequestBody String body,
                                            HttpSession session) {

        String val = body.replaceAll(" ","");

        if("equation".equals(url_key) && !Calculator.validation(val)) {
            return ResponseEntity.status(HttpStatus.valueOf(400)).body(400);
        } else if (Calculator.isNumeric(val) && !(Integer.parseInt(val) < 10001 && Integer.parseInt(val) > -10001)){
            return ResponseEntity.status(HttpStatus.valueOf(403)).body(403);
        }

        Enumeration<String> enumeration = session.getAttributeNames();

        while (enumeration.hasMoreElements()) {
            if(url_key.equals(enumeration.nextElement())) {
                session.setAttribute(url_key, val);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(200);
            }
        }

        session.setAttribute(url_key,val);
        return ResponseEntity.status(HttpStatus.valueOf(201)).body(201);
    }

    @RequestMapping(value = "/calc/{url_key:[a-z]|equation}", method = RequestMethod.DELETE)
    public ResponseEntity<Integer> delVariable(@PathVariable String url_key, HttpSession session) {
        session.removeAttribute(url_key);
        return ResponseEntity.status(HttpStatus.valueOf(204)).body(204);
    }
}
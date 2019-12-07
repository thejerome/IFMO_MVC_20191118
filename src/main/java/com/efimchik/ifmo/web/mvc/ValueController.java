package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;


@Controller
@ResponseBody
public class ValueController {
    @RequestMapping(value = "/calc/{key:[a-z]|equation}",
                    method = RequestMethod.PUT)
    public ResponseEntity setVariable(@PathVariable String key,
                                      @RequestBody String bodyValue,
                                      HttpSession session) {

        String body = bodyValue.replaceAll("\\s","");
        if("equation".equals(key) && !EvalHelper.isExpression(body)) {
            return ResponseEntity.status(HttpStatus.valueOf(400)).body("bad format");
        } else if (EvalHelper.isInteger(body) && (Integer.parseInt(body) > 10000 || Integer.parseInt(body) < -10000)){
            return ResponseEntity.status(HttpStatus.valueOf(403)).body("bad value");
        }

        Enumeration<String> e = session.getAttributeNames();
        boolean valueExists = false;

        if(e.hasMoreElements()) {
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                if(key.equals(name)) {
                    valueExists = true;
                    session.setAttribute(key, body);
                    return ResponseEntity.status(HttpStatus.valueOf(200)).body(key + ": " + body);
                }
            }
        }
        if(!valueExists) {
            session.setAttribute(key,body);
            return ResponseEntity.status(HttpStatus.valueOf(201)).body(key + ": " + body);
        }
        return null;
    }

    @RequestMapping(value = "/calc/{key:[a-z]|equation}",
            method = RequestMethod.DELETE)
    public ResponseEntity setVariable(@PathVariable String key,
                                      HttpSession session) {
        session.removeAttribute(key);
        return ResponseEntity.status(HttpStatus.valueOf(204)).body(null);
    }
}

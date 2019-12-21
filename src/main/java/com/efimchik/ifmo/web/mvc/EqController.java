package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpSession;

@Controller
public class EqController {

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEquation(HttpSession session, @RequestBody String equation) {
        if (!equationCheck(equation)) {
            Object oldOne = session.getAttribute("equation");
            session.setAttribute("equation", equation);
            if (oldOne != null)
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            else
                return new ResponseEntity<>(HttpStatus.valueOf(201));

        } else {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private static boolean equationCheck(String expr){
        for (int i = 0; i < expr.length() - 1; i++)
            if('a' <= expr.charAt(i) && expr.charAt(i) <= 'z' && 'a' <= expr.charAt(i+1) && expr.charAt(i+1) <= 'z')
                return true;
        return false;
    }


}

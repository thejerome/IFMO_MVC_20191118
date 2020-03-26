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
public class VariableController {
    @RequestMapping(value = "/calc/{urlPart:[a-z]|equation}", method = RequestMethod.PUT)
    public ResponseEntity<String> putIntoSession(@PathVariable String urlPart, @RequestBody (required = true) String value, HttpSession session) {

        if ("equation".equals(urlPart) && value.matches("^[a-z ]*$")) return ResponseEntity.status(HttpStatus.valueOf(400)).body(String.valueOf(400));
        else if (EquationSolver.isNumeric(value) && (Integer.parseInt(value) > 10000 || Integer.parseInt(value) < -10000))
            return ResponseEntity.status(HttpStatus.valueOf(403)).body(String.valueOf(403));
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (urlPart.equals(name)) {
                session.setAttribute(urlPart, value);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(null);
            }
        }
        session.setAttribute(urlPart, value);
        return ResponseEntity.status(HttpStatus.valueOf(201)).body(null);
    }

    @RequestMapping(value = "/calc/{urlPart:equation|[a-z]}", method = RequestMethod.DELETE)
    public ResponseEntity<Integer> deleteFromSession(@PathVariable String urlPart, HttpSession session) {
        if (session.getAttribute(urlPart) != null) {
            session.removeAttribute(urlPart);
            return ResponseEntity.status(HttpStatus.valueOf(204)).body(204);
        } else return ResponseEntity.status(HttpStatus.valueOf(404)).body(404);
    }
}


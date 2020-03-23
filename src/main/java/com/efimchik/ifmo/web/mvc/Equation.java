package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/calc/equation")
public class Equation {
    @PutMapping()
    public ResponseEntity<String> put(@RequestBody String eq, HttpSession ses) {
        if (!ok(eq)) return new ResponseEntity<>("bad", HttpStatus.BAD_REQUEST);
        if (ok(eq)) {
            if (ses.getAttribute("eq") == null) {
                ses.setAttribute("eq", eq);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            else {
                ses.setAttribute("eq", eq);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return null;
    }

    @DeleteMapping()
    public ResponseEntity del(HttpSession ses) {
        ses.removeAttribute("eq");
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private boolean ok(String eq) {
        return checkEquation(eq);
    }

    private boolean checkEquation(String eq) {
        boolean op = false;
        for (char c : eq.toCharArray()) {
            if (c >= 'A' && c <= 'Z')
                return false;
            if (c == '+' || c == '-' || c == '/' || c == '*')
                op = true;
        }
        return op;
    }
}

package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/calc/equation")
public class EquationController {
    @PutMapping()
    public ResponseEntity<String> doPut(HttpSession sesh, @RequestBody String equation) {
        if (!badRequest(equation)) {
            if (sesh.getAttribute("equation") == null) {
                sesh.setAttribute("equation", equation);
                return new ResponseEntity<>("CREATED", HttpStatus.CREATED);
            }
            else {
                sesh.setAttribute("equation", equation);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }
        else {
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping()
    public ResponseEntity doDelete(HttpSession sesh) {
        sesh.removeAttribute("equation");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean badRequest(String equation) {
        int operators = 0;
        for (int i = 0; i < equation.length(); i++) {
            char elem = equation.charAt(i);
            if ('A' <= elem && elem <= 'Z' ) {
                return true;
            }
            if (elem == '+' || elem == '-' || elem == '/' || elem == '*') {
                operators++;
            }
        }
        if (operators == 0) {
            return true;
        }
        return false;
    }
}
package com.efimchik.ifmo.web.mvc;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpSession;
import static org.springframework.http.HttpStatus.valueOf;

@Controller
public class EquationContr {

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEquation(HttpSession session, @RequestBody String equation) {
        if (equationCheck(equation)) {
            return new ResponseEntity<>(valueOf(400));
        } else {
            Object old;
            old = session.getAttribute("equation");
            session.setAttribute("equation", equation);
            if (old != null)
                return new ResponseEntity<>(valueOf(200));
            else
                return new ResponseEntity<>(valueOf(201));

        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity<String> deleteEquation(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity<>(valueOf(204));
    }

    private static boolean equationCheck(String expr){
        for (int i = 0; i < expr.length() - 1; i++)
            if (expr.charAt(i) <= 'z')
                if (expr.charAt(i) >= 'a' && expr.charAt(i + 1) >= 'a' && expr.charAt(i + 1) <= 'z') {
                    return true;
                }
        return false;
    }


}
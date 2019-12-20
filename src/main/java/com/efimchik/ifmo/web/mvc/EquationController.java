package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpSession;

import static java.lang.Character.isLetter;

@Controller
public class EquationController {

    @PutMapping("/calc/equation")
    public ResponseEntity<String> doPut (HttpSession thisSession, @RequestBody String equation){
        if (!goodFormatEquation(equation)) {
            return new ResponseEntity<>("Неверный формат", HttpStatus.valueOf(400));
        }
        else{
            if (thisSession.getAttribute("equation") == null){
                thisSession.setAttribute("equation", equation);
                return new ResponseEntity<>("Новое выражение", HttpStatus.valueOf(201));
            }
            else {
                thisSession.setAttribute("equation", equation);
                return new ResponseEntity<>("Выражение было изменено", HttpStatus.valueOf(200));
            }
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity<String> doDelete(HttpSession thisSession){
        thisSession.setAttribute("equation", null);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean goodFormatEquation(String equation) {
        for (int i = 0; i < equation.length() - 1; i++) {
            if (isLetter(equation.charAt(i)) && isLetter(equation.charAt(i + 1)))
                return false;
        }
        return true;
    }
}

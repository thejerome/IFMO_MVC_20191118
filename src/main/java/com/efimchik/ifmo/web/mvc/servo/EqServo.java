package com.efimchik.ifmo.web.mvc.servo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class EqServo {

    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEq(HttpSession s, @RequestBody String eq) {
        if (!properEq(eq)) {
            return new ResponseEntity<>("Bad request", HttpStatus.valueOf(400));
        }

        Object oldValue = s.getAttribute("equation");
        s.setAttribute("equation", eq);
        if (oldValue != null)
            return new ResponseEntity<>("OK, save", HttpStatus.valueOf(200));
        else
            return new ResponseEntity<>("OK, init save", HttpStatus.valueOf(201));
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity delEq(HttpSession s) {
        s.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean properEq(String str) {
        Matcher matcher = Pattern.compile("[A-Z]+").matcher(str);
        Matcher matcher1 = Pattern.compile("[-+*/]").matcher(str);
        return !matcher.find() && matcher1.find();
    }

}

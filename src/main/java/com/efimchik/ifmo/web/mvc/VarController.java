package com.efimchik.ifmo.web.mvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class VarController {

    public static boolean ifCorrect(String s)
    {
        if (((Integer.valueOf(s)*Integer.valueOf(s)<100000000)||(s.charAt(0) >= 'a' && s.charAt(0) <= 'z')))
        {
            return true;
        }
        else
        {
            return  false;
        }
    }
    @PutMapping("/calc/{n}")
    public ResponseEntity<String> putVariable(HttpSession s, @PathVariable String n, @RequestBody String val) {
        if (!ifCorrect(val))
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        if (s.getAttribute(n) != null) {
            s.setAttribute(n, val);
            return new ResponseEntity<>(HttpStatus.valueOf(200));

        } else {
            s.setAttribute(n, val);
            return new ResponseEntity<>(HttpStatus.valueOf(201));
        }
    }
    public static boolean ifEqCorrect (String s)
    {
        if (s.indexOf('*') != -1 || s.indexOf('/') != -1 || s.indexOf('+') != -1 || s.indexOf('-') !=-1)
        {
            return  true;
        }
        else
        {
            return false;
        }
    }
    @DeleteMapping("/calc/{n}")
    public ResponseEntity deleteVariable(HttpSession s, @PathVariable String n) {
        s.removeAttribute(n);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}
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
    public static boolean ifEqCorrect (String s)
    {
        if (s.indexOf('*') != -1 || s.indexOf('/') != -1 || s.indexOf('+') != -1 || s.indexOf('-') != -1)
        {
            return  true;
        }
        else
        {
            return false;
        }
    }
    @PutMapping("/calc/eq")
    public ResponseEntity<String> putEquation(HttpSession s, @RequestBody String eq) {

        if (!ifEqCorrect(eq))
            return new ResponseEntity<>("That was  a bad thingy", HttpStatus.valueOf(400));

        if (s.getAttribute("equation") != null) {
            s.setAttribute("equation", eq);
            return new ResponseEntity<>("Replaced an equation", HttpStatus.valueOf(200));
        }
        else {
            s.setAttribute("equation", eq);
            return new ResponseEntity<>("Created an equation", HttpStatus.valueOf(201));
        }
    }
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
    @DeleteMapping("/calc/eq")
    public ResponseEntity deleteEquation(HttpSession s){
        s.removeAttribute("equation");
        return new ResponseEntity<>("Wiped out, diverged to atoms", HttpStatus.valueOf(204));
    }

}
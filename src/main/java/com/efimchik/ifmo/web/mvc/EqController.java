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

    public static boolean checkIfGood (String s)
    {
        if ((s.indexOf('+') == 1 || s.indexOf('-') == 1 || s.indexOf('*') == 1 || s.indexOf('/') == 1))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    @PutMapping("/calc/equation")
    public ResponseEntity<String> putEq(HttpSession s, @RequestBody String eq)
    {

        if (!checkIfGood(eq)
        {
            return new ResponseEntity<>("That equation is meh", HttpStatus.valueOf(400));
        }

        if (session.getAttribute("equation"))
        {
            s.setAttribute("equation", eq);
            return new ResponseEntity<>("Corrected an equation", HttpStatus.valueOf(200));
        }
        else
            {
                s.setAttribute("equation", eq);
                return new ResponseEntity<>("Created an equation", HttpStatus.valueOf(201));
        }
    }
    @DeleteMapping("/calc/equation")
    public ResponseEntity delEq(HttpSession s){
        s.removeAttribute("equation");
        return new ResponseEntity<>("Efimchik forces me to code java, please help", HttpStatus.valueOf(204));
    }
}
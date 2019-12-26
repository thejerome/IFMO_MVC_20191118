package com.efimchik.ifmo.web.mvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class VarController {

    public boolean checkIfGood (String val)
    {
        if (((Integer.valueOf(val*val)<100000000)||((val.charAt(0) >= 'a' && val.charAt(0) <= 'z'))))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    @PutMapping("/calc/{name}")
    public ResponseEntity<String> editVar(@PathVariable String n, @RequestBody String val, HttpSession s) {
        if (!checkIfGood(val))
        {
            return new ResponseEntity<>("That's a bad thingy", HttpStatus.valueOf(403));
        }
        if (s.getAttribute(n))
        {
            s.setAttribute(n, val);
            return new ResponseEntity<>("Replaced a var", HttpStatus.valueOf(200));
        }
        else
            {
                s.setAttribute(n, val);
                return new ResponseEntity<>("Created a variable", HttpStatus.valueOf(201));
        }

    }
    @DeleteMapping("/calc/{name}")
    public ResponseEntity clearVar(HttpSession s, @PathVariable String n) {
        s.removeAttribute(n);
        return new ResponseEntity<>("Deleted var, blood for the blood god", HttpStatus.valueOf(204));
    }
}

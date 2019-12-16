package com.efimchik.ifmo.web.mvc;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class VariableManager
{
    @PutMapping("/calc/{var}")
    public ResponseEntity<String> doPut(HttpSession s, @PathVariable String var, @RequestBody String val)
    {

        if (!((val.charAt(0) >= 'a' && val.charAt(0) <= 'z') || (Integer.valueOf(val) > -10000 && Integer.valueOf(val) < 10000)))
            return new ResponseEntity<>(HttpStatus.valueOf(403));

        if (s.getAttribute(var) != null)
        {
            s.setAttribute(var, val);
            return new ResponseEntity<>(HttpStatus.valueOf(200));
        } else
        {
            s.setAttribute(var, val);
            return new ResponseEntity<>(HttpStatus.valueOf(201));
        }
    }

    @DeleteMapping("/calc/{var}")
    public ResponseEntity<String> doDelete(HttpSession s, @PathVariable String var)
    {
        s.removeAttribute(var);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}
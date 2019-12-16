package com.efimchik.ifmo.web.mvc;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class EquationManager
{
    @PutMapping("/calc/equation")
    public ResponseEntity<String> doPut(HttpSession s, @RequestBody String eq) throws IOException
    {
        if (eq.indexOf('*') == -1 && eq.indexOf('/') == -1 && eq.indexOf('+') == -1 && eq.indexOf('-') == -1)
            return new ResponseEntity<>(HttpStatus.valueOf(400));

        if (s.getAttribute("equation") == null)
        {
            s.setAttribute("equation", eq);
            return new ResponseEntity<>(HttpStatus.valueOf(201));
        } else
        {
            s.setAttribute("equation", eq);
            return new ResponseEntity<>(HttpStatus.valueOf(200));
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity<String> doDelete(HttpSession s)
    {
        s.removeAttribute("equation");
        return new ResponseEntity<>(HttpStatus.valueOf(204));

    }
}

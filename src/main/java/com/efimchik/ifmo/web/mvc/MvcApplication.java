package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.checkerAndCalculator.Calculator;
import com.efimchik.ifmo.web.mvc.checkerAndCalculator.Checker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;


@Controller
@SpringBootApplication
public class MvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcApplication.class, args);
    }

    @PutMapping(value = "/calc/{variableName}")
    public ResponseEntity<String> putVariable(@PathVariable String variableName, @RequestBody String value, HttpSession session){
        String message = new Checker().checkIsItOK(variableName, value);
        if ("OK".equals(message)){
            if (session.getAttribute(variableName) != null){
                session.setAttribute(variableName, value);
                return new ResponseEntity<>("you've change something", HttpStatus.valueOf(200));
            }else{
                session.setAttribute(variableName, value);
                return new ResponseEntity<>("you've put something new", HttpStatus.valueOf(201));
            }
        }else{
            if ("Bad variable value".equals(message)){
                return new ResponseEntity<>(message, HttpStatus.valueOf(403));
            }else{
                return new ResponseEntity<>(message, HttpStatus.valueOf(400));
            }
        }
    }

    @GetMapping(value = "/calc/result")
    public ResponseEntity<String> getResult(HttpSession session){
        HashMap<String, String> p = new HashMap<>();
        Enumeration<String> namesAr = session.getAttributeNames();
        while (namesAr.hasMoreElements()){
            String nameAr = namesAr.nextElement();
            p.put(nameAr, (String)session.getAttribute(nameAr));
        }
        String ans = new Calculator().calculate(p);
        if ("problems in calculate".equals(ans)){
            return new ResponseEntity<>(ans,
                    HttpStatus.valueOf(409));
        }else{
            return new ResponseEntity<>(ans, HttpStatus.valueOf(200));
        }
    }

    @DeleteMapping(value = "/calc/{variableName}")
    public ResponseEntity<String> deleteSomething(@PathVariable String variableName, HttpSession session){
        if (session != null){
            if (session.getAttribute(variableName) != null){
                session.removeAttribute(variableName);
                return new ResponseEntity<>("you've deleted something", HttpStatus.valueOf(204));
            }else{
                return new ResponseEntity<>("You're doing something bad", HttpStatus.valueOf(400));
            }
        }else{
            return new ResponseEntity<>("You're doing something bad", HttpStatus.valueOf(400));
        }
    }
}



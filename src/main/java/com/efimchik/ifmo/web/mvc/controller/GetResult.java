package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.services.Parser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;

@Controller
public class GetResult {
    @RequestMapping(value = "/calc/result", method = RequestMethod.GET)
    public ResponseEntity<String> getResult(HttpSession session){
        try {

            final String name = (String) session.getAttribute("equation");
            HashMap<String, String> variables = new HashMap();
            Enumeration<String> list = session.getAttributeNames();


            while (list.hasMoreElements()) {
                String buffer = list.nextElement();
                if (buffer.compareTo("equation") != 0) {
                    variables.put(buffer, (String) session.getAttribute(buffer));
                }
            }

            Parser parser = new Parser(name, variables);
            parser.parse();
            return new ResponseEntity(parser.evaluate(), HttpStatus.valueOf(200));
        }catch(NumberFormatException ex){
            return new ResponseEntity(HttpStatus.valueOf(409));
        }
    }
}


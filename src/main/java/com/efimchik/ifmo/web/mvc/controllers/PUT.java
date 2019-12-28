package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Controller
public class PUT {
    @PutMapping("/calc/{value}")
    public ResponseEntity putValue(@PathVariable String value, @RequestBody String value_, HttpSession session){
        if(session.getAttribute("parameters")==null) session.setAttribute("parameters", new HashMap<String, String>());
        Map<String, String> valueMap = (HashMap<String, String>)session.getAttribute("parameters");
        int status;
        if(bigValue(value_)) return new ResponseEntity(HttpStatus.valueOf(403));
        else if(valueMap.containsKey(value)){
            status = 200;
        }
        else status = 201;
        valueMap.put(value,value_);
        session.setAttribute("parameters", valueMap);
        return new ResponseEntity(HttpStatus.valueOf(status));
    }

    private boolean bigValue(String value){
        if(value.charAt(0)>='a' && value.charAt(0)<='z') return false;
        return Integer.parseInt(value)>10000|| Integer.parseInt(value)<-10000;
    }

    @DeleteMapping("/calc/{value}")
    public ResponseEntity delete(@PathVariable String value, HttpSession session){
        Map<String, String> valueMap = (HashMap<String, String>) session.getAttribute("parameters");
        valueMap.remove(value);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }
}

package com.efimchik.ifmo.web.mvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@SessionAttributes("equation")
public class PutAndDelete {

    @PutMapping("/calc/equation")
    public ResponseEntity PutRequest(@RequestBody String equation, ModelMap model)  {
        if (checkbadformat(equation)) {
            return new ResponseEntity(HttpStatus.valueOf(400));
        }
        else {
            Object attr = model.getAttribute("equation");
            model.addAttribute("equation", equation);
            if (attr == null) return new ResponseEntity(HttpStatus.valueOf(201));
            else return new ResponseEntity(HttpStatus.valueOf(200));
        }
    }

    @DeleteMapping("/calc/equation")
    public ResponseEntity DeleteRequest(ModelMap model) {
        model.addAttribute("equation", null);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean checkbadformat(String str) {
        int cnt = 0;
        for (int i=0; i<str.length(); i++) {
            if(isOperator(str.charAt(i))) cnt++;
            if(str.charAt(i)>='A' && str.charAt(i)<='Z') return true;
        }
        return cnt == 0;
    }

    private boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/');
    }
}

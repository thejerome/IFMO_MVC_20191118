package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class VarController{
    @PutMapping("/calc/{var}")
    public ResponseEntity<String> putVariable(HttpSession session, @PathVariable String var, @RequestBody String value){

        String text = "Illegal variable";
        int code = 403;
        StringBuilder vList = new StringBuilder();
        String old;

        if (corVar(value)){

            if (session.getAttribute(var) == null) {
                text = "Variable created";
                code = 201;
            }
            else {
                text = "Variable replaced";
                code = 200;
            }
            session.setAttribute(var, value);
            if (session.getAttribute("varList") != null) {
                old = session.getAttribute("varList").toString();
                vList.append(old);
            }
            vList.append(var);
            session.setAttribute("varList", vList.toString());
        }
        return new ResponseEntity<>(text, HttpStatus.valueOf(code));
    }

    @DeleteMapping("/calc/{var}")
    public ResponseEntity deleteVariable(HttpSession session, @PathVariable String var) {
        session.removeAttribute(String.valueOf(var));
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean corVar (String var){
        if (var.charAt(0)>='a' && var.charAt(0)<='z') {
            return true;
        }
        return Integer.parseInt(var) >= -10000 && Integer.parseInt(var) <= 10000;
    }
}
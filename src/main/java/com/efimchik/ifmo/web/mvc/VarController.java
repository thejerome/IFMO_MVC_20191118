package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

//I just took VarServ from serv task-2 and changed everything for springy things lol
@Controller
public class VarController{
    @PutMapping("/calc/{var}")
    public ResponseEntity<String> putVariable(HttpSession session, @PathVariable String var, @RequestBody String value){

        String text = "Illegal variable"; //prepare for incorrect var
        int code = 403;
        
        if (correctVar(value)){
            
            if (session.getAttribute(var) == null) {
                text = "Variable created";
                code = 201;
                //bad value
            }
            else {
                text = "Variable replaced";
                code = 200;
                //good value
            }
            
            session.setAttribute(var, value);
            //Working with string of var names, like "abcz" to make future easier
            StringBuilder varList = new StringBuilder();

            if (session.getAttribute("varList") != null) {
                String oldList = session.getAttribute("varList").toString();
                varList.append(oldList);
            }
            varList.append(var);
            session.setAttribute("varList", varList.toString());
        }
        return new ResponseEntity<>(text, HttpStatus.valueOf(code));
    }
    
    @DeleteMapping("/calc/{var}")
    public ResponseEntity deleteVariable(HttpSession session, @PathVariable String var) {
        session.removeAttribute(String.valueOf(var));
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean correctVar (String var){
        if (var.charAt(0)>='a' && var.charAt(0)<='z') {
            return true;
        }
        return Integer.parseInt(var) >= -10000 && Integer.parseInt(var) <= 10000;
    }
}
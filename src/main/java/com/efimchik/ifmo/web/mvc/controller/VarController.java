package com.efimchik.ifmo.web.mvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@SessionAttributes("var")
public class VarController {

    @PutMapping("/calc/{var}")
    public ResponseEntity PutVar(@PathVariable String var, @RequestBody String value, Model model) {
        if (badformat(value)) {
            return new ResponseEntity(HttpStatus.valueOf(403));
        }
        else {
            String attrstring = (model.getAttribute("var") == null) ? "":model.getAttribute("var").toString();
            String para = attrstring;
            if (attrstring.contains(var)) {
                int varpos = attrstring.indexOf(var);
                attrstring = attrstring.replace(" ", "");
                String varandvalue = attrstring.substring(varpos-1);
                attrstring = attrstring.replace(varandvalue, "");
            }
            model.addAttribute("var", attrstring + " " + var + ":" + value);
            if (para.equals("") || !para.contains(var))
                return new ResponseEntity(HttpStatus.valueOf(201));
            else return new ResponseEntity(HttpStatus.valueOf(200));
        }
    }

    @DeleteMapping("/calc/{var}")
    public ResponseEntity DeleteVar(@PathVariable String var, Model model) {
        String para = Objects.requireNonNull(model.getAttribute("var")).toString();
        int varpos = para.indexOf(var);
        int spacepos = (para.indexOf(" ", varpos) == -1) ? (para.length()-1):para.indexOf(" ", varpos);
        String varandvalue = para.substring(varpos-1, spacepos+1);
        para = para.replace(varandvalue, "");
        model.addAttribute("var", para);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    private boolean badformat(String str) {
        if (str.charAt(0)>='a' && str.charAt(0)<='z')
            return false;
        return (Integer.parseInt(str) < -10000 || Integer.parseInt(str) > 10000);
    }
}
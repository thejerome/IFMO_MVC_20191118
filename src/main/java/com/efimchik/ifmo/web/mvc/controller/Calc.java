package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.Calc.CalcUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

@Controller
@SessionAttributes({"equation", "var"})
public class Calc {

    @GetMapping("/calc/result")
    public ResponseEntity<String> GetRequest(HttpSession session) {
        if (session.getAttribute("equation") == null) {
            return new ResponseEntity<>("409",HttpStatus.valueOf(409));
        }
        else {
            HashMap<String, String> ss = new HashMap<>();
            ss.put("equation",session.getAttribute("equation").toString());
            String paras = Objects.requireNonNull(session.getAttribute("var")).toString();

            if (paras.charAt(0) == ' ')
                paras = paras.substring(1);
            System.out.println(paras);
            String[] parameters = paras.split(" ");
            for (String parameter : parameters) {
                int twodotpos = parameter.indexOf(":");
                ss.put(parameter.substring(0,twodotpos), parameter.substring(twodotpos+1));
            }
            String equation = CalcUtil.MakeEquation(ss);
            if (Pattern.matches(".*[a-zA-Z].*", equation))
                return new ResponseEntity<>("409",HttpStatus.valueOf(409));
            else return new ResponseEntity<>(CalcUtil.calc(" " + equation), HttpStatus.valueOf(200));
        }
    }


}
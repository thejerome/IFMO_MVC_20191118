package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Controller
@ResponseBody
public class ResultController {
    @RequestMapping(value = "/calc/result",
            method = RequestMethod.GET)
    public ResponseEntity setVariable(HttpSession session) {

        Map<String, String> map = new HashMap<>();
        Enumeration<String> e = session.getAttributeNames();

        if(e.hasMoreElements()) {
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                map.put(name, (String) session.getAttribute(name));
            }
        } else {
            return ResponseEntity.status(HttpStatus.valueOf(409)).body("no values");
        }

        String equation = map.get("equation");

        String postfixEquation = EvalHelper.delimitString(EvalHelper.infixToPostfix(equation));
        String numEquation = EvalHelper.getNumericEquation(postfixEquation, map);
        if (numEquation == null) {
            return ResponseEntity.status(HttpStatus.valueOf(409)).body("lack of data");
        }
        int result = EvalHelper.evaluate(numEquation);

        return ResponseEntity.status(HttpStatus.valueOf(200)).body(result);
    }
}

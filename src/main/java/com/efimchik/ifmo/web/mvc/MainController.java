package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Controller
@ResponseBody
public class MainController {
    @RequestMapping(value = "/calc/result", method = RequestMethod.GET)
    public ResponseEntity getResult(HttpSession session) {

        Map<String, String> map = new HashMap<>();
        Enumeration<String> enumeration = session.getAttributeNames();

        if(!enumeration.hasMoreElements()) {
            return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
        }
        while (enumeration.hasMoreElements()) {
            String element = enumeration.nextElement();
            map.put(element, (String) session.getAttribute(element));
        }

        String equation = map.get("equation");
        if (equation!=null) {
            try {
                String postfix = Calculator.makePostfix(equation);
                System.out.println(postfix);
                return ResponseEntity.status(HttpStatus.valueOf(200)).body(Calculator.eval(postfix, map));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
            }
        } return ResponseEntity.status(HttpStatus.valueOf(409)).body(null);
    }
}

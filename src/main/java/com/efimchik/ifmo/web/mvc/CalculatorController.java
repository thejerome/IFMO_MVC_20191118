package com.efimchik.ifmo.web.mvc;

import org.dom4j.IllegalAddException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CalculatorController {

    @GetMapping("/calc/result")
    public ResponseEntity<String> doGet(HttpSession thisSession) {

        String equation = ParserUtils.mapping(thisSession);
        if (equation == null) {
            return new ResponseEntity<>("Выражения нет", HttpStatus.valueOf(409));
        } else {
            Map<String, String> prmtrs = new HashMap<>();
            Enumeration<String> list = thisSession.getAttributeNames();

            while (list.hasMoreElements()) {
                String buff = list.nextElement();
                if (buff.compareTo(equation) != 0) //=0 в случае равенства baff and equation
                    prmtrs.put(buff, (String) thisSession.getAttribute(buff));
            }
            try {
                return new ResponseEntity<>(String.valueOf(ParserUtils.answerMthd(ParserUtils.parse(equation))), HttpStatus.valueOf(200));
            }catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.valueOf(409));
            }
        }
    }
}

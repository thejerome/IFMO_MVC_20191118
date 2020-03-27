package com.efimchik.ifmo.web.mvc.task1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Result {

    @GetMapping(value = "/calc/result")
    public ResponseEntity<String> get(HttpSession session) {
        if (session.getAttribute("equation") == null && session.getAttribute("var") == null && letterz((String) session.getAttribute("equation"))) {
            return new ResponseEntity<>("error", HttpStatus.valueOf(409));
        } else {
            Map<String, Object> varsMap = map(session);
            try {
                int result = Calculator.getResult((String) session.getAttribute("equation"), varsMap);
                return new ResponseEntity<>(String.valueOf(result), HttpStatus.valueOf(200));
            } catch (InvalidParameterException e) {
                return new ResponseEntity<>("error", HttpStatus.valueOf(409));
            }
            catch (IllegalArgumentException e) {
                return new ResponseEntity<>("var", HttpStatus.valueOf(409));
            }
        }
    }

    private boolean letterz(String str) {
        for (char c : str.toCharArray()) {
            if (c >= 'A' && c <= 'Z')
                return true;
        }
        return false;
    }

    private Map<String, Object> map(HttpSession ses) {
        char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        Map<String, Object> objectMap = new HashMap<>();
        for (char chr: chars) {
            String str = (String) ses.getAttribute(String.valueOf(chr));
            if (str != null){
                if (Character.isDigit(str.toCharArray()[0]))
                    objectMap.put(String.valueOf(chr), Integer.parseInt(str));
                else {
                    objectMap.put(String.valueOf(chr), str);
                }
            }
        }
        System.out.println(objectMap);
        return objectMap;
    }
}

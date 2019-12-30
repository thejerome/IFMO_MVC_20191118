package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class VarsController {
    @PutMapping("/calc/{varName}")
    public ResponseEntity<String> doPut(HttpSession sesh, @PathVariable String varName, @RequestBody String value) {
        boolean isDigit = false;
        try {
            int varVal = Integer.parseInt(value);
            if (varVal > 10000 || varVal < -10000) { // DAMN EXCEPTIONS
                return new ResponseEntity<>("Out of range", HttpStatus.FORBIDDEN);
            }
            isDigit = true;
        }
        catch (NumberFormatException exception) {}
        if (sesh.getAttribute("vars") == null) {
            sesh.setAttribute("vars", new HashMap<String, Object>());
        }
        Map<String, Object> varsMap = (Map<String, Object>) sesh.getAttribute("vars");

        HttpStatus httpstatus;
        if (varsMap.containsKey(varName)) {
            httpstatus = HttpStatus.OK;
        }
        else {

            httpstatus = HttpStatus.CREATED;
        }
        if (isDigit) {
            varsMap.put(
                    varName,
                    Integer.parseInt(value)
            );
        }
        else {
            varsMap.put(
                    varName,
                    value
            );
        }
        sesh.setAttribute("vars", varsMap);
        return new ResponseEntity<>(httpstatus);
    }

    @DeleteMapping("/calc/{varName}")
    public ResponseEntity doDelete(HttpSession sesh, @PathVariable String varName) {
        Map<String, Object> varsMap = (Map<String, Object>) sesh.getAttribute("vars");
        varsMap.remove(varName);
        sesh.setAttribute("vars", varsMap);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Vars {
    @PutMapping("/calc/{var}")
    public ResponseEntity<String> put(HttpSession ses, @PathVariable String var, @RequestBody String val) {
        ResponseEntity<String> res;
        if (ses.getAttribute("var") == null) {
            ses.setAttribute("var", new HashMap<String, Object>());
        }
        boolean num = false;
        try {
            int value = Integer.parseInt(val);
            if (value < -10000 || value > 10000)
                return new ResponseEntity<>("forbidden", HttpStatus.FORBIDDEN);
            num = true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Map<String, Object> vars = (Map<String, Object>) ses.getAttribute("var");
        if (vars.containsKey(var))
            res = new ResponseEntity<>(HttpStatus.OK);
        else
            res = new ResponseEntity<>(HttpStatus.CREATED);
	if (num) {
		vars.put(
                var,
                Integer.parseInt(val)
        );
	}
        else {
	vars.put(
                var,
                val
        );
	}
        ses.setAttribute("var", vars);
        return res;
    }

    @DeleteMapping("/calc/{var}")
    public ResponseEntity<HttpStatus> del(HttpSession ses, @PathVariable String var) {
        Map<String, Object> var1 = (Map<String, Object>) ses.getAttribute("var");
        var1.remove(var);
        ses.setAttribute("var", var1);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.efimchik.ifmo.web.mvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


@Controller
public class ResController {

    private String prepareYourself(Map<String, String> sesMap, Map<String, String> eqMap, String eq, HttpSession s) {
        Enumeration<String> responseStuff = s.getAttributeNames();
        while (responseStuff.hasMoreElements()) {
            String var = responseStuff.nextElement();
            sesMap.put(var, s.getAttribute(var).toString());
        }
        sesMap.remove("equation");
        boolean retardedCheck=checkIfGood(eq);
        for (int i = 0; i < eq.length(); i++) {
            //if (checkIfVarGood(eq.charAt(i)))
                eqMap.put(Character.toString(eq.charAt(i)), "");
        }
        for (Map.Entry<String, String> var : eqMap.entrySet()) {
            String key = var.getKey();
            String val = sesMap.get(key);
            if (val == null)
                throw new IllegalArgumentException("No such value");
            while (!Pattern.matches("^[-0-9]+$", val)) {
                key = val;
                val = sesMap.get(key);
                if (val == null)
                    throw new IllegalArgumentException("No such value");
            }
            var.setValue(val);
        }
        for (int i = 0; i < eq.length(); i++) {
            if (eq.charAt(i) >= 'a' && eq.charAt(i) <= 'z')
                eq= eq.replace(Character.toString(eq.charAt(i)),
                        eqMap.get(Character.toString(eq.charAt(i))));
        }
        return eq;
    }
    private static boolean checkIfGood (String s)
    {
        if ((s.indexOf('+') == 1 || s.indexOf('-') == 1 || s.indexOf('*') == 1 || s.indexOf('/') == 1))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private static boolean checkIfVarGood (String val)
    {
        if (((Integer.valueOf(val)*Integer.valueOf(val)<100000000)||((val.charAt(0) >= 'a' && val.charAt(0) <= 'z'))))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession s) {
        try {
            if ((s.getAttribute("equation"))==null)
                throw new IllegalArgumentException("No such equation");
            String eq = s.getAttribute("equation").toString().replace(" ", "");
            Map<String, String> sesMap = new HashMap();
            Map<String, String> eqMap = new HashMap();
            return new ResponseEntity<>(Integer.toString(CountingThingy.process(prepareYourself(sesMap, eqMap, eq, s))), HttpStatus.valueOf(200));
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(409));
        }
    }




}
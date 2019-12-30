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

    public static boolean ifCorrect(String s)
    {

        if (((Integer.valueOf(s)*Integer.valueOf(s)<100000000)||(s.charAt(0) >= 'a' && s.charAt(0) <= 'z')))
        {
            return true;
        }
        else
        {
            return  false;
        }
    }
    public static String setVars (String s, Map<String, String> eqMap)
    {
        for (int i = 0; i < s.length(); i++) {
            if (ifCorrectCh(s.charAt(i)))
                s = s.replace(Character.toString(s.charAt(i)),
                        eqMap.get(Character.toString(s.charAt(i))));
        }
        return s;
    }
    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession s) {
        try {
            if (s.getAttribute("equation") == null)
                throw new IllegalArgumentException("No equation found");
            String eq = s.getAttribute("equation").toString().replace(" ", "");
            Map<String, String> sesMap = new HashMap();
            Map<String, String> eqMap = new HashMap();
            prepareYourself(s, eq, sesMap, eqMap);
            return new ResponseEntity<>(Integer.toString(CountingThingy.process(setVars(eq,eqMap))), HttpStatus.valueOf(200));
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(409));
        }
    }
    public static boolean ifEqCorrect (String s)
    {
        if (s.indexOf('*') != -1 || s.indexOf('/') != -1 || s.indexOf('+') != -1 || s.indexOf('-') !=-1)
        {
            return  true;
        }
        else
        {
            return false;
        }
    }
    private void prepareYourself(HttpSession s, String eq, Map<String, String> sesMap, Map<String, String> eqMap) {

        Enumeration<String> attributes = s.getAttributeNames();

        while (attributes.hasMoreElements()) {
            String var = attributes.nextElement();
            String var1=s.getAttribute(var).toString();
            sesMap.put(var, var1);
        }
        sesMap.remove("equation");
        for (int i = 0; i < eq.length(); i++) {
            if (ifCorrectCh(eq.charAt(i)))
                eqMap.put(Character.toString(eq.charAt(i)), "");
        }
            for (Map.Entry<String, String> var : eqMap.entrySet()) {
                String key = var.getKey();
                String val = sesMap.get(key);
                if (val == null) {

                    throw new IllegalArgumentException("No such value");
                }
                while (!Pattern.matches("^[-0-9]+$", val)) {
                    key = val;
                    val = sesMap.get(key);
                    if (val == null)
                        throw new IllegalArgumentException("No such value");
                }
                var.setValue(val);
            }
    }
    public static boolean ifCorrectCh(char s)
    {
        if (s >= 'a' && s <= 'z')
        {
            return true;
        }
        else
        {
            return  false;
        }
    }


}
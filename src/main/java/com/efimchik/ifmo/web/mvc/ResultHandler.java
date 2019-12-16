package com.efimchik.ifmo.web.mvc;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ResultHandler
{
    @GetMapping("/calc/result")
    public ResponseEntity<String> doGet(HttpSession s) throws IllegalArgumentException
    {
        try
        {
            String eq = s.getAttribute("equation").toString();

            if (eq == null)
                throw new IllegalArgumentException();

            eq = eq.replace(" ", "");


            HashMap<String, String> sessionVars = new HashMap();
            HashMap<String, String> eqVars = new HashMap();

            KillVariables(s, eq, sessionVars, eqVars);


            for (int i = 0; i < eq.length(); i++)
                if (eq.charAt(i) >= 'a' && eq.charAt(i) <= 'z')
                    eq = eq.replace(Character.toString(eq.charAt(i)), eqVars.get(Character.toString(eq.charAt(i))));


            return new ResponseEntity<>(Integer.toString(C.calc(eq)), HttpStatus.valueOf(200));

        } catch (IllegalArgumentException e)
        {
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        }
    }

    private void KillVariables(HttpSession s, String eq, Map<String, String> sVars, Map<String, String> eqVars) throws IllegalArgumentException
    {
        Enumeration<String> attributes = s.getAttributeNames();

        while (attributes.hasMoreElements())
        {
            String var = attributes.nextElement();
            sVars.put(var, s.getAttribute(var).toString());
        }
        sVars.remove("equation");

        for (int i = 0; i < eq.length(); i++)
            if (eq.charAt(i) >= 'a' && eq.charAt(i) <= 'z')
                eqVars.put(Character.toString(eq.charAt(i)), "");

        for (Map.Entry<String, String> var : eqVars.entrySet())
        {
            String key = var.getKey();
            String val = sVars.get(key);

            if (val == null)
                throw new IllegalArgumentException();

            while (!Pattern.matches("^[-0-9]+$", val))
            {
                key = val;
                val = sVars.get(key);

                if (val == null)
                    throw new IllegalArgumentException();
            }

            var.setValue(val);
        }
    }
}

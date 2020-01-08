package com.efimchik.ifmo.web.mvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
public class VarController {

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
    @PutMapping("/calc/{n}")
    public ResponseEntity<String> putVariable(HttpSession s, @PathVariable String n, @RequestBody String val) {
        System.out.println("в меня пихнули " + n + " со значением " + val);
        System.out.println("Значение аргумента " + val.toCharArray()[0] + " проканало?  " + ifCorrectCh(val.toCharArray()[0]));
        if (ifCorrectCh(val.toCharArray()[0]))
        {
            Object ss=s.getAttribute(n.toString());
            val=s.getAttribute(val).toString();
        }
        if (!ifCorrect(val)) {
            //System.out.println("Случилась дичь на " + n + " со значением " + val);
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }

        if (s.getAttribute(n) == null) {
            System.out.println("Вещь создана " + n + " со значением " + val);
            s.setAttribute(n, val);
            return new ResponseEntity<>(HttpStatus.valueOf(201));
        }
        else
            {
                System.out.println("Вещь обновлена " + n + " со значением " + val);
                s.setAttribute(n, val);
                return new ResponseEntity<>(HttpStatus.valueOf(200));

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
    @DeleteMapping("/calc/{n}")
    public ResponseEntity deleteVariable(HttpSession s, @PathVariable String n) {
        s.removeAttribute(n);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }
}
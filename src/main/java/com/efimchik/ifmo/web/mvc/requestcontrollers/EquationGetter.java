package com.efimchik.ifmo.web.mvc.requestcontrollers;

import com.efimchik.ifmo.web.mvc.calculating.Calculator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;

public class EquationGetter extends Getter {

    EquationGetter(HttpSession session) {
        super(session);
    }

    @Override
    public ResponseEntity getData(String key)
    {
        try {
            if (m_session.getAttribute(key) == null){
                return new ResponseEntity(HttpStatus.valueOf(409));
            }

            Enumeration<String> NameVars = m_session.getAttributeNames();

            String equation = "";

            HashMap<String, String> temp = new HashMap<>();

            while (NameVars.hasMoreElements()) {
                String varKey = NameVars.nextElement();
                String val = (String)m_session.getAttribute(varKey);
                if (key.equals(varKey)){
                    equation = val;
                }else{
                    temp.put(varKey,val);
                }
            }

            equation = Calculator.generateEquation(temp, equation);

            if (equation.contains("error")){
                return new ResponseEntity(-1,HttpStatus.valueOf(409));
            }

            return new ResponseEntity(Calculator.evaluate(equation), HttpStatus.valueOf(200));
        }catch (NumberFormatException ex){
            return new ResponseEntity(HttpStatus.valueOf(419));
        }
    }
}

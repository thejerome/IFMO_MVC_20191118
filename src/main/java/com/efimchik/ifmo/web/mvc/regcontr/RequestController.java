package com.efimchik.ifmo.web.mvc.regcontr;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class RequestController {
    @RequestMapping(value = "/calc/result", method = RequestMethod.GET)
    public ResponseEntity getData(HttpSession session){
        return new EquationGetter(session).getData("equation");
    }

    @RequestMapping(value = "/calc/{variableName}", method = RequestMethod.PUT)
    private ResponseEntity putData(HttpSession session, @PathVariable String variableName, @RequestBody String bodyData){
        return GenerateSetter(session, variableName).setData(variableName, bodyData);
    }

    @RequestMapping(value = "/calc/{variableName}", method = RequestMethod.DELETE)
    private ResponseEntity deleteData(HttpSession session, @PathVariable String variableName){
        return GenerateSetter(session, variableName).deleteData(variableName);
    }

    private Setter GenerateSetter(HttpSession session, String key){
        if (key.equals("equation")){
            return new EquationSetter(session);
        }

        return new ValueSetter(session);
    }
}
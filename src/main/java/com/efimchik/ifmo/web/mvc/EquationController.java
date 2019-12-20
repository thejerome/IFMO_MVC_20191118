package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpSession;


@Controller
public class EquationController{

    private static boolean isOperator (char oper) {
        return oper == '*' || oper == '/' || oper == '+' || oper == '-';
    }
    private static boolean isDelimiter(char oper) { return oper == '(' || oper == ')'; }
    private static boolean isVariable (char oper) {
        return oper >= 'a' && oper <= 'z';
    }
    private static boolean isNumber (char oper) {
        return oper >= '0' && oper <= '9';
    }
    private static boolean correctExpression (String exp) {
        for (int i = 0; i < exp.length(); i++) {
            if (!(isNumber(exp.charAt(i)) || isOperator(exp.charAt(i)) || isVariable(exp.charAt(i)) || isDelimiter(exp.charAt(i)) || (exp.charAt(i) == ' ')) || (i != 0 && isVariable(exp.charAt(i)) && isVariable(exp.charAt(i-1)))) {
                return false;
            }
        }
        return true;
    }


    @PutMapping("/calc/equation")
    public ResponseEntity doPut(HttpSession session, @RequestBody String equation){
        if (!correctExpression(equation)) {
            return new ResponseEntity("expression isn't correct", HttpStatus.valueOf(400));
        } else if (session.getAttribute("equation") == null) {
                session.setAttribute("equation", equation);
                return new ResponseEntity(HttpStatus.valueOf(201));
        } else {
                session.setAttribute("equation", equation);
                return new ResponseEntity(HttpStatus.valueOf(200));
        }
    }


    @DeleteMapping("/calc/equation")
    public ResponseEntity doDelete(HttpSession session) {
        session.removeAttribute("equation");
        return new ResponseEntity(HttpStatus.valueOf(204));
    }
}

package com.efimchik.ifmo.web.mvc.checkerandcalculator;

import java.util.regex.Pattern;

public class Checker {
    public String checkIsItOK(String variableName, String value){
        if ("equation".equals(variableName)){
            return (checkIsEquationOK(value))? "OK" : "Bad equation format";
        }else{
            if (Pattern.matches("-*\\d+", value)){
                return (checkIsValueOK(Integer.parseInt(value)))? "OK" : "Bad variable value";
            }else{
                return (Pattern.matches("[a-zA-Z]+", value))? "OK" : "Bad variable format";
            }
        }
    }
    private boolean checkIsValueOK(int value){
        return value >= -10000 && value <= 10000;
    }
    private boolean checkIsEquationOK(String equation){
        return Pattern.matches("[(\\da-zA-Z]+\\s*([+\\-*/]\\s*[()\\da-zA-Z]+\\s*)+", equation);
    }
}

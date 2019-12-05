package com.efimchik.ifmo.web.mvc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;


@Controller
public class Result {

    @GetMapping("/calc/result")
    public ResponseEntity<String> getResult(HttpSession session){
        if (session == null){
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        } else if (session.getAttribute("equation") == null){
            return new ResponseEntity<>(HttpStatus.valueOf(409));
        } else {
            String equation = session.getAttribute("equation").toString();
            StringBuilder stringBuilder = new StringBuilder();
            for (char c: equation.toCharArray()) {
                if (Character.toString(c).matches("[a-z]")){
                    if (session.getAttribute(Character.toString(c)) != null) {
                        String equationCharacter = session.getAttribute(Character.toString(c)).toString();
                        while (Character.toString(equationCharacter.charAt(0)).matches("[a-z]")) {
                            equationCharacter = session.getAttribute(equationCharacter).toString();
                        }
                        if (Integer.parseInt(equationCharacter) < 0) {
                            stringBuilder.append("(0").append(equationCharacter).append(")");
                        } else {
                            stringBuilder.append(equationCharacter);
                        }
                    } else {
                        return new ResponseEntity<>(HttpStatus.valueOf(409));
                    }
                } else {
                    stringBuilder.append(c);
                }
            }
            return new ResponseEntity<>(calculateRPN(buildRPN(stringBuilder.toString())), HttpStatus.valueOf(200));
        }
    }

    private String calculateRPN(ArrayList<String> equation) {
        while (equation.size() > 1) {
            for (int i = 1; i < equation.size() - 1; i++) {
                if (   !equation.get(i - 1).equals("+")
                        && !equation.get(i - 1).equals("-")
                        && !equation.get(i - 1).equals("*")
                        && !equation.get(i - 1).equals("/")
                        && !equation.get(i    ).equals("+")
                        && !equation.get(i    ).equals("-")
                        && !equation.get(i    ).equals("*")
                        && !equation.get(i    ).equals("/")
                        && (equation.get(i + 1).equals("+")
                        ||  equation.get(i + 1).equals("-")
                        ||  equation.get(i + 1).equals("*")
                        ||  equation.get(i + 1).equals("/"))) {

                    int leftNumber = Integer.parseInt(equation.get(i - 1));
                    int rightNumber = Integer.parseInt(equation.get(i));
                    String operator = equation.get(i + 1);
                    equation.subList(i - 1, i + 2).clear();
                    switch (operator) {
                        case ("+"):
                            equation.add(i - 1, Integer.toString(leftNumber + rightNumber));
                            break;
                        case ("-"):
                            equation.add(i - 1, Integer.toString(leftNumber - rightNumber));
                            break;
                        case ("*"):
                            equation.add(i - 1, Integer.toString(leftNumber * rightNumber));
                            break;
                        case ("/"):
                            equation.add(i - 1, Integer.toString(leftNumber / rightNumber));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return equation.get(0);
    }

    private ArrayList<String> buildRPN(String equation){
        ArrayList<Character> operatorStack = new ArrayList<>();
        ArrayList<String> outputStack = new ArrayList<>();
        StringBuilder tempNumber = new StringBuilder();
        for (int i = 0; i < equation.length(); i++){
            if (Character.toString(equation.charAt(i)).matches("[0-9]")){
                tempNumber.append(equation.charAt(i));
            } else {
                if (tempNumber.length() != 0) {
                    outputStack.add(tempNumber.toString());
                    tempNumber.setLength(0);
                }
                switch (equation.charAt(i)){
                    case ('*'):
                    case ('/'):
                        if (!operatorStack.isEmpty() && Character.toString(operatorStack.get(operatorStack.size() - 1)).matches("[/*]")){
                            outputStack.add(operatorStack.get(operatorStack.size() - 1).toString());
                            operatorStack.remove(operatorStack.size() - 1);
                        }
                        operatorStack.add(equation.charAt(i));
                        break;
                    case ('+'):
                    case ('-'):
                        while (!operatorStack.isEmpty()){
                            if (operatorStack.get(operatorStack.size() - 1) != '(' ) {
                                outputStack.add(operatorStack.get(operatorStack.size() - 1).toString());
                                operatorStack.remove(operatorStack.size() - 1);
                            } else {
                                break;
                            }
                        }
                        operatorStack.add(equation.charAt(i));
                        break;
                    case ('('):
                        operatorStack.add('(');
                        break;
                    case (')'):
                        while (operatorStack.get(operatorStack.size() - 1) != '('){
                            outputStack.add(operatorStack.get(operatorStack.size() - 1).toString());
                            operatorStack.remove(operatorStack.size() - 1);
                        }
                        operatorStack.remove(operatorStack.size() - 1);
                    default:
                        break;
                }
            }
        }
        if (tempNumber.length() != 0) {
            outputStack.add(tempNumber.toString());
            tempNumber.setLength(0);
        }
        while (!operatorStack.isEmpty()){
            outputStack.add(operatorStack.get(operatorStack.size() - 1).toString());
            operatorStack.remove(operatorStack.size() - 1);
        }
        System.out.println(outputStack);
        return outputStack;
    }
}

package com.efimchik.ifmo.web.mvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.StringJoiner;
import java.util.regex.Pattern;

@Controller
@SessionAttributes({"equation", "var"})
public class Calc {

    @GetMapping("/calc/result")
    public ResponseEntity<String> GetRequest(Model model) {
        if (model.getAttribute("equation") == null) {
            return new ResponseEntity<>("409",HttpStatus.valueOf(409));
        }
        else {
            HashMap<String, String> ss = new HashMap<>();
            ss.put("equation",model.getAttribute("equation").toString());
            String paras = model.getAttribute("var") == null ? "":model.getAttribute("var").toString();
            if (paras.charAt(0) == ' ') paras = paras.substring(1);
            System.out.println(paras);
            String[] parameters = paras.split("\\s");
            for (String parameter : parameters) {
                int twodotpos = parameter.indexOf(":");
                ss.put(parameter.substring(0,twodotpos), parameter.substring(twodotpos+1));
            }
            String equation = MakeEquation(ss);
            if (Pattern.matches(".*[a-zA-Z].*", equation)) return new ResponseEntity<>("409",HttpStatus.valueOf(409));
            else return new ResponseEntity<>(calc(" " + equation), HttpStatus.valueOf(200));
        }
    }

    private String MakeEquation(HashMap<String, String> li) {
        HashMap<String, String> vars = new HashMap<>();
        String equation = "";
        for (String keys : li.keySet()) {
            if (keys.equals("equation")) equation = li.get(keys);
            else if (vars.containsKey(li.get(keys))) vars.put(keys, vars.get(li.get(keys)));
            else vars.put(keys, li.get(keys));
        }
        equation = equation.replaceAll(" ", "");
        StringJoiner sj = new StringJoiner("");
        String[] s = equation.split("");
        for (int i=0; i<s.length; i++) {
            if (vars.containsKey(s[i])) s[i] = vars.get(s[i]);
            sj.add(s[i]);
        }
        return sj.toString();
    }

    private String calc(String str) {
        int res = 0;
        int tmpres = 0;
        String operators = "+ ";
        for (int i=1; i<str.length(); i++) {
            String subs;
            int j;
            if (str.charAt(i) == '(') {
                int endIndex = i+1;
                int cnt = 1;
                while (cnt != 0) {
                    if (str.charAt(endIndex) == '(') cnt++;
                    else if (str.charAt(endIndex) == ')') cnt--;
                    endIndex++;
                }
                subs = calc(str.substring(i,endIndex-1));
                j = endIndex;
            }
            else {
                j = i;
                while (j < str.length() && (isOperator(str.charAt(j)) || j==1)) j++;
                subs = str.substring(i, j);
            }
            if (j == str.length()) {
                if (operators.charAt(1) == ' ') tmpres = Integer.parseInt(subs);
                else tmpres = solveNPath(operators.charAt(1), tmpres, Integer.parseInt(subs));
                res = solveNPath(operators.charAt(0), res, tmpres);
                i=j;
                continue;
            }
            if (operators.charAt(1) == '*' || operators.charAt(1) == '/') {
                tmpres = solveNPath(operators.charAt(1), tmpres, Integer.parseInt(subs));
                if (str.charAt(j) == '+' || str.charAt(j) == '-') {
                    res = solveNPath(str.charAt(j), res, tmpres);
                    tmpres = 0;
                    operators = str.charAt(j) + " ";
                }
                else operators = operators.replace(operators.charAt(1),str.charAt(j));
            }
            else {
                if (str.charAt(j) == '*' || str.charAt(j) == '/') {
                    tmpres += Integer.parseInt(subs);
                    operators = operators.replace(operators.charAt(1),str.charAt(j));
                }
                else {
                    if (operators.charAt(0) == '+') res += Integer.parseInt(subs);
                    else res -= Integer.parseInt(subs);
                    operators = operators.replace(operators.charAt(0),str.charAt(j));

                }
            }
            i=j;
        }
        return String.valueOf(res);
    }

    private int solveNPath(char op, int op1, int op2) {
        int result = 0;
        switch (op) {
            case('+'):
                result = op1 + op2;
                break;
            case('-'):
                result = op1 - op2;
                break;
            case('*'):
                result = op1 * op2;
                break;
            case('/'):
                result = op1 / op2;
                break;
            default:
                break;
        }
        return result;
    }

    private boolean isOperator(char c) {
        return !(c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')');
    }
}

package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.*;
import java.util.zip.DataFormatException;


@Controller
@RequestMapping(value = "/calc/*")
public class CalcController {
    private static final int SC_CREATED = 201;
    private static final int SC_OK = 200;
    private static final int SC_BAD_REQUEST = 400;
    private static final int SC_FORBIDDEN = 403;
    private static final int SC_CONFLICT = 409;
    private static final int SC_NO_CONTENT = 204;


    @PutMapping(value = "/{variable_name}")
    public ResponseEntity<String> put(@PathVariable(name = "variable_name") String variable, HttpSession session, @RequestBody String val) {
        HttpStatus status;
        String response = "null";
        try {

            if ("equation".equals(variable)) {
                parseIntoPolishNotation(val);
            } else if (!Character.isLetter(val.toCharArray()[0])) {
                int arg = Integer.parseInt(val);
                if (arg < -10000 || arg > 10000)
                    throw new DataFormatException();
            }
            if (session.getAttribute(variable) == null) {
                status = HttpStatus.valueOf(SC_CREATED);
                response = "Location";
            } else {
                status = HttpStatus.valueOf(SC_OK);
            }
            session.setAttribute(variable, val);
        } catch (ParseException e) {
            status = HttpStatus.valueOf(SC_BAD_REQUEST);
            response = e.getMessage();
        } catch (NumberFormatException ee) {
            status = HttpStatus.valueOf(SC_BAD_REQUEST);
            response = "Wrong number format";
        } catch (DataFormatException eee) {
            status = HttpStatus.valueOf(SC_FORBIDDEN);
            response = "Exceeding values";
        }

        return new ResponseEntity<>(response, status);

    }

    @GetMapping("/calc/result")
    public ResponseEntity<String> get(HttpSession session) {
        HttpStatus status;
        String response;
        try {
            List<String> tokens = parseIntoPolishNotation(session.getAttribute("equation").toString());
            Map<String, Integer> args = new HashMap<>();
            Map<String, String> StringValue = new HashMap<>();
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String key = names.nextElement();
                if (key.length() != 1) continue;
                String value = session.getAttribute(key).toString();
                try {
                    args.put(key, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    StringValue.put(key, value);
                }
            }
            for (String key : StringValue.keySet()) {
                Integer value = args.get(StringValue.get(key));
                args.put(key, value);
            }
            int result = calculate(tokens, args);

            status = HttpStatus.valueOf(SC_OK);
            response = String.valueOf(result);

        } catch (ParseException e) {
            status = HttpStatus.valueOf(SC_CONFLICT);
            response = "Problem with equation";
        } catch (NumberFormatException ee) {
            status = HttpStatus.valueOf(SC_CONFLICT);
            response = "Problem with variable";
        } catch (NullPointerException eee) {
            status = HttpStatus.valueOf(SC_CONFLICT);
            response = "Not set required parameters";
        }
        return new ResponseEntity<>(response, status);

    }

    @DeleteMapping("/{variable_name}")
    public ResponseEntity<String> delete(@PathVariable(name = "variable_name") String variable, HttpSession session) {
        session.setAttribute(variable, null);
        return new ResponseEntity<>("DELETED", HttpStatus.valueOf(SC_NO_CONTENT));
    }


    private static int calculate(List<String> polishNotation, Map<String, Integer> args) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String token : polishNotation) {
            int first;
            int second;
            switch (token) {
                case "+":
                    stack.addFirst(stack.removeFirst() + stack.removeFirst());
                    break;
                case "-":
                    first = stack.removeFirst();
                    second = stack.removeFirst();
                    stack.addFirst(second - first);
                    break;
                case "*":
                    stack.addFirst(stack.removeFirst() * stack.removeFirst());
                    break;
                case "/":
                    first = stack.removeFirst();
                    second = stack.removeFirst();
                    stack.addFirst(second / first);
                    break;
                default:
                    if (args.containsKey(token)) {
                        stack.addFirst(args.get(token));
                    } else {
                        int arg = Integer.parseInt(token);
                        stack.addFirst(arg);
                    }
                    break;
            }
        }
        return stack.removeFirst();
    }

    private List<String> parseIntoPolishNotation(String equation) throws ParseException {
        if (!(equation.contains("+")
                || equation.contains("-")
                || equation.contains("*")
                || equation.contains("/"))) {
            throw new ParseException("notValidEquation", 0);
        }
        List<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(equation.replaceAll(" ", ""), "()+-/*", true);
        while (tokenizer.hasMoreElements()) {
            tokens.add(tokenizer.nextToken());
        }
        Deque<String> stack = new ArrayDeque<>();
        List<String> polishNotation = new ArrayList<>();
        for (String token : tokens) {
            try {
                switch (token) {
                    case "+":
                    case "-":
                    case ")":
                        while ("+-/*".contains(stack.getFirst())) {
                            polishNotation.add(stack.removeFirst());
                        }
                        if (")".equals(token)) {
                            if (!stack.getFirst().equals("("))
                                throw new ParseException("notValidEquation", 0);
                            stack.removeFirst();
                        } else {
                            stack.addFirst(token);
                        }
                        break;
                    case "*":
                    case "/":
                        while ("/*".contains(stack.getFirst())) {
                            polishNotation.add(stack.removeFirst());
                        }
                        stack.addFirst(token);
                        break;
                    case "(":
                        stack.addFirst(token);
                        break;
                    default:
                        polishNotation.add(token);
                }
            } catch (NoSuchElementException e) {
                if (!token.equals(")")) {
                    stack.addFirst(token);
                }
            }
        }
        while (!stack.isEmpty()) {
            if (stack.getFirst().equals("("))
                throw new ParseException("notValidEquation", 0);
            polishNotation.add(stack.removeFirst());
        }
        return polishNotation;
    }
}
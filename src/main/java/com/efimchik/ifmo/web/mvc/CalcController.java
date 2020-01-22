package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/calc")
public class CalcController {

    private static final Pattern VARIABLE_REGEX = Pattern.compile("[a-z]");
    private static final Pattern PARENTHESES_REGEX = Pattern.compile("\\([^()]*\\)");
    private static final Pattern[] SUBEQ_REGEX = new Pattern[] {
            Pattern.compile("-?\\d+[*/]-?\\d+"), Pattern.compile("-?\\d+[+-]-?\\d+")
    };
    private static final Pattern OP_REGEX = Pattern.compile("\\d[+*/-][\\d-]");

    private final Equation equation;

    public CalcController(Equation equation) {
        this.equation = equation;
    }

    @GetMapping("result")
    public ResponseEntity<String> getResult() {
        String eq = equation.getEquation();

        if (eq == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Undefined equation");
        }

        for (Matcher matcher = VARIABLE_REGEX.matcher(eq); matcher.find(); matcher = VARIABLE_REGEX.matcher(eq)) {
            final int start = matcher.start();
            final int end = matcher.end();

            final Object param = equation.getParam(eq.substring(start, end));
            if (param == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Unknown parameter");
            }

            eq = (eq.substring(0, start) + param + (end == eq.length() - 1 ? "" : eq.substring(end)))
                    .replaceAll("\\s", "");
        }

        eq = eq.replaceAll("---", "-");
        eq = eq.replaceAll("--", "+");

        try {
            for (Matcher matcher = PARENTHESES_REGEX.matcher(eq); matcher.find(); matcher = PARENTHESES_REGEX.matcher(eq)) {
                final int start = matcher.start();
                final int end = matcher.end();

                eq = eq.substring(0, start) + calc(eq.substring(start + 1, end - 1)) +
                        (end == eq.length() - 1 ? "" : eq.substring(end));
            }

            return ResponseEntity.ok(calc(eq));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bad equation");
        }
    }

    private String calc(String equation) {
        String eq = equation;

        for (Pattern subeqRegex : SUBEQ_REGEX) {
            for (Matcher matcher = subeqRegex.matcher(eq); matcher.find(); matcher = subeqRegex.matcher(eq)) {
                final int start = matcher.start();
                final int end = matcher.end();

                eq = eq.substring(0, start) + calcSubeq(eq.substring(start, end)) +
                        (end == eq.length() - 1 ? "" : eq.substring(end));
            }
        }

        return eq;
    }

    private Long calcSubeq(String subeq) {
        final Matcher matcher = OP_REGEX.matcher(subeq);

        if (!matcher.find()) {
            return null;
        }

        final int pos = matcher.start() + 1;
        final long first = Long.parseLong(subeq.substring(0, pos));
        final long second = Long.parseLong(subeq.substring(pos + 1));

        switch (subeq.charAt(pos)) {
            case '+': return first + second;
            case '-': return first - second;
            case '*': return first * second;
            case '/': return first / second;
            default: return null;
        }
    }

    @PutMapping("equation")
    public ResponseEntity<?> putEquation(@RequestBody String equation) {
        try {
            if (this.equation.setEquation(equation.replaceAll("\\s", "")) == null) {
                return ResponseEntity.created(URI.create("/calc/equation")).build();
            } else {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("equation")
    public ResponseEntity<?> deleteEquation() {
        this.equation.setEquation(null);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{param}")
    public ResponseEntity<?> putParam(@PathVariable String param, @RequestBody String value) {
        try {
            if (this.equation.setParam(param.trim(), value.trim()) == null) {
                return ResponseEntity.created(URI.create("/calc/" + param)).build();
            } else {
                return ResponseEntity.ok().build();
            }
        } catch (OutOfRangeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{param}")
    public ResponseEntity<?> deleteParam(@PathVariable String param) {
        this.equation.setParam(param.trim(), null);

        return ResponseEntity.noContent().build();
    }
}

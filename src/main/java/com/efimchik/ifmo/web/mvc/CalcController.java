package com.efimchik.ifmo.web.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.net.URI;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RequestScope
@RestController
@RequestMapping("/calc")
public class CalcController {

    private final CalcBundle bundle;

    public CalcController(CalcBundle bundle) {
        this.bundle = bundle;
    }

    @PutMapping("/equation")
    public ResponseEntity<Object> putEquation(@RequestBody String equation) {
        String eq = equation.replaceAll("\\s+", "");

        try {
            if (Pattern.compile("[a-z][a-z]").matcher(eq).find()) {
                throw new IllegalArgumentException();
            }

            highLevelSolve(eq.replaceAll("[a-z]", "1"));
        } catch (ArithmeticException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return doPut("equation", eq);
    }

    @PutMapping("/{var}")
    public ResponseEntity<Object> putVariable(@PathVariable String var, @RequestBody String value) {
        String val = value.trim();

        try {
            int valInt = Integer.parseInt(val);

            if (valInt < -10000 || valInt > 10000) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (NumberFormatException e) {
            if (val.length() != 1) {
                return ResponseEntity.badRequest().build();
            }
        }

        return doPut(var, val);
    }

    private ResponseEntity<Object> doPut(String var, String val) {
        if (bundle.getContents().put(var, val) == null) {
            return ResponseEntity.created(URI.create("/calc/" + var)).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{var}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void doDelete(@PathVariable String var) {
        bundle.getContents().remove(var);
    }

    @GetMapping("/result")
    public ResponseEntity<Integer> getResult() {
        try {
            return ResponseEntity.ok(highLevelSolve(substituteVariables()));
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    private String substituteVariables() {
        StringBuilder next = new StringBuilder(bundle.getContents().get("equation"));
        String eq;

        do {
            eq = next.toString();

            next = new StringBuilder();
            for (int i = 0; i < eq.length(); ++i) {
                if (Character.isAlphabetic(eq.codePointAt(i))) {
                    Object var = bundle.getContents().get(Character.toString(eq.charAt(i)));

                    if (var != null) {
                        next.append(var);
                    } else {
                        throw new IllegalStateException();
                    }
                } else {
                    next.append(eq.charAt(i));
                }
            }
        } while (!next.toString().equals(eq));

        return next.toString();
    }

    private int highLevelSolve(String equation) {
        String eq = equation;
        int left;

        do {
            left = 0;

            boolean opened = false;
            StringBuilder next = new StringBuilder();
            for (int i = 0; i < eq.length(); ++i) {
                if (eq.charAt(i) == '(') {
                    next.append(eq, left, i);
                    opened = true;
                    left = i;
                } else if (eq.charAt(i) == ')' && opened) {
                    next.append(lowLevelSolve(binaryMinusToUnary(eq.substring(left + 1, i))));
                    opened = false;
                    left = i + 1;
                }

                if (eq.charAt(i) != ')' && i == eq.length() - 1) {
                    next.append(eq, left, i + 1);
                }
            }

            eq = reduceUnary(next.toString());
        } while (left != 0);

        return lowLevelSolve(eq);
    }

    private String reduceUnary(String equation) {
        String eq = equation;
        String next = eq;

        do {
            eq = next;

            next = eq.replaceAll("--|\\+\\+", "+")
                    .replaceAll("\\+-", "-");
        } while (!eq.equals(next));

        return next;
    }

    private String binaryMinusToUnary(String equation) {
        return equation.replaceAll("(\\d)-(\\d)", "$1+-$2");
    }

    private int lowLevelSolve(String equation) {
        String eq = equation;

        eq = simpleSolve(eq, eq1 -> Math.min(indexOf(eq1, '*'), indexOf(eq1, '/')),
                (operator, operands) -> {
                    if (operator == '*') {
                        return operands[0] * operands[1];
                    } else {
                        return operands[0] / operands[1];
                    }
                });

        eq = simpleSolve(eq, eq1 -> indexOf(eq1, '+'), (operator, operands) -> operands[0] + operands[1]);
        return Integer.parseInt(eq);
    }

    private String simpleSolve(
            String equation,
            ToIntFunction<String> nextIndex,
            ToIntBiFunction<Character, int[]> operator
    ) {
        String eq = equation;

        for (int i = nextIndex.applyAsInt(eq); i < eq.length(); i = nextIndex.applyAsInt(eq)) {
            int l = i - 1;
            int r = i + 1;

            while (l > -1 && (Character.isDigit(eq.charAt(l)) || eq.charAt(l) == '-')) {
                --l;
            }
            ++l;

            while (r < eq.length() && (Character.isDigit(eq.charAt(r)) || eq.charAt(r) == '-')) {
                ++r;
            }

            int[] operands = Stream.of(Pattern.compile(Character.toString(eq.charAt(i)), Pattern.LITERAL)
                    .split(eq.substring(l, r))).mapToInt(Integer::parseInt).toArray();

            eq = eq.substring(0, l) + operator.applyAsInt(eq.charAt(i), operands) + eq.substring(r);
        }

        return eq;
    }

    private int indexOf(String str, char c) {
        int i = str.indexOf(c);

        if (i == -1) {
            return str.length();
        }

        return i;
    }
}

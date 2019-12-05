package com.efimchik.ifmo.web.mvc.util;

public class Check {

    public static boolean isEquationGood(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            char current = s.charAt(i);
            char next = s.charAt(i + 1);
            if (Character.isLetter(current) && Character.isLetter(next)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNameVarGood(String s) {
        return s.length() == 1 && s.charAt(0) >= 'a' && s.charAt(0) <= 'z';
    }

    public static boolean isVarInRange(String value) {
        try {
            long a = Integer.parseInt(value);
            if (a > 10000 || a < -10000) {
                return false;
            }
        } catch (Exception e) {
            //System.out.println("not a number, obviously");
            return true;
        }
        return true;
    }

}




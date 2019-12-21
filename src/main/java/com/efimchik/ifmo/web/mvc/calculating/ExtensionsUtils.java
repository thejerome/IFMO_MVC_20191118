package com.efimchik.ifmo.web.mvc.calculating;

import java.util.List;

public class ExtensionsUtils {
    static boolean containsOperation(String data){
        return data.contains("+") || data.contains("/") || data.contains("*");
    }

    private static boolean isNumeric(String data){
        return data.matches("-?\\d+(\\.\\d+)?");
    }

    static boolean isSymbol(String data){
        return data.charAt(0)>='a' && data.charAt(0)<='z';
    }

    public static boolean checkVariable(String data, int min, int max){
        if (data.charAt(0)>='a' && data.charAt(0)<='z') {
            return true;
        }

        if (ExtensionsUtils.isNumeric(data)){
            int parsed = Integer.parseInt(data);

            return parsed > min && parsed < max;
        }

        return false;
    }

    static boolean equationContainsSome(String data, List<String> checkData){
        for (int i=0;i<data.length();i++){
            if (checkData.contains(Character.toString(data.charAt(i)))){
                return true;
            }
        }
        return false;
    }

}

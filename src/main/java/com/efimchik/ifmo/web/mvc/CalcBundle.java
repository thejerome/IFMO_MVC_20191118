package com.efimchik.ifmo.web.mvc;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class CalcBundle {

    private final Map<String, String> contents = new HashMap<>();

    public Map<String, String> getContents() {
        return contents;
    }
}

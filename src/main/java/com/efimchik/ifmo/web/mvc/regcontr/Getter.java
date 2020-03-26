package com.efimchik.ifmo.web.mvc.regcontr;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

public class Getter {
    HttpSession m_session;

    Getter(HttpSession session){
        m_session = session;
    }

    public ResponseEntity getData(String key){
        return null;
    }
}

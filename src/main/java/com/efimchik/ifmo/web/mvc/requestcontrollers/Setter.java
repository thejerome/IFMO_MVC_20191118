package com.efimchik.ifmo.web.mvc.requestcontrollers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

public class Setter {
    HttpSession m_session;

    Setter(HttpSession session){
        m_session = session;
    }

    public ResponseEntity setData(String key, String data){
        return null;
    }

    ResponseEntity deleteData(String key){
        m_session.removeAttribute(key);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }
}
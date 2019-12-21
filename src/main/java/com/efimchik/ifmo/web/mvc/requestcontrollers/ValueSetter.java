package com.efimchik.ifmo.web.mvc.requestcontrollers;

import com.efimchik.ifmo.web.mvc.calculating.ExtensionsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

class ValueSetter extends Setter {
    ValueSetter(HttpSession session) {
        super(session);
    }

    @Override
    public ResponseEntity setData(String key, String data){
        if (data == null){
            return new ResponseEntity(HttpStatus.valueOf(204));
        }

        if (!ExtensionsUtils.checkVariable(data, -10000,10000)){
            return new ResponseEntity(HttpStatus.valueOf(403));
        }

        int resultCode = m_session.getAttribute(key) == null ? 201:200;
        m_session.setAttribute(key, data);
        return new ResponseEntity(HttpStatus.valueOf(resultCode));
    }
}
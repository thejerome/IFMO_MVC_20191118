package com.efimchik.ifmo.web.mvc.regcontr;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpSession;

public class EquationSetter extends Setter {
    EquationSetter(HttpSession session) {
        super(session);
    }
    @Override
    public ResponseEntity setData(String key, String data){
        if ((data.indexOf('+') == -1) && data.indexOf('/') == -1 && (data.indexOf('*') == -1)) {
            return new ResponseEntity(HttpStatus.valueOf(400));
        }

        int updateCode = m_session.getAttribute(key) == null ? 201 : 200;
        m_session.setAttribute(key, data);
        return new ResponseEntity(HttpStatus.valueOf(updateCode));
    }
}
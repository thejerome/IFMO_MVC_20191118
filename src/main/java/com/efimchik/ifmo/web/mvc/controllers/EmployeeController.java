package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    private Transformers transformers;

    @GetMapping(value="/employees")
    public ResponseEntity<List<Employee>> getEmployees(HttpSession session,
                                                       @RequestParam(required = false) String sort,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Integer size)
    {
        Pageable request = null;
        if (page != null) {
            if(sort != null){request = PageRequest.of(page, size, Sort.by(sort));}
            else {request = PageRequest.of(page, size);}
            return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAll(request)),HttpStatus.OK);
        }
        else if (sort != null) {
            return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAll(Sort.by(sort))),HttpStatus.OK);
        }
        return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAll()),HttpStatus.OK);
    }

    @GetMapping(value="/employees/{id}")
    public ResponseEntity<Employee> getEmployee(HttpSession session,
                                                @PathVariable Integer id,
                                                @RequestParam(defaultValue = "false") Boolean full_chain)
    {
        return new ResponseEntity<>(transformers.employeeFromRep(transformers.employeeRep.findById(id).get(),full_chain?2:1),HttpStatus.OK);
    }

    @GetMapping(value="/employees/by_manager/{id}")
    public ResponseEntity<List<Employee>> getManager(HttpSession session,
                                                     @PathVariable Integer id,
                                                     @RequestParam(required = false) String sort,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size)
    {
        Pageable request = null;
        if (page != null) {
            if(sort != null){request = PageRequest.of(page, size, Sort.by(sort));}
            else {request = PageRequest.of(page, size);}
            return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAllByManager(id,request)),HttpStatus.OK);
        }
        else if (sort != null) {
            return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAllByManager(id,Sort.by(sort))),HttpStatus.OK);
        }
        return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAllByManager(id)),HttpStatus.OK);
    }

    @GetMapping(value = "/employees/by_department/{_id}")
    public ResponseEntity<List<Employee>> getDepartment(HttpSession session,
                                                        @PathVariable String _id,
                                                        @RequestParam(required = false) String sort,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer size)
    {
        Pageable request = null;
        Integer id = null;
        try {
            id = Integer.parseInt(_id);
        }
        catch (NumberFormatException e)
        {
            id=transformers.departmentRep.findByName(_id).getId();
        }
        if (page != null) {
            if(sort != null){request = PageRequest.of(page, size, Sort.by(sort));}
            else {request = PageRequest.of(page, size);}
            return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAllByDepartment(id,request)),HttpStatus.OK);
        }
        else if (sort != null) {
            return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAllByDepartment(id,Sort.by(sort))),HttpStatus.OK);
        }
        return new ResponseEntity<>(transformers.listFromIterable(transformers.employeeRep.findAllByDepartment(id)),HttpStatus.OK);
    }




}



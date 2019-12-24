package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.repository.EmployeeDao;
import com.efimchik.ifmo.web.mvc.repository.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeRestController {

    @Autowired
    private EmployeeDao employeeDao;

    @GetMapping
    public List<Employee> getAll(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        Paging paging = new Paging(page, size);
        return obtainPage(paging, employeeDao.findAll(sort));
    }

    @GetMapping(value = "/{employee_id}")
    public Employee getById(
            @RequestParam(value = "full_chain", required = false, defaultValue = "false") Boolean full_chain,
            @PathVariable(name = "employee_id") Long id
    ) {
        return employeeDao.findById(id, full_chain);
    }

    @GetMapping(value = "/by_manager/{managerId}")
    public List<Employee> getByManager(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort,
            @PathVariable(name = "managerId") Long id
    ) {
        Paging paging = new Paging(page, size);
        return obtainPage(paging, employeeDao.findByManager(id, sort));
    }

    @GetMapping(value = "/by_department/{department}")
    public List<Employee> getByDepartment(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort,
            @PathVariable(name = "department") String department
    ) {
        Paging paging = new Paging(page, size);
        return obtainPage(paging, employeeDao.findByDepartment(department, sort));
    }

    //============== PAGING
    private List<Employee> obtainPage(Paging paging, List<Employee> book) {
        int fromIndex = Math.max((paging.page - 1) * paging.itemPerPage, 0);
        int toIndex = Math.min(paging.itemPerPage * paging.page, book.size());
        if(fromIndex > toIndex) toIndex = fromIndex = 0;
        return book.subList(fromIndex, toIndex);
    }
}

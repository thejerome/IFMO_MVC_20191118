package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.efimchik.ifmo.web.mvc.service.Mapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceFactoryController {
    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> getAll(@RequestParam(required = false) Integer page,
                                                 @RequestParam (required = false) Integer size,
                                                 @RequestParam (required = false) String sort) {
        String request = "SELECT * FROM EMPLOYEE ";
        if (sort != null) request = request + "ORDER BY " + sort;
        request = request.replace("hired", "hiredate");
        List<Employee> list = getListBySQL(request, 1);
        if (page == null || size == null) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

        return new ResponseEntity<>(list.subList(page * size,
                                      Math.min((page + 1) * size, list.size())), HttpStatus.OK);

    }
    @RequestMapping(value = "/employees/{employee_id}", method = RequestMethod.GET)
    public ResponseEntity<Employee> getEmployee(@PathVariable int employee_id,
                                                @RequestParam (required = false) String full_chain) {

        List<Employee> list = "true".equals(full_chain) ? getListBySQL("SELECT * FROM EMPLOYEE WHERE ID = " + employee_id):
                                                  getListBySQL("SELECT * FROM EMPLOYEE WHERE ID = " + employee_id, 1);
        return new ResponseEntity<>(list.get(0), HttpStatus.OK);
    }

    @RequestMapping(value = "/employees/by_manager/{managerId}", method = RequestMethod.GET)
    public ResponseEntity<List<Employee>> getEmployeeByManagerId(
                                                      @RequestParam (required = false) Integer page,
                                                      @RequestParam (required = false) Integer size,
                                                      @RequestParam (required = false) String sort,
                                                      @PathVariable int managerId) {


        String request = "SELECT * FROM EMPLOYEE " +
                "WHERE MANAGER = " + managerId;
        if (sort != null) request = request + " ORDER BY " + sort;
        request = request.replace("hired", "hiredate");
        List<Employee> list = getListBySQL(request,1);
        if (page == null || size == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return new ResponseEntity<>(list.subList(Math.min(page * size, list.size()),
                Math.min((page + 1) * size, list.size())), HttpStatus.OK);
    }


    @RequestMapping(value = "/employees/by_department/{departmentId_or_departmentName}", method = RequestMethod.GET)
    public ResponseEntity<List<Employee>> getEmployee(@RequestParam (required = false) Integer page,
                                                      @RequestParam (required = false) Integer size,
                                                      @RequestParam (required = false) String sort,
                                                      @PathVariable String departmentId_or_departmentName) {
        Long id = new Long(0);
        try{
            id = Long.valueOf(departmentId_or_departmentName);
        } catch(NumberFormatException e){
            Mapper mapper = new Mapper();
            id =  mapper.getDepartmentByName(departmentId_or_departmentName).getId();
        }

        String request = "SELECT * FROM EMPLOYEE " +
                "WHERE DEPARTMENT = " + id ;
        if (sort != null) request = request + " ORDER BY " + sort;
        request = request.replace("hired", "hiredate");
        List<Employee> list = getListBySQL(request,1);
        if (page == null || size == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return new ResponseEntity<>(list.subList(Math.min(page * size, list.size()),
                Math.min((page + 1) * size, list.size())), HttpStatus.OK);

    }



    public List<Employee> getListBySQL(String request, int layers) {
        Mapper mapper = new Mapper(layers);
        return mapper.employeesListMapper(request, 0);
    }
    public List<Employee> getListBySQL(String request) {
        Mapper mapper = new Mapper();
        return mapper.employeesListMapper(request, 0);
    }

}

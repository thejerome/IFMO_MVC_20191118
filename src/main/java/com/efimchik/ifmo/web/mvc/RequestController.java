package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class RequestController {
    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    List<Employee> getEmployees(HttpSession session,
                                @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer size,
                                @RequestParam(required = false) String sort){
        return new DaoFactory().employeeDAO().getAll(page, size, sort);
    }

    @RequestMapping(value = "/employees/by_manager/{managerId}", method = RequestMethod.GET)
    List<Employee> getEmployeesBMID(HttpSession session,
                                    @PathVariable String managerId,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String sort){
        return new DaoFactory().employeeDAO().getByManagerId(managerId, page, size, sort);
    }

    @RequestMapping(value = "/employees/by_department/{departmentId}", method = RequestMethod.GET)
    List<Employee> getEmployeesBDID(HttpSession session,
                                    @PathVariable String departmentId,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String sort){
        return new DaoFactory().employeeDAO().getByDepartmentId(departmentId, page, size, sort);
    }

    @RequestMapping(value = "/employees/{employee_id}", method = RequestMethod.GET)
    Employee getEmployeeById(HttpSession session, @PathVariable String employee_id,
                             @RequestParam(required = false) boolean full_chain){
        return new DaoFactory().employeeDAO().getById(employee_id, full_chain);
    }
}

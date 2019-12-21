package com.efimchik.ifmo.web.mvc.service;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.mydomain.DepartmentEntity;
import com.efimchik.ifmo.web.mvc.mydomain.EmployeeEntity;
import com.efimchik.ifmo.web.mvc.repository.DepRepository;
import com.efimchik.ifmo.web.mvc.repository.EmpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EmployeeService {

    @Autowired
    DepRepository depRepository;

    @Autowired
    EmpRepository empRepository;

    public Department getDepartment(EmployeeEntity employeeEntity) {
        if (employeeEntity.getDepartment() != null) {
            DepartmentEntity departmentEnt = depRepository.findById(employeeEntity.getDepartment()).get();
            Department department = new Department(departmentEnt.getId(), departmentEnt.getName(), departmentEnt.getLocation());
            return department;
        } else {
            return null;
        }
    }

    public FullName getFullName(EmployeeEntity employeeEntity) {
        return new FullName(employeeEntity.getFirstname(),
                employeeEntity.getLastname(),
                employeeEntity.getMiddlename());
    }

    public EmployeeEntity findById(Long id) {
        return empRepository.findById(id).get();
    }

    public com.efimchik.ifmo.web.mvc.domain.Employee getEmployeeWithoutChain(EmployeeEntity employeeEntity) {
        com.efimchik.ifmo.web.mvc.domain.Employee manager1 = null;
        if (employeeEntity.getManager() != null) {
            EmployeeEntity manager = empRepository.findById(employeeEntity.getManager()).get();

            manager1 = new com.efimchik.ifmo.web.mvc.domain.Employee(manager.getId(), getFullName(manager),
                    manager.getPosition(), manager.getHired(),
                    manager.getSalary(), null, getDepartment(manager));
        } else {
            manager1 = null;
        }
        return new com.efimchik.ifmo.web.mvc.domain.Employee(employeeEntity.getId(), getFullName(employeeEntity),
                employeeEntity.getPosition(), employeeEntity.getHired(),
                employeeEntity.getSalary(), manager1, getDepartment(employeeEntity));
    }

    public com.efimchik.ifmo.web.mvc.domain.Employee getEmployeeWithChain(EmployeeEntity employeeEntity) {
        if (employeeEntity.getManager() != null) {
            return new com.efimchik.ifmo.web.mvc.domain.Employee(employeeEntity.getId(), getFullName(employeeEntity),
                    employeeEntity.getPosition(), employeeEntity.getHired(), employeeEntity.getSalary(),
                    getEmployeeWithChain(findById(employeeEntity.getManager())), getDepartment(employeeEntity));
        } else return new com.efimchik.ifmo.web.mvc.domain.Employee(employeeEntity.getId(), getFullName(employeeEntity),
                employeeEntity.getPosition(), employeeEntity.getHired(),
                employeeEntity.getSalary(), null, getDepartment(employeeEntity));
    }

    public Iterable<EmployeeEntity> getAll(PageRequest pageable) {
        return empRepository.findAll(pageable);
    }

    public Iterable<EmployeeEntity> getAll(Sort sort) {
        return empRepository.findAll(sort);
    }

    public Iterable<EmployeeEntity> getAll() {
        return empRepository.findAll();
    }

    public ArrayList<Employee> toNormalClasses(Iterable<EmployeeEntity> employees) {
        ArrayList<com.efimchik.ifmo.web.mvc.domain.Employee> data = new ArrayList<>();
        for (EmployeeEntity e : employees) {
            data.add(getEmployeeWithoutChain(e));
        }
        return data;
    }

}

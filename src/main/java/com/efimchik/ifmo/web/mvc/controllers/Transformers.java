package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import com.efimchik.ifmo.web.mvc.repos.DepartmentRep;
import com.efimchik.ifmo.web.mvc.repos.EmployeeRep;
import com.efimchik.ifmo.web.mvc.repos.RepDepartment;
import com.efimchik.ifmo.web.mvc.repos.RepEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class Transformers {
    @Autowired
    public EmployeeRep employeeRep;
    @Autowired
    public DepartmentRep departmentRep;

    public List<Employee> listFromIterable(Iterable<RepEmployee> source)
    {
        ArrayList<Employee> result = new ArrayList<>();
        for(RepEmployee s: source){
            result.add(employeeFromRep(s, 1));
        }
        return result;
    }

    public Employee employeeFromRep (RepEmployee s, Integer fullChain)
    {
        Employee tmp = new Employee(
                new Long(s.getId()),
                new FullName(
                        s.getFirstname(),
                        s.getLastname(),
                        s.getMiddlename()
                ),
                Position.valueOf(s.getPosition()),
                s.getHiredate().toLocalDate(),
                new BigDecimal(s.getSalary()),
                ((fullChain>0)&&(s.getManager()!=null))?employeeFromRep(employeeRep.findById(s.getManager()).get(), (fullChain==1)?0:fullChain):null,
                department(s.getDepartment())
        );
        return tmp;
    }
    public Department department(Integer id)
    {
        if (id == null) {return null;}
        RepDepartment d = departmentRep.findById(id).get();
        return new Department(new Long(d.getId()),d.getName(),d.getLocation());
    }
}
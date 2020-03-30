package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Parsers {


    public interface Parser<T>{
        T Parse(ResultSet result) throws SQLException;
    }

    public static class EmployeeParser implements Parser<Employee> {
        boolean FindNext;
        boolean Recursive;

        EmployeeParser(){
            FindNext = true;
            Recursive = false;
        }

        EmployeeParser(boolean findNext, boolean recursive){
            FindNext = findNext;
            Recursive = recursive;
        }

        @Override
        public Employee Parse(ResultSet result) throws SQLException {
            DaoFactory factory = new DaoFactory();

            Employee manager = null;
            Department department = null;

            if (FindNext){
                long next = new LongParser("MANAGER").Parse(result);

                if (next >= 0){
                    List<Employee> resultList = factory.CalculateResultList("SELECT * FROM employee WHERE id = " + next, new EmployeeParser(Recursive, Recursive));
                    if (resultList.size()>0){
                        manager = resultList.get(0);
                    }
                }
            }

            long depId = new LongParser("DEPARTMENT").Parse(result);
            if (depId >= 0){
                department = factory.GetDepartment(Long.toString(depId), false);
            }

            return  new Employee(
                    Long.parseLong(result.getString("id")),
                    new FullName(result.getString("firstname"), result.getString("lastname"), result.getString("middlename")),
                    Position.valueOf(result.getString("position")),
                    LocalDate.parse(result.getString("hiredate")),
                    new BigDecimal(result.getString("salary")),
                    manager,
                    department);
        }
    }

    public static class DepartmentParser implements Parser<Department> {

        @Override
        public Department Parse(ResultSet result) throws SQLException {
            return new Department(
                    Long.parseLong(result.getString("id")),
                    result.getString("name"),
                    result.getString("location")
            );
        }
    }

    public static class LongParser implements Parser<Long> {
        String Code;

        public LongParser(String code){
            Code = code;
        }

        @Override
        public Long Parse(ResultSet result) throws SQLException {
            String data = null;
            try {
                data = result.getString(Code);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (data == null){
                return (long)-1;
            }
            return Long.parseLong(data);
        }
    }
}

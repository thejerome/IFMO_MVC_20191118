package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

import static com.efimchik.ifmo.web.mvc.Service.*;

// Тут мы тоже переписываем старый код - ура, не надо думать!
// Обращаемся к JDBC.task-5 и засовываем нужные сервисы оттуда в класс Service, а потом вызываем их из нужных контроллеров
// Класс ConnectionSource позволяет подключаться к БД по старой схеме, all regards to Миша Просолович

@RestController
public class Controller {
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) {

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";

        String request = "SELECT * FROM employee" +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");

        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employeeId") String employeeId,
                                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String isFullChain) throws SQLException {

        return new ResponseEntity<>(Service.getEmployeeById(Integer.parseInt(employeeId), Boolean.parseBoolean(isFullChain)), HttpStatus.OK);
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable Integer managerId) throws SQLException {

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";

        String request = "SELECT * FROM employee WHERE manager = " + managerId +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");

        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);

    }

    @GetMapping("/employees/by_department/{departmentIdOrName}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentIdOdName(@RequestParam(required = false) Integer page,
                                                                     @RequestParam(required = false) Integer size,
                                                                     @RequestParam(required = false) String sort,
                                                                     @PathVariable String departmentIdOrName) {

        Long departmentId;

        // Меняем имя на ID, если это имя, и потом работаем только с ID
        try { departmentId = Long.parseLong(departmentIdOrName); }
        catch (NumberFormatException e) {
            departmentId = getDepartmentIdByName(departmentIdOrName);
        }

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";

        String request = "SELECT * FROM employee WHERE department = " + departmentId +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");

        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);
    }
}

# IFMO_MVC_20191118
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo)

Create a webapp serving somewhat like REST catalog of employees stored in embedded database.

Implement endpoints serving employees as JSON representations of com.efimchik.ifmo.web.mvc.domain.* classes.

Each employee record should include inforamtion of his department and his manager.
Manager's manager should be usually null. 

Endpoints (all serves GET requests):

* `/employees` - list all employees. Supports paging*.

* `/employees/{employee_id}` - single employee. 
If parameter named `full_chain` exists and is set to true then full manager chain should be written 
(include employee\`s manager, manager of manager, manager of manager of manager and so on up to the organization head)

* `/employees/by_manager/{managerId}` - list employees who subordinates to the manager. No transitivity. Supports paging*.

* `/employees/by_department/{departmentId or departmentName}` - list employees who is working in the department. Supports paging*.

\*Supports paging - means that you may manage what sublist of employees you want to get by three parameters:
* `page` - number of the page (starts with `0`)
* `size` - amount of entry per page
* `sort` - name of the field for sorting (single value from list \[`lastName`, `hired`, `position`, `salary`\], order is ascending)

*Reminder:*
- You may not change domain classes
- You may not change sql scripts
- You may not change gradle-related files
- You may not change tests or test cases
- You should serve employees from database without any in-memory storage
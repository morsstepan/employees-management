package om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.repository;

import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.model.Department;
import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Employee findEmployeeById(Long id);

    List<Employee> findEmployeesByPosition(String position);
}
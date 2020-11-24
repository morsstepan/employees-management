package om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.repository;

import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
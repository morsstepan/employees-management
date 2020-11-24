package om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.service;

import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.model.Department;
import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }


    public List<Department> findAll(){
        return departmentRepository.findAll();
    }
}

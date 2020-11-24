package om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.service;

import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.model.Employee;
import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> findByPosition(String position) {
        return employeeRepository.findEmployeesByPosition(position);
    }

    public Employee findEmployeeById(Long employeeId) {
        return employeeRepository.findEmployeeById(employeeId);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public void deleteEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        employeeRepository.delete(employee);
    }
}

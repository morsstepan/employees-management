package om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.controller;

import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.model.Employee;
import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.service.DepartmentService;
import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class EmployeeController {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping("/home")
    public String greeting(Model model){
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "index";
    }

    @GetMapping("/employees")
    public String findAll(Model model){
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "employee-list";
    }

    @GetMapping(value = { "/create-employee", "/" })
    public String createEmployee(Model model) {
        model.addAttribute("pageTitle", "Employees | Add");
        model.addAttribute("departments", departmentService.findAll());
        return "create-employee";
    }

    @PostMapping(value = "/create-employee")
    public String createEmployee(@Valid @ModelAttribute Employee employee, RedirectAttributes redirectAttr,
                              BindingResult result) {
        String msg = "";
        if (result.hasErrors()) {
            FieldError error = null;
            for (Object obj : result.getAllErrors()) {
                error = (FieldError) obj;
                msg += error.getDefaultMessage();
            }
        }
        if (msg.length() == 0) {
            try {
                if (employeeService.findEmployeeById(employee.getId()) == null) {
                    employeeService.saveEmployee(employee);
                    redirectAttr.addFlashAttribute("status", "success");
                    redirectAttr.addFlashAttribute("message", "Employee information successfully saved");
                } else {
                    redirectAttr.addFlashAttribute("status", "error");
                    redirectAttr.addFlashAttribute("message", "Employee with id=" + employee.getId() + " already exists");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            redirectAttr.addFlashAttribute("status", "error");
            redirectAttr.addFlashAttribute("message", msg);
        }
        return "redirect:/employees";
    }

    @GetMapping(value = "/update-employee/{employeeId}")
    public String updateEmployee(@PathVariable Long employeeId, Model model, RedirectAttributes redirectAttr) {
        try {
            if (employeeId > 0) {
                Employee employee = employeeService.findEmployeeById(employeeId);
                if (employee != null) {
                    model.addAttribute("employee", employee);
                    model.addAttribute("departments", departmentService.findAll());
                } else {
                    redirectAttr.addFlashAttribute("message", "No employee was matched");
                    return "redirect:/employees/view";
                }
            } else {
                redirectAttr.addFlashAttribute("message", "No reference was found");
                return "redirect:/employees/view";
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        model.addAttribute("pageTitle", "Employees | Update");
        return "update-employee";
    }

    @PostMapping(value = "/update-employee")
    public String updateEmployee(@Valid @ModelAttribute Employee employee, RedirectAttributes redirectAttr,
                         BindingResult result) {
        String msg = "";
        redirectAttr.addFlashAttribute("status", "error");
        try {
            if (employee != null && employee.getId() > 0) {
                if (result.hasErrors()) {
                    FieldError error = null;
                    for (Object obj : result.getAllErrors()) {
                        error = (FieldError) obj;
                        msg += error.getDefaultMessage();
                    }
                }
                if (msg.length() == 0) {
                    Employee oldModel = employeeService.findEmployeeById(employee.getId());
//                    if (!oldModel.getRoll().equalsIgnoreCase(student.getRoll())) {
//                        if (studentService.findByRoll(student.getRoll()) != null) {
//                            redirectAttr.addFlashAttribute("message", student.getRoll() + " Roll already exist");
//                            return "redirect:/students/edit/" + student.getStudentId();
//                        }
//                    }
                    employeeService.saveEmployee(employee);
                    redirectAttr.addFlashAttribute("status", "success");
                    redirectAttr.addFlashAttribute("message", "Employee Info Successfully updated");
                    return "redirect:/employees";
                } else {
                    redirectAttr.addFlashAttribute("message", msg);
                    return "redirect:/employees/edit/" + employee.getId();
                }
            } else {
                redirectAttr.addFlashAttribute("message", "No reference was found");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "redirect:/employees";
    }

    @GetMapping(value = "/delete-employee/{employeeId}")
    public String deleteEmployee(@PathVariable Long employeeId, RedirectAttributes redirectAttr) {
        if (employeeId != null) {
            try {
                employeeService.deleteEmployeeById(employeeId);
                redirectAttr.addFlashAttribute("status", "success");
                redirectAttr.addFlashAttribute("message", "Employee Information successfully deleted");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return "redirect:/employees";
    }
}

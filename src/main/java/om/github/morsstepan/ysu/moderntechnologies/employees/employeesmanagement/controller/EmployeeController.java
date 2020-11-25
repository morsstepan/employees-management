package om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.controller;

import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.model.Employee;
import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.service.DepartmentService;
import om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement.service.EmployeeService;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

    @GetMapping("/")
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

    @GetMapping("/sort-employees-by-cur")
    public String sortEmployeesByCurrency(Model model) {
        List<Employee> employees = employeeService.findAll();

        employees.forEach(employee -> {
            doGet(employee);
            employeeService.saveEmployee(employee);
        });
//        employees.sort();
        model.addAttribute("employees", employees);
        return "employee-list";
    }

    private Employee doGet(Employee employee) {
        final CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("" +
                "http://localhost:8100/currency-converter-feign/from/" + employee.getSalaryCurrency() + "/to/RUB/quantity/" +
                employee.getSalaryAmount());

        // add request headers
//        request.addHeader("custom-key", "mkyong");
//        request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                List<String> items = Arrays.asList(result.split("\""));
                employee.setSalaryAmount(
                        new BigDecimal(items.get(16).substring(1, items.get(16).length() - 1))
                );
                employee.setSalaryCurrency(items.get(9));
                System.out.println(result);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employee;
    }

    @GetMapping("/create-employee")
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

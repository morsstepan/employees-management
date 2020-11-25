package om.github.morsstepan.ysu.moderntechnologies.employees.employeesmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EmployeesManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeesManagementApplication.class, args);
	}

}

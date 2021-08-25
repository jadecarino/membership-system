package synoptic.project.rest.employee;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class EmployeeDataAccessObject {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;
    
    public void createEmployee(Employee employee) {
        em.persist(employee);
    }

    public Employee readEmployee(int employeeId) {
        return em.find(Employee.class, employeeId);
    }

    public void updateEmployee(Employee employee) {
        em.merge(employee);
    }

    public void deleteEmployee(Employee employee) {
        em.remove(employee);
    }

    public List<Employee> readAllEmployees() {
        return em.createNamedQuery("Employee.findAll", Employee.class).getResultList();
    }

    public List<Employee> findEmployee(String name, String phoneNumber, String emailAddress, String company, String cardNumber) {
        return em.createNamedQuery("Employee.findEmployee", Employee.class)
            .setParameter("name", name)
            .setParameter("phoneNumber", phoneNumber)
            .setParameter("emailAddress", emailAddress)
            .setParameter("company", company)
            .setParameter("cardNumber", cardNumber).getResultList();
    }
    
}

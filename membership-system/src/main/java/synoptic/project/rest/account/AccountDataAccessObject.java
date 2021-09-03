package synoptic.project.rest.account;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class AccountDataAccessObject {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;
    
    public void createAccount(Account account) {
        em.persist(account);
    }

    public Account readAccount(String cardNumber) {
        return em.find(Account.class, cardNumber);
    }

    public void updateAccount(Account account) {
        em.merge(account);
    }

    public void deleteAccount(Account account) {
        em.remove(account);
    }

    public List<Account> readAllAccounts() {
        return em.createNamedQuery("Account.findAll", Account.class).getResultList();
    }
    
}
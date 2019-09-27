package com.payments.account.repo;

import com.payments.account.model.Account;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class AccountDao {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void create(Account account) {
        account.setId(0);
        em.persist(account);
    }

    public Account find(Integer id) {
        return em.find(Account.class, id);
    }
    public List<Account> findByEmail(String email) {
        return em.createNamedQuery("Account.findByEmail", Account.class)
                .setParameter("email", email)
                .getResultList();
    }

    public List<Account> getAll() {
        return em.createNamedQuery("Account.findAll", Account.class).getResultList();
    }

    public void update(Account account) {
        em.persist(account);
    }
}

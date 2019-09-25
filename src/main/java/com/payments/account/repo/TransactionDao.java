package com.payments.account.repo;

import com.payments.account.model.Transaction;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class TransactionDao {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void create(Transaction transaction) {
        em.persist(transaction);
    }

    public Transaction find(Integer id) {
        return em.find(Transaction.class, id);
    }

    public List<Transaction> getAll() {
        return em.createNamedQuery("Transaction.findAll", Transaction.class).getResultList();
    }
}

package app.entity;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * DOMAIN Logika...CRUD save, update, delete, retrieve
 * <p>
 * @author Grupa1
 */
public abstract class AbstractBankAccount {

    static final EntityManagerFactory ENTITY_MANAGER_FACTORY
            = Persistence.createEntityManagerFactory("com.solutions.bank_ApplicationBankJPA_jar_1.0PU");

    //save -> insert
    public void save() {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(this);
            transaction.commit();
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(exception.getMessage());
        }
    }

    //select
    public BankAccount get() {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            BankAccount bankAccount = entityManager.find(BankAccount.class, this);
            transaction.commit();
            return bankAccount;
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(exception.getMessage());
        }
    }

    //delete
    public void delete() {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(this);
            transaction.commit();
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(exception.getMessage());
        }
    }

    //merge update
    public void update() {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(this);
            transaction.commit();
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * *
     * STATIC METHOD
     */
    public static List<BankAccount> findAll() {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createNamedQuery("BankAccount.findAll");
            transaction.commit();
            List<BankAccount> bankAccounts = query.getResultList();
            return bankAccounts;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    public static boolean transferMoney(BankAccount fromAccount, BankAccount toAccount, BigDecimal transferredAmount) {
        if (fromAccount == null || toAccount == null) {
            return false;
        }
        if (transferredAmount == null) {
            return false;
        }
        BigDecimal fromAccountAmount = fromAccount.getAmount();
        if (fromAccountAmount.compareTo(transferredAmount) < 0) {
            return false;
        }
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            //2 zapisa u tabeli u bazi
            BigDecimal newFromAccountAmount = fromAccount.getAmount().subtract(transferredAmount);
            fromAccount.setAmount(newFromAccountAmount);

            BigDecimal newToAccountAmount = toAccount.getAmount().add(transferredAmount);
            toAccount.setAmount(newToAccountAmount);

            entityManager.merge(fromAccount);
            entityManager.merge(toAccount);
            
            transaction.commit();
            return true;
        } catch (Exception e) {
            if(transaction!=null){
                transaction.rollback();
            }
            throw new RuntimeException(e.getMessage());
        }
    }
}

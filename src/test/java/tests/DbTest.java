package tests;

import model.dao.CustomerAddressDao;
import model.dao.CustomerDao;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static java.lang.System.out;

public class DbTest {
    @Test
    void checkDBConnection(){
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .build();
        //ObjectMapper mapper = new ObjectMapper();
        try {
            SessionFactory sessionFactory =
                    new MetadataSources(registry)
                            .addAnnotatedClass(CustomerDao.class)
                            .addAnnotatedClass(CustomerAddressDao.class)
                            .buildMetadata()
                            .buildSessionFactory();
            sessionFactory.inTransaction(session -> {
                String hql = "FROM CustomerDao c JOIN FETCH c.addresses WHERE c.id =: id";
                Query query = session.createQuery(hql);
                query.setParameter("id", UUID.fromString("8ba282fc-86ae-4859-b2bb-b4026abe9ae7"));
                CustomerDao customers = (CustomerDao) query.getSingleResult();
                out.println();
            });
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
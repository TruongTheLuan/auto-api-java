package tests;

import model.dao.CustomerAddressDao;
import model.dao.CustomerDao;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import utils.DbUtils;

import java.util.List;
import java.util.UUID;

import static java.lang.System.out;

public class DbTest {
    @Test
    void checkDBConnection(){
        CustomerDao customer = DbUtils.getCustomerFromDb("e527e265-1287-4068-bdce-ab721caf708c");
        out.println();
    }
}
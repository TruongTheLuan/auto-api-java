package model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
public class CustomerDao {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String birthday;
    private String phone;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
}

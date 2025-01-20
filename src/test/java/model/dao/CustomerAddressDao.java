package model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "addresses")
@Data
public class CustomerAddressDao {
    @Id
    private UUID id;
    private UUID customerId;
    private String streetNumber;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String state;
    private String zip;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

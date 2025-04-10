package com.ecommerce.ft_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 3, message = "Street name should be atleast 3 characters")
    private String street;

    @NotBlank
    @Size(min = 3, message = "BuildingName name should be atleast 3 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 3, message = "city name should be atleast 3 characters")
    private String city;

    @NotBlank
    @Size(min = 3, message = "state name should be atleast 3 characters")
    private String state;

    @NotBlank
    @Size(min = 3, message = "Country name should be atleast 3 characters")
    private String country;

    @NotBlank
    @Size(min = 6, message = "pincode name should be atleast 3 characters")
    private String pincode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();
}

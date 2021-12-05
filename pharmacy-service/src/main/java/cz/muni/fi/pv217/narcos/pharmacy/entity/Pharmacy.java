package cz.muni.fi.pv217.narcos.pharmacy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
@Entity
@Table(name = "pharmacy")
public class Pharmacy extends PanacheEntity {
    @NotBlank
    public String name;
    @NotBlank
    public String street;
    public int streetNumber;
    @NotBlank
    public String city;
}
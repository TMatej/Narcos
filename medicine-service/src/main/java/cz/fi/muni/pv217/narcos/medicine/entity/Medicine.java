package cz.fi.muni.pv217.narcos.medicine.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author Matej Turek
 */
@Entity
@Table(name = "medicine")
public class Medicine extends PanacheEntity {
    @NotBlank
    public String name;
    @NotBlank
    public String manufacturer;
    @NotNull
    public Form form;
    public int quantity;
    @NotNull
    public LocalDate expirationDate;
}

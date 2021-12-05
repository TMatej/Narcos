package cz.fi.muni.pv217.narcos.medicine.DTO;

import cz.fi.muni.pv217.narcos.medicine.entity.Form;

import java.time.LocalDate;

/**
 * @author Matej Turek
 */
public class MedicineDTO {
    public Long id;
    public String name;
    public String manufacturer;
    public Form form;
    public int quantity;
    public LocalDate expirationDate;
}

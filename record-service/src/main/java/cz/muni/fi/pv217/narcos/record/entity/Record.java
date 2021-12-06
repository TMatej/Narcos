package cz.muni.fi.pv217.narcos.record.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "record")
public class Record extends PanacheEntity {
    @NotNull
    public Long pharmacyId;
    @NotNull
    public Long medicineId;
    public int amount;
}

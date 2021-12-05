package cz.fi.muni.pv217.narcos.medicine.repository;

import cz.fi.muni.pv217.narcos.medicine.entity.Medicine;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Matej Turek
 */
@ApplicationScoped
public class MedicineRepository implements PanacheRepository<Medicine> {
    public Medicine findById(Long id) {
        return find("id", id).firstResult();
    }

    public Medicine findByName(String name) {
        return find("name", name).firstResult();
    }
}

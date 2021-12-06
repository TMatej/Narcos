package cz.muni.fi.pv217.narcos.record.repository;

import cz.muni.fi.pv217.narcos.record.entity.Record;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class RecordRepository implements PanacheRepository<Record> {
    public Stream<Record> findAllMedicineInPharmacy(Long id) {
        return stream("pharmacyId", id);
    }

    public Stream<Record> findAllPharmaciesWithMedicine(Long id) {
        return stream("medicineId", id);
    }

    public Record findRecordOfMedicineInPharmacy(Long pharmacyId, Long medicineId) {
        return find("pharmacyId = ?1 AND medicineId = ?2", pharmacyId, medicineId).firstResult();
    }
}

package cz.muni.fi.pv217.narcos.pharmacy.repository;

import cz.muni.fi.pv217.narcos.pharmacy.entity.Pharmacy;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.stream.Stream;

@ApplicationScoped
public class PharmacyRepository implements PanacheRepository<Pharmacy> {

    public Pharmacy findById(Long id) {
        return find("id", id).firstResult();
    }

    public Pharmacy findByName(String name) {
        return find("name", name).firstResult();
    }

    public Stream<Pharmacy> findByIds(Set<Long> ids) {
        return stream("id in ?1", ids);
    }
}

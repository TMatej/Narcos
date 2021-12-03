package cz.fi.muni.pv217.narcos.user.repository;

import cz.fi.muni.pv217.narcos.user.entity.Person;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Repository of class (entities) Person.
 *
 * @author Matej Turek
 */
@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {
    public Person findById(Long id) {
        return find("id", id).firstResult();
    }

    public Person findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Stream<Person> streamByIds(Set<Long> ids) {
        return streamAll().filter(person -> ids.contains(person.id));
    }
}

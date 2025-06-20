package SR.Lab3.service;

import java.util.List;

import SR.Lab3.entity.AbstractEntity;

public interface Service<T extends AbstractEntity> {

    T read(Long id);

    List<T> read();

    T save(T entity);

    void delete(Long id);

    void edit (T entity);
}

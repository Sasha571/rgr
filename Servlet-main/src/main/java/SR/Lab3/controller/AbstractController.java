package SR.Lab3.controller;

import SR.Lab3.entity.AbstractEntity;
import SR.Lab3.service.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.List;

public abstract class AbstractController<T extends AbstractEntity> {
    protected HttpHeaders headers;

    @PostConstruct
    private void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @GetMapping
    public ResponseEntity<List<T>> get() {
        List<T> entities = getService().read();
        if (entities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entities, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable long id) {
        T entity = getService().read(id);
        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entity, headers, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> put(@RequestBody T entity) {
        getService().save(entity);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<String> post(@RequestBody T entity) {
        getService().save(entity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        getService().delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public abstract Service<T> getService();
}

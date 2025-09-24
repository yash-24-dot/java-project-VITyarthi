package ccrm.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Searchable<T> {
    List<T> search(Predicate<T> predicate);
    List<T> findAll();
    Optional<T> findById(String id); // Changed from T to Optional<T>
}
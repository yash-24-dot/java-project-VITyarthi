package ccrm.service;

public interface Persistable<T> {
    void saveToFile(T entity, String filename);
    T loadFromFile(String filename);
}
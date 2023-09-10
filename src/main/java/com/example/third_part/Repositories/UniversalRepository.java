package com.example.third_part.Repositories;


import javax.naming.directory.SearchResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;
public interface UniversalRepository {
    Optional<?> findEntityById(String modelName, Long id);
    void saveEntity(String modelName, Object entity);
    <T> void updateEntity(T entity, Map<String, String> fieldValues);
    List<SearchResult> searchEntities(String modelName, String fieldName, String fieldValue) throws ClassNotFoundException;
    void deleteEntityById(String modelName, Long id);
}

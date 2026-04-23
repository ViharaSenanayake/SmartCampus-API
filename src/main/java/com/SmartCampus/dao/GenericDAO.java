
package com.SmartCampus.dao;


import com.SmartCampus.model.BaseModel;

import java.util.*;

public class GenericDAO<T extends BaseModel> {
    private final Map<String, T> storage;

    public GenericDAO(Map<String, T> storage) {
        this.storage = storage;
    }

    public void add(T entity) { storage.put(entity.getId(), entity); }
    public T get(String id) { return storage.get(id); }
    public List<T> getAll() { return new ArrayList<>(storage.values()); }
    public void update(T entity) { storage.put(entity.getId(), entity); }
    public void delete(String id) { storage.remove(id); }
}
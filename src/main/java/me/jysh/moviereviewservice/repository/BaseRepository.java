package me.jysh.moviereviewservice.repository;

import me.jysh.moviereviewservice.model.BaseEntity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseRepository<T extends BaseEntity> {

  protected final Map<String, T> store = new HashMap<>();

  public Optional<T> findById(String id) {

    return Optional.ofNullable(this.store.get(id));
  }

  public List<T> findAllBy(Collection<String> ids) {

    return ids.stream().filter(store::containsKey).map(store::get).collect(Collectors.toList());
  }

  public void save(T entity) {

    store.put(entity.getId(), entity);
  }

  public List<T> findAllBy(Predicate<T> entityPredicate) {

    return this.store.values().stream().filter(entityPredicate).collect(Collectors.toList());
  }
}

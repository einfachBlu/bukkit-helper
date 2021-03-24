package de.blu.bukkithelper.repository;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T> {

  @Getter(AccessLevel.PRIVATE)
  private List<T> list = new ArrayList<>();

  public void add(T element) {
    this.getList().add(element);
  }

  public T get(int index) {
    return this.getList().get(index);
  }

  public T getOrDefault(int index, T defaultValue) {
    if (index >= this.getList().size()) {
      return defaultValue;
    }

    if (this.getList().get(index) == null) {
      return defaultValue;
    }

    return this.getList().get(index);
  }

  public void remove(T element) {
    this.getList().remove(element);
  }

  public boolean contains(T element) {
    return this.getList().contains(element);
  }

  public List<T> all() {
    return this.getList();
  }
}

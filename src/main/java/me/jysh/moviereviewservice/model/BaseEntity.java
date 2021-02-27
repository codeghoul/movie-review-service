package me.jysh.moviereviewservice.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BaseEntity {

  String id;

  public BaseEntity() {
    this.id = UUID.randomUUID().toString();
  }
}

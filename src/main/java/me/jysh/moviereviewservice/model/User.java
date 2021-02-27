package me.jysh.moviereviewservice.model;

import lombok.AccessLevel;
import lombok.Getter;
import me.jysh.moviereviewservice.enums.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
public class User extends BaseEntity {

  private final String name;

  @Getter(AccessLevel.NONE)
  private final Set<Role> roles;

  private final Set<String> reviews;

  public User(String name) {

    this.name = name;
    this.roles = new HashSet<>();
    this.reviews = new HashSet<>();
  }

  public void addRole(Role role) {

    this.roles.add(role);
  }

  public void addReview(String reviewId) {

    this.reviews.add(reviewId);
  }

  public boolean hasRole(Role role) {

    return this.roles.contains(role);
  }
}

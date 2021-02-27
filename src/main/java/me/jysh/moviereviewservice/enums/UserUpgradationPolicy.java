package me.jysh.moviereviewservice.enums;

import lombok.AccessLevel;
import lombok.Getter;
import me.jysh.moviereviewservice.model.User;

import java.util.function.Predicate;

/**
 * Contains conditional checks that can be performed on User check eligibility of a new role. <br>
 */
@Getter
public enum UserUpgradationPolicy {
  CRITIC_UPGRADE_POLICY(Role.ROLE_CRITIC, UserUpgradationPolicy::isUpgradableToCritic);

  Role upgradableRole;

  @Getter(AccessLevel.NONE)
  Predicate<User> upgradeCondition;

  UserUpgradationPolicy(Role upgradableRole, Predicate<User> upgradeCondition) {

    this.upgradableRole = upgradableRole;
    this.upgradeCondition = upgradeCondition;
  }

  private static boolean isUpgradableToCritic(User user) {

    return user.hasRole(Role.ROLE_USER) && user.getReviews().size() >= 3;
  }

  public boolean passes(User user) {

    return this.upgradeCondition.test(user);
  }
}

package ru.rulex.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public abstract class RulexMatcher<T> extends TypeSafeMatcher<T> {

  public RulexMatcher<T> and(Matcher<T> matcher) {
    return RulexDsl.and(this, matcher);
  }

  public RulexMatcher<T> or(Matcher<T> matcher) {
    return RulexDsl.or(this, matcher);
  }
}

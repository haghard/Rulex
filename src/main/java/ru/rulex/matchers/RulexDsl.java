package ru.rulex.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class RulexDsl {

  private RulexDsl() {
  // static utility
  }

  public static <T> RulexMatcher<T> no(final Matcher<T> matcher) {
    return new RulexMatcher<T>() {
      @Override
      public boolean matchesSafely(final T item) {
        return !matcher.matches(item);
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("NO (");
        matcher.describeTo(description);
        description.appendText(")");
      }
    };
  }

  public static <T> RulexMatcher<T> and(final Matcher<T> matcher1,
      final Matcher<T> matcher2) {
    return new RulexMatcher<T>() {
      @Override
      public boolean matchesSafely(final T item) {
        return matcher1.matches(item) && matcher2.matches(item);
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("(");
        matcher1.describeTo(description);
        description.appendText(") AND (");
        matcher2.describeTo(description);
        description.appendText(")");
      }
    };
  }

  public static <T> RulexMatcher<T> or(final Matcher<T> matcher1,
      final Matcher<T> matcher2) {
    return new RulexMatcher<T>() {
      @Override
      public boolean matchesSafely(final T item) {
        return matcher1.matches(item) || matcher2.matches(item);
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("(");
        matcher1.describeTo(description);
        description.appendText(") OR (");
        matcher2.describeTo(description);
        description.appendText(")");
      }
    };
  }
}

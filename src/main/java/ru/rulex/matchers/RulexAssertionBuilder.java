package ru.rulex.matchers;

import org.hamcrest.Matcher;

public interface RulexAssertionBuilder<T>
{
  RulexRule<T> assertThat( Matcher<T> assertionMatcher );
}

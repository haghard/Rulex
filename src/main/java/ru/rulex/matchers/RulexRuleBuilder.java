package ru.rulex.matchers;

import org.hamcrest.Matcher;

public interface RulexRuleBuilder<T> extends RulexAssertionBuilder<T>
{
  RulexAssertionBuilder<T> forEach();

  RulexAssertionBuilder<T> forEach( Matcher<T> matcher );
}

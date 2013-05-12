/*
 * 
 * 
 */
package ru.rulex.matchers;

import org.hamcrest.Description;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.RulexMatchersDsl;
import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;
/**
 * 
 * @author haghard
 * 
 * @param <T>
 * 
 */
public final class RulexMatchersBuilder<T> {

  private final SelectorMatcherAdapter<T> adapter;

  public RulexMatchersBuilder(SelectorMatcherAdapter<T> adapter) {
    this.adapter = adapter;
  }

  public RulexMatcher<T> lessThan(final RulexMatchersBuilder<T> param) {
    return new RulexMatcher<T>() {
      @Override
      protected boolean matchesSafely(final T item) {
        throw new UnsupportedOperationException("RulexMatchersBuilder lessThan(final RulexMatchersBuilder<T> param)");
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText(adapter.matcherDisplayName() + " < "
            + param.getAdapter().matcherDisplayName());
      }
    };
  }

  public <E extends Number & Comparable<? super E>> RulexMatcher<T> lessThan(final E value) {
    return new RulexMatcher<T>() {
      @Override
      protected boolean matchesSafely(final T item) {
        ConclusionPredicate<E> pred = RulexMatchersDsl.<E>lessThan(value);
        Selector<T, E> selector = (Selector<T, E>)adapter.valueOf(item);
        ConclusionPredicate<T> p = new SelectorPredicate<T, E>(pred, selector);
        return p.apply(item);
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText(adapter.matcherDisplayName() + " < " + value);
      }
    };
  }

  public RulexMatcher<T> isTrue() {

    return new RulexMatcher<T>() {
      @Override
      protected boolean matchesSafely(final T item) {
        return true;
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText(adapter.matcherDisplayName());
      }
    };
  }

  SelectorMatcherAdapter<T> getAdapter() {
    return adapter;
  }
}

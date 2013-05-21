/*
 * Copyright (C) 2013 The Conclusions Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.rulex.matchers;

import java.util.Arrays;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import ru.rulex.conclusion.Selector;
import com.google.common.collect.Iterables;

import ru.rulex.conclusion.delegate.ProxyUtils;

/**
 * @author haghard
 * @param <T>
 */
public final class RulexObjectMatcher<T> implements RulexRuleBuilder<T>, RulexRule<T>,
    RulexAnalyzer
{

  private final Class<T> clazz;

  private Matcher<T> assertionMatcher;

  private Matcher<T> filterMatcher;

  private Iterable<T> iterator;

  private RulexObjectMatcher( Class<T> clazz )
  {
    this.clazz = clazz;
  }

  public <E extends Comparable<? super E>> RulexMatchersBuilder<T> selector( final E arg )
  {
    return new RulexMatchersBuilder<T>( new SelectorAdapter<T>()
    {
      @Override
      public Selector<T, E> selector( final T matched )
      {
        return ProxyUtils.toSelector( arg );
      }

      @Override
      public String matcherDisplayName()
      {
        return "Selectors(" + clazz.getName() + ")";
      }
    } );
  }

  /**
   * Simple, one level selector
   * 
   * @param type
   * @param arg
   * @return builder
   */
  public static <T, E extends Comparable<? super E>> RulexMatchersBuilder<T> selector(
      final Class<T> type, final E arg )
  {
    return new RulexMatchersBuilder<T>( new SelectorAdapter<T>()
    {
      @Override
      public Selector<T, E> selector( final T matched )
      {
        return ProxyUtils.toSelector( arg );
      }

      @Override
      public String matcherDisplayName()
      {
        return "Selectors(" + type.getClass().getName() + ")";
      }
    } );
  }

  public static <T> RulexRuleBuilder<T> projection( Class<T> clazz )
  {
    return new RulexObjectMatcher<T>( clazz );
  }

  @Override
  public RulexRule<T> assertThat( Matcher<T> assertionMatcher )
  {
    this.assertionMatcher = assertionMatcher;
    return this;
  }

  @Override
  public RulexAnalyzer in( final Iterable<T> iterator )
  {
    this.iterator = Iterables.unmodifiableIterable( iterator );
    return this;
  }

  @Override
  public RulexAnalyzer on( final T item )
  {
    this.iterator = Iterables.unmodifiableIterable( Arrays.asList( item ) );
    return this;
  }

  @Override
  public void analyze( final AssertionAwareListener listener )
  {
    if ( listener == null )
    {
      throw new IllegalArgumentException( "listener cannot be null" );
    }

    for ( T currentlyAnalysed : iterator )
    {
      doAnalyse( listener, currentlyAnalysed );
    }

    listener.done();
  }

  private void doAnalyse( AssertionAwareListener listener, T currentlyAnalysed )
  {
    try
    {
      if ( filterMatcher == null || filterMatcher.matches( currentlyAnalysed ) )
      {
        if ( assertionMatcher.matches( currentlyAnalysed ) )
        {
          listener.passed( currentlyAnalysed, assertionMatcher );
        }
        else
        {
          listener.failed( currentlyAnalysed, assertionMatcher );
        }
      }
      else
      {
        listener.filtered( currentlyAnalysed, filterMatcher );
      }
    }
    catch ( Exception e )
    {
      listener.unexpected( currentlyAnalysed, e );
    }
  }

  @Override
  public RulexAssertionBuilder<T> forEach()
  {
    this.filterMatcher = trueMatcher();
    return this;
  }

  @Override
  public RulexAssertionBuilder<T> forEach( Matcher<T> filterMatcher )
  {
    this.filterMatcher = filterMatcher;
    return this;
  }

  private Matcher<T> trueMatcher()
  {
    return new RulexMatcher<T>()
    {
      @Override
      protected boolean matchesSafely( final T item )
      {
        return true;
      }

      @Override
      public void describeTo( final Description description )
      {
        description.appendText( "TRUE" );
      }
    };
  }
}

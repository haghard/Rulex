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

import org.hamcrest.Description;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.RulexMatchersDsl;
import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;

@SuppressWarnings("unchecked")
public final class RulexMatchersBuilder<T>
{
  private final SelectorDelegate<T> delegate;

  public RulexMatchersBuilder( SelectorDelegate<T> delegate )
  {
    this.delegate = delegate;
  }

  public <E extends Number & Comparable<? super E>> RulexVerb<T> lessThan(
      final RulexMatchersBuilder<T> otherBuilder )
  {
    return new RulexVerb<T>( getDelegate() )
    {
      @Override
      protected boolean matchesSafely( final T item )
      {
        final Selector<T, E> lSelector = (Selector<T, E>) getDelegate().selector( item );
        final Selector<T, E> rSelector = (Selector<T, E>) otherBuilder.getDelegate().selector( item );
        return lSelector.select( item ).compareTo( rSelector.select( item ) ) < 0;
      }

      @Override
      public void describeTo( final Description description )
      {
        description.appendText( getDelegate().matcherDisplayName() + " $lessThan "
            + otherBuilder.getDelegate().matcherDisplayName() );
      }
    };
  }

  public <E extends Number & Comparable<? super E>> RulexVerb<T> lessThan( final E value )
  {
    return new RulexVerb<T>( getDelegate() )
    {
      @Override
      protected boolean matchesSafely( final T item )
      {
        ConclusionPredicate<E> pred = RulexMatchersDsl.<E> lessThan( value );
        Selector<T, E> selector = (Selector<T, E>) getDelegate().selector( item );
        ConclusionPredicate<T> p = new SelectorPredicate<T, E>( pred, selector );
        return p.apply( item );
      }

      @Override
      public void describeTo( final Description description )
      {
        description.appendText( delegate.matcherDisplayName() + " $lessThan " + value );
      }
    };
  }

  public <E extends Comparable<? super E>> RulexVerb<T> isEquals(
      final RulexMatchersBuilder<T> otherBuilder )
  {
    return new RulexVerb<T>( getDelegate() )
    {
      @Override
      protected boolean matchesSafely( T item )
      {
        Selector<T, E> lSelector = (Selector<T, E>) getDelegate().selector( item );
        Selector<T, E> rSelector = (Selector<T, E>) otherBuilder.getDelegate().selector( item );
        return lSelector.select( item ).compareTo( rSelector.select( item ) ) == 0;
      }

      @Override
      public void describeTo( Description description )
      {

      }
    };
  }

  public <E extends Comparable<? super E>> RulexVerb<T> isEquals( final E value )
  {
    return new RulexVerb<T>( getDelegate() )
    {

      @Override
      protected boolean matchesSafely( T item )
      {
        ConclusionPredicate<E> pred = RulexMatchersDsl.<E> eq( value );
        Selector<T, E> selector = (Selector<T, E>) getDelegate().selector( item );
        ConclusionPredicate<T> p = new SelectorPredicate<T, E>( pred, selector );
        return p.apply( item );
      }

      @Override
      public void describeTo( Description description )
      {
        description.appendText( delegate.matcherDisplayName() + " $isEquals: " + value );
      }
    };
  }

  public <E extends Number & Comparable<? super E>> RulexVerb<T> moreThan( final E value )
  {
    return new RulexVerb<T>( getDelegate() )
    {
      @Override
      protected boolean matchesSafely( T item )
      {
        ConclusionPredicate<E> pred = RulexMatchersDsl.<E> greaterThan( value );
        Selector<T, E> selector = (Selector<T, E>) getDelegate().selector( item );
        ConclusionPredicate<T> p = new SelectorPredicate<T, E>( pred, selector );
        return p.apply( item );
      }

      @Override
      public void describeTo( Description description )
      {
        description.appendText( delegate.matcherDisplayName() + " $moreThan: " + value );
      }
    };
  }

  public <E extends Comparable<? super E>> RulexVerb<T> moreThan(
      final RulexMatchersBuilder<T> otherBuilder )
  {
    return new RulexVerb<T>( getDelegate() )
    {
      @Override
      protected boolean matchesSafely( T item )
      {
        Selector<T, E> lSelector = (Selector<T, E>) getDelegate().selector( item );
        Selector<T, E> rSelector = (Selector<T, E>) otherBuilder.getDelegate().selector( item );
        return lSelector.select( item ).compareTo( rSelector.select( item ) ) > 0;
      }

      @Override
      public void describeTo( Description description )
      {

      }
    };
  }

  private SelectorDelegate<T> getDelegate()
  {
    return delegate;
  }
}

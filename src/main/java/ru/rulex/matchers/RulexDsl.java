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
import org.hamcrest.Matcher;

import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.delegate.ProxyUtils;

public class RulexDsl
{

  private RulexDsl()
  {
    // static utility
  }

  public static <T> RulexMatcher<T> no( final RulexMatcher<T> matcher )
  {
    return new RulexMatcher<T>( matcher.getAdapter() )
    {
      @Override
      public boolean matchesSafely( final T item )
      {
        return !matcher.matches( item );
      }

      @Override
      public void describeTo( final Description description )
      {
        description.appendText( "NO (" );
        matcher.describeTo( description );
        description.appendText( ")" );
      }
    };
  }

  public static <T> RulexMatcher<T> and( final RulexMatcher<T> matcher1,
      final Matcher<T> matcher2 )
  {
    return new RulexMatcher<T>( matcher1.getAdapter() )
    {
      @Override
      public boolean matchesSafely( final T item )
      {
        return matcher1.matches( item ) && matcher2.matches( item );
      }

      @Override
      public void describeTo( final Description description )
      {
        description.appendText( "(" );
        matcher1.describeTo( description );
        description.appendText( ") AND (" );
        matcher2.describeTo( description );
        description.appendText( ")" );
      }
    };
  }

  public static <T> RulexMatcher<T> or( final RulexMatcher<T> matcher1,
      final Matcher<T> matcher2 )
  {
    return new RulexMatcher<T>( matcher1.getAdapter() )
    {
      @Override
      public boolean matchesSafely( final T item )
      {
        return matcher1.matches( item ) || matcher2.matches( item );
      }

      @Override
      public void describeTo( final Description description )
      {
        description.appendText( "(" );
        matcher1.describeTo( description );
        description.appendText( ") OR (" );
        matcher2.describeTo( description );
        description.appendText( ")" );
      }
    };
  }

  public static <T> RulexMatcher<T> statefull( final RulexMatcher<T> matcher )
  {

    matcher.setAdapter( new SelectorAdapter<T>()
    {
      Selector<T, ?> selector = ProxyUtils.toSelector( null );

      @Override
      public Selector<T, ?> selector( final T matched )
      {
        return selector;
      }

      @Override
      public String matcherDisplayName()
      {
        return "";
      }
    } );
    return matcher;
  }
}

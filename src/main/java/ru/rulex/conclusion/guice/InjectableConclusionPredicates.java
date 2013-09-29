/*
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.rulex.conclusion.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import ru.rulex.conclusion.ConclusionPredicate;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public final class InjectableConclusionPredicates
{
  @Inject
  private InjectableConclusionPredicates()
  {
  }

  public static class InjectableAlwaysFalsePredicate<T> implements ConclusionPredicate<T>
  {
    private final T boolValue;

    @Inject
    public InjectableAlwaysFalsePredicate( Boolean boolValue )
    {
      this.boolValue = ( T ) boolValue;
    }

    @Override
    public boolean apply( T value )
    {
      return Boolean.FALSE;
    }

    @Override
    public java.lang.String toString()
    {
      return String.format( " $always false " );
    }
  }

  public static class InjectableAlwaysTruePredicate<T> implements ConclusionPredicate<T>
  {
    private final T boolValue;

    @Inject
    public InjectableAlwaysTruePredicate( Boolean boolValue )
    {
      this.boolValue = ( T ) boolValue;
    }

    @Override
    public boolean apply( T value )
    {
      return Boolean.FALSE;
    }

    @Override
    public java.lang.String toString()
    {
      return String.format( " $always false " );
    }
  }

  public static class InjectableIsNullConclusionPredicate<T> implements ConclusionPredicate<T>
  {
    @Override
    public boolean apply( T argument )
    {
      return argument == null;
    }

    @Override
    public String toString()
    {
      return " $IsNull ";
    }
  }

  public static class InjectableNotNullConclusionPredicate<T> implements ConclusionPredicate<T>
  {
    @Override
    public boolean apply( T argument )
    {
      return argument != null;
    }

    @Override
    public String toString()
    {
      return " $isNotNull ";
    }
  }

  public static class InjectableEqualsConclusionPredicate<T extends Comparable<? super T>> implements
      ConclusionPredicate<T>
  {
    private final T parameter;

    @Inject
    public InjectableEqualsConclusionPredicate( T parameter )
    {
      this.parameter = checkNotNull( parameter );
    }

    @Override
    public boolean apply( T argument )
    {
      return parameter.compareTo( argument ) == 0;
    }

    @Override
    public String toString()
    {
      return String.format( "%s $equal ", parameter );
    }
  }

  public static class InjectableLessConclusionPredicate<T extends Comparable<? super T>> implements
      ConclusionPredicate<T>
  {
    private final T parameter;

    @Inject
    public InjectableLessConclusionPredicate( T parameter )
    {
      this.parameter = checkNotNull( parameter );
    }

    @Override
    public boolean apply( T argument )
    {
      return parameter.compareTo( argument ) < 0;
    }

    @Override
    public String toString()
    {
      return String.format( "%s $less ", parameter );
    }
  }

  public static class InjectableMoreConclusionPredicate<T extends Comparable<? super T>> implements
      ConclusionPredicate<T>
  {
    private final T parameter;

    @Inject
    public InjectableMoreConclusionPredicate( T parameter )
    {
      this.parameter = checkNotNull( parameter );
    }

    @Override
    public boolean apply( T argument )
    {
      return parameter.compareTo( argument ) > 0;
    }

    @Override
    public String toString()
    {
      return String.format( "%s $more ", parameter );
    }
  }

  /**
   * moreOrEquals operation
   * @param <T>
   */
  public static class InjectableAtLeastConclusionPredicate<T extends Comparable<? super T>> implements
    ConclusionPredicate<T> 
  {
    private final T parameter;

    @Inject
    public InjectableAtLeastConclusionPredicate( T parameter )
    {
      this.parameter = parameter;
    }
    
    @Override
    public boolean apply( T argument )
    {
      return parameter.compareTo( argument ) >= 0;
    }

    @Override
    public String toString()
    {
      return String.format( "%s $atLeast ", parameter );
    }
  }

  /**
   * lessOrEquals
   * @param <T>
   */
  public static class InjectableAtMostConclusionPredicate<T extends Comparable<? super T>> implements
    ConclusionPredicate<T> 
  {
    private final T parameter;

    @Inject
    public InjectableAtMostConclusionPredicate( T parameter )
    {
      this.parameter = parameter;
    }
    
    @Override
    public boolean apply( T argument )
    {
      return parameter.compareTo( argument ) <= 0;
    }
  
    @Override
    public String toString()
    {
      return String.format( "%s $atMost ", parameter );
    }
  }

  public static class InjectableRegexpPredicate<T> implements  ConclusionPredicate<T>
  {
    private final Pattern pattern;

    @Inject
    InjectableRegexpPredicate( String expression )
    {
      pattern = Pattern.compile( expression, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE );
    }

    @Override
    public boolean apply( T argument )
    {
      return pattern.matcher((CharSequence) argument).find();
    }

    @Override public String toString()
    {
      return String.format("%s $match ", pattern );
    }
  }

  public static class InjectableMultiRegexpPredicate<T> implements  ConclusionPredicate<T>
  {
    private ImmutableSet<Pattern> patterns = ImmutableSet.of();

    @Inject
    InjectableMultiRegexpPredicate( ImmutableSet<String> expressions )
    {
      final ImmutableSet.Builder<Pattern> builder = ImmutableSet.builder();
      for ( String expression : expressions )
      {
          builder.add( Pattern.compile( expression, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE ) );
      }
      patterns = builder.build();
    }

    @Override
    public boolean apply( T argument )
    {
      for (Pattern pattern : patterns)
        if (pattern.matcher((CharSequence) argument).find())
          return true;

      return false;
    }

    @Override
    public String toString()
    {
      return String.format("%s $match any ", patterns );
    }
  }

  public static class InjectableMatchAnyOffPredicate<T> implements ConclusionPredicate<T>
  {
    private final ImmutableSet<String> variants;

    @Inject
    public InjectableMatchAnyOffPredicate( ImmutableSet<String> variants0 )
    {
      this.variants = ImmutableSet.copyOf( variants0 );
    }

    @Override
    public boolean apply( T value )
    {
      return variants.contains( value );
    }

    @Override public String toString()
    {
      return String.format("%s $equalsAny ", variants );
    }
  }

  public static class InjectableAnyOffConclusionPredicate<T> implements ConclusionPredicate<T>
  {
    private final ImmutableList<ConclusionPredicate> predicates;

    @Inject
    InjectableAnyOffConclusionPredicate( ImmutableList<ConclusionPredicate> predicates )
    {
      this.predicates = checkNotNull( ImmutableList.copyOf( predicates ) );
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean apply( T argument )
    {
      for (ConclusionPredicate predicate : predicates)
      {
        if ( predicate.apply( argument ) )
        {
          return true;
        }
      }
      return false;
    }
  }
}

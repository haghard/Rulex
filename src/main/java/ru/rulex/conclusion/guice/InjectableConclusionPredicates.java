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
import com.google.inject.Inject;
import ru.rulex.conclusion.ConclusionPredicate;
import static com.google.common.base.Preconditions.checkNotNull;

public final class InjectableConclusionPredicates
{
  @Inject
  private InjectableConclusionPredicates()
  {
  }

  public static class InjectableEqualsConclusionPredicate<T extends Comparable<? super T>> implements
      ConclusionPredicate<T>
  {
    private final T parameter;

    @Inject
    InjectableEqualsConclusionPredicate( T parameter )
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

  public static class InjectableMoreOrEqualsConclusionPredicate<T extends Comparable<? super T>> implements
    ConclusionPredicate<T> 
  {
    private final T parameter;

    @Inject
    public InjectableMoreOrEqualsConclusionPredicate( T parameter )
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
      return String.format( "%s $moreOrEquals ", parameter );
    }
  }

  public static class InjectableLessOrEqualsConclusionPredicate<T extends Comparable<? super T>> implements
    ConclusionPredicate<T> 
  {
    private final T parameter;

    @Inject
    public InjectableLessOrEqualsConclusionPredicate( T parameter )
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
      return String.format( "%s $lessOrEquals ", parameter );
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

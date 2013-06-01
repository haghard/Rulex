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
package ru.rulex.conclusion;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractRulexDsl<T> implements ConclusionPredicate<T>
{

  public List<ConclusionPredicate<? super T>> asList( ConclusionPredicate<? super T> first,
      ConclusionPredicate<? super T> second )
  {
    return Arrays.<ConclusionPredicate<? super T>> asList( first, second );
  }

  public FluentConclusionPredicate<T> and( final ConclusionPredicate<? super T> p,
      final ConclusionPredicate<? super T> p0 )
  {
    return new FluentConclusionPredicate.AndConclusionPredicate<T>( asList( p, p0 ).iterator() );
  }

  public FluentConclusionPredicate<T> or( final ConclusionPredicate<? super T> p,
      final ConclusionPredicate<? super T> p0 )
  {
    return new FluentConclusionPredicate.OrConclusionPredicate<T>( asList( p, p0 ).iterator() );
  }
}
/**
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file without in compliance with the License. You may obtain a copy
 * of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * <pre>
 */
package ru.rulex.conclusion;

import static ru.rulex.conclusion.FluentConclusionPredicate.typeSafeQuery;
import static ru.rulex.conclusion.FluentConclusionPredicate.lambda;
import static ru.rulex.conclusion.FluentConclusionPredicate.number;
import ru.rulex.conclusion.execution.ParallelStrategy;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.EventOrientedPhrasesBuilder;

public final class ConclusionPhrase
{
  /**
   * 
   * @param value
   * @param argumentClass
   * @param methodName
   * @param predicate
   * @return
   * 
   */
  public static <E extends Number & Comparable<? super E>, T> AbstractEventOrientedPhrasesBuilder phrase(
      final E value, final Class<T> argumentClass, final String methodName,
      final ConclusionPredicate<E> predicate )
  {
    return phrase( "default", value, argumentClass, methodName, predicate );
  }

  /**
   * 
   * @param termName
   * @param value
   * @param argumentClass
   * @param methodName
   * @param predicate
   * @return AbstractConclusionExecutionPhrasesBuilder
   * 
   */
  public static <E extends Number & Comparable<? super E>, T> AbstractEventOrientedPhrasesBuilder phrase(
      final String termName, final E value, final Class<T> argumentClass, final String methodName,
      final ConclusionPredicate<E> predicate )
  {
    return phrase( termName, value, argumentClass, methodName, predicate,
        ParallelStrategy.<Boolean> serial() );
  }

  /**
   * 
   * @param termName
   * @param value
   * @param argumentClass
   * @param methodName
   * @param predicate
   * @param pStrategy
   * @return AbstractConclusionExecutionPhrasesBuilder
   */
  public static <E extends Number & Comparable<? super E>, T> AbstractEventOrientedPhrasesBuilder phrase(
      final String termName, final E value, final Class<T> argumentClass, final String methodName,
      final ConclusionPredicate<E> predicate,
      final ParallelStrategy<Boolean> pStrategy0 )
  {
    return new EventOrientedPhrasesBuilder()
    {
      @SuppressWarnings("unchecked")
      protected void build()
      {
        rule( pStrategy0, argumentClass, termName ).shouldMatch(
            typeSafeQuery( number( argumentClass, (Class<E>) value.getClass(), methodName ),
                lambda( predicate ) ) );
      }
    };
  }

  /**
   * 
   * @param PhraseSettings
   *          <E,T> termSettings
   * @return AbstractConclusionExecutionPhrasesBuilder
   * 
   */
  @SuppressWarnings("unchecked")
  public static <E extends Number & Comparable<? super E>, T> AbstractEventOrientedPhrasesBuilder phrase(
      final String termName, final PhraseSettings<E, T> phraseSettings )
  {
    return new EventOrientedPhrasesBuilder()
    {
      protected void build()
      {
        rule( phraseSettings.<Boolean> getParallelStrategy(),
            phraseSettings.getTargetClass(), termName ).shouldMatch(
            typeSafeQuery(
                number( phraseSettings.getTargetClass(), (Class<E>) phraseSettings.getValue()
                    .getClass(), phraseSettings.getAccessorName() ), lambda( phraseSettings
                    .getPredicate() ) ) );
      }
    };
  }
}

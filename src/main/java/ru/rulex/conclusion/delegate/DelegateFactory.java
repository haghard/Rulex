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
package ru.rulex.conclusion.delegate;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.FluentConclusionPredicate;

import java.lang.reflect.Array;

import static ru.rulex.conclusion.delegate.ProxyUtils.toPredicate;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class DelegateFactory
{

  public static <T> ConclusionPredicate<Delegate<T>> from( T element )
  {
    return construct( element );
  }

  public static <T> ConclusionPredicate<Delegate<T>> from( T element1, T element2 )
  {
    return construct( element1, element2 );
  }

  public static <T> ConclusionPredicate<Delegate<T>> from( T element1, T element2, T element3 )
  {
    return construct( element1, element2, element3 );
  }

  public static <T> ConclusionPredicate<Delegate<T>> from( T element1, T element2, T element3,
      T element4 )
  {
    return construct( element1, element2, element3, element4 );
  }

  public static <T> ConclusionPredicate<Delegate<T>> from( T element1, T element2, T element3,
      T element4, T element5 )
  {
    return construct( element1, element2, element3, element4, element5 );
  }

  @SuppressWarnings("unchecked")
  private static <T> ConclusionPredicate<Delegate<T>> construct( Object... elements )
  {
    T[] params;
    // for support FluentConclusionPredicate subclasses
    if ( elements[0] instanceof FluentConclusionPredicate )
    {
      params = (T[]) Array.newInstance( FluentConclusionPredicate.class, elements.length );
    }
    else
    {
      params = (T[]) Array.newInstance( elements[0].getClass(), elements.length );
    }
    System.arraycopy( elements, 0, params, 0, elements.length );
    return createPredicate( params );
  }

  /**
   * @param element1
   * @param element2
   * @param element3
   * @param element4
   * @param element5
   * @param others
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> ConclusionPredicate<Delegate<T>> values( T element1, T element2, T element3,
      T element4, T element5, T... others )
  {
    final int paramCount = 5;
    T[] params = (T[]) Array.newInstance( element1.getClass(), paramCount + others.length );
    params[0] = element1;
    params[1] = element2;
    params[2] = element3;
    params[3] = element4;
    params[4] = element5;
    System.arraycopy( others, 0, params, paramCount, others.length );
    return createPredicate( params );
  }

  /**
   * Create predicate which store base on passed param
   * 
   * @param params
   * @param <T>
   * @return ConclusionPredicate<Delegate<T>>
   */
  @SuppressWarnings("unchecked")
  private static <T> ConclusionPredicate<Delegate<T>> createPredicate( T[] params )
  {
    return toPredicate( callOn( Delegate.class ).execute( params ) );
  }
}
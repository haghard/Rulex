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
package ru.rulex.conclusion.execution;

import ru.rulex.conclusion.ConclusionFunction;
import ru.rulex.conclusion.PhraseExecutionException;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import com.google.common.util.concurrent.CheckedFuture;

public final class Callables
{

  public static <A> Callable<A> identity( final A argument )
  {
    return new Callable<A>()
    {
      @Override
      public A call() throws Exception
      {
        return argument;
      }
    };
  }

  /**
   * 
   * @param unit
   * @return A
   * @throws Exception
   */
  public static <A> A call( final Callable<A> unit ) throws Exception
  {
    return unit.call();
  }

  /**
   * Create new Callable which wrap function apply call with unit
   * 
   * @param Callable
   *          <A> - unit A
   * @param ConclusionFunction
   *          <A, Callable<B>> - function
   * @return Callable<B> - unit B
   */
  public static <A, B> Callable<B> bind( final Callable<A> unit,
      final ConclusionFunction<A, Callable<B>> function )
  {
    return new Callable<B>()
    {
      public B call() throws Exception
      {
        return function.apply( unit.call() ).call();
      }
    };
  }

  /**
   * 
   * @param ConclusionFunction
   *          <A, B> function
   * @return ConclusionFunction<Callable<A>, Callable<B>> function
   */
  public static <A, B> ConclusionFunction<Callable<A>, Callable<B>> fmap(
      final ConclusionFunction<A, B> function )
  {
    return new ConclusionFunction<Callable<A>, Callable<B>>()
    {
      public Callable<B> apply( final Callable<A> a )
      {
        return bind( a, new ConclusionFunction<A, Callable<B>>()
        {
          public Callable<B> apply( final A argument )
          {
            return new Callable<B>()
            {
              public B call()
              {
                return function.apply( argument );
              }
            };
          }
        } );
      }
    };
  }

  /**
   * 
   * Wrap values of type Future<T> inside of a Callable<T> so that we can
   * manipulate their return values while they are running
   * 
   * Implicitly way to do similar {@code future.checkedGet() }
   * 
   * @param ListenableFuture
   *          <T>
   * @return Callable<T>
   */
  public static <T> Callable<T> obtain( final CheckedFuture<T, PhraseExecutionException> future )
  {
    return new Callable<T>()
    {
      public T call() throws PhraseExecutionException
      {
        return future.checkedGet();
      }
    };
  }

  /**
   * 
   * @param future
   * @param listener
   * @param executor
   * @return Callable<T>
   */
  public static <T> Callable<T> obtain( final CheckedFuture<T, PhraseExecutionException> future,
      final Runnable listener, final Executor executor )
  {
    return new Callable<T>()
    {
      public T call() throws PhraseExecutionException
      {
        future.addListener( listener, executor );
        return future.checkedGet();
      }
    };
  }

  /**
   * 
   * @param effectFunction
   * @return
   */
  public static <T, E> ConclusionFunction<T, Callable<E>> curry(final ConclusionFunction<T, E> effectFunction) {
    return new ConclusionFunction<T, Callable<E>>() {
      @Override
      public Callable<E> apply( final T argument )
      {
        return new Callable<E>() {
          @Override
          public E call() throws Exception
          {
            return effectFunction.apply( argument );
          }
        };
      }
    };
  }
}

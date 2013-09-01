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
import ru.rulex.conclusion.ErrorCode;
import ru.rulex.conclusion.PhraseErrorCode;
import ru.rulex.conclusion.PhraseExecutionException;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
/**
 * 
 * Class for separate parallel execution from algorithm itself.
 * 
 * Used CheckedFuture<T, E> for purpose to translate
 * {@link InterruptedException}, {@link CancellationException} and
 * {@link ExecutionException} into application-specific exceptions using method
 * getChecked()
 *  
 */
public final class ParallelStrategy<T>
{

  private final ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>> function;  

  private static final ListeningExecutorService COMPUTATION_EXECUTOR = createComputationExecutor();

  private static final ListeningExecutorService SAME_THREAD_EXECUTOR = MoreExecutors.sameThreadExecutor();

  
  private static Function<Exception, PhraseExecutionException> createMapper(final ErrorCode code, final String[] line )
  {
    return new Function<Exception, PhraseExecutionException>()
    {
      public PhraseExecutionException apply( Exception clause )
      {
        // for debug only
        clause.printStackTrace();
        return new PhraseExecutionException( code, line, clause );
      }
    };
  }

  private ParallelStrategy( ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>> function)
  {
    this.function = function;
  }

  private ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>> function() 
  {
    return function;
  }

  /**
   * Function composition.
   *
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C> ConclusionFunction<A, C> compose(final ConclusionFunction<B, C> f, final ConclusionFunction<A, B> g) {
    return new ConclusionFunction<A, C>() {
      public C apply(final A a) {
        return f.apply(g.apply(a));
      }
    };
  }

  /**
   * Wrap target function into other function
   * 
   * @param function
   * @return
   */
  public <B> ConclusionFunction<B, CheckedFuture<T, PhraseExecutionException>> lift(
      final ConclusionFunction<B, T> function )
  {
    final ParallelStrategy<T> self = this;
    return new ConclusionFunction<B, CheckedFuture<T, PhraseExecutionException>>()
    {
      public CheckedFuture<T, PhraseExecutionException> apply( final B b )
      {
        return self.function().apply( new Callable<T>()
        {
          public T call()
          {
            return function.apply( b );
          }
        } );
      }
    };
  }

  /**
   * @param function
   *          which implement concrete thread strategy
   * @return instance {@code ParallelStrategy} with has function parameter as
   *         phrase
   */
  private static <T> ParallelStrategy<T> toCheckedStrategy(
      ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>> function )
  {
    return new ParallelStrategy<T>( function );
  }

  /**
   * @return ParallelStrategy instance which create single thread for on every
   *         call
   */
  public static <T> ParallelStrategy<T> separateThreadStrategy()
  {
    return toCheckedStrategy( new ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>>()
    {
      @Override
      public CheckedFuture<T, PhraseExecutionException> apply( Callable<T> input )
      {
        final ListenableFutureTask<T> task = ListenableFutureTask.create( input );
        new Thread( task ).start();
        Function<Exception, PhraseExecutionException> mapper = ParallelStrategy.createMapper( 
            PhraseErrorCode.ERROR, new String[] { "singleThreadStrategy" } );
        return Futures.makeChecked( task, mapper );
      }
    } );
  }

  /**
   * 
   * @return ParallelStrategy<T, E> pStrategy
   */
  public static <T> ParallelStrategy<T> serial()
  {
    return toCheckedStrategy( new ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>>()
    {
      @Override
      public CheckedFuture<T, PhraseExecutionException> apply( Callable<T> input )
      {
        return Futures.makeChecked( SAME_THREAD_EXECUTOR.submit( input ), 
            createMapper( PhraseErrorCode.ERROR, new String[] { "sameThreadStrategy" } ));
      }
    } );
  }

  /**
   * @return ParallelStrategy<T, E> pStrategy
   */
  public static <T> ParallelStrategy<T> listenableFutureStrategy(
      final ListeningExecutorService es )
  {
    return toCheckedStrategy( new ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>>()
    {
      @Override
      public CheckedFuture<T, PhraseExecutionException> apply( Callable<T> task )
      {
        return Futures.makeChecked( es.submit( task ), 
            ParallelStrategy.createMapper( PhraseErrorCode.ERROR, new String[] { "listenableFutureStrategy" } ));
      }
    } );
  }

  /**
   * 
   * @return
   */
  public static <T> ParallelStrategy<T> defaultListenableFutureStrategy()
  {
    return toCheckedStrategy( new ConclusionFunction<Callable<T>, CheckedFuture<T, PhraseExecutionException>>()
    {
      @Override
      public CheckedFuture<T, PhraseExecutionException> apply( Callable<T> task )
      {
        return Futures.makeChecked( COMPUTATION_EXECUTOR.submit( task ), 
            ParallelStrategy.createMapper( PhraseErrorCode.ERROR, new String[] { "listenableFutureStrategy" }  ));
      }
    } );
  }

  private static ListeningExecutorService createComputationExecutor()
  {
    int cores = Runtime.getRuntime().availableProcessors();
    return MoreExecutors.listeningDecorator( Executors.newScheduledThreadPool( cores,
        new ThreadFactory()
        {
          final AtomicInteger counter = new AtomicInteger();

          @Override
          public Thread newThread( Runnable r )
          {
            Thread t = new Thread( r, "FcComputationThreadPool-" + counter.incrementAndGet() );
            t.setDaemon( true );
            return t;
          }
        } ) );
  }
}

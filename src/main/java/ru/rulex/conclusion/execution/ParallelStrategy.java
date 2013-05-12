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

import ru.rulex.conclusion.CodedException;
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
 * {@link ExecutionException} into application-specific exceptions
 * using method getChecked()
 *
 */
public final class ParallelStrategy<T, E extends Exception> {

  private final ConclusionFunction<Callable<T>, CheckedFuture<T, E>> function;

  private static final ListeningExecutorService COMPUTATION_EXECUTOR = createComputationExecutor();

  @SuppressWarnings("unchecked")
  private static <X extends CodedException> Function<Exception, X> createMapper(final ErrorCode code, final String[] line) {
    return new Function<Exception, X>() {
      public X apply(Exception clause) {
        clause.printStackTrace();
        return (X) new PhraseExecutionException(code, line, clause);
      } 
    };
  }

  private ParallelStrategy(ConclusionFunction<Callable<T>, CheckedFuture<T, E>> function) {
    this.function = function;
  }

  private ConclusionFunction<Callable<T>, CheckedFuture<T, E>> function() {
    return function;
  }

  public <B> ConclusionFunction<B, CheckedFuture<T, E>> lift(final ConclusionFunction<B, T> function) {
    final ParallelStrategy<T, E> self = this;
    return new ConclusionFunction<B, CheckedFuture<T, E>>() {
      public CheckedFuture<T, E> apply(final B b) {
        return self.function().apply(new Callable<T>() {
          public T call() {
            return function.apply(b);
          }
        });
      }
    };
  }

  /**
   * @param function which implement concrete thread strategy
   * @return instance {@code ParallelStrategy} with has function parameter as delegate
   */
  private static <T, X extends Exception> ParallelStrategy<T, X> strategy(ConclusionFunction<Callable<T>, CheckedFuture<T, X>> function) {
    return new ParallelStrategy<T, X>(function);
  }

  /**
   * @return ParallelStrategy instance which create single thread for  on every call
   */
  public static <T, X extends CodedException> ParallelStrategy<T, X> separateThreadStrategy() {
    return strategy(new ConclusionFunction<Callable<T>, CheckedFuture<T, X>>() {
      @Override
      public CheckedFuture<T, X> apply(Callable<T> input) {
        final ListenableFutureTask<T> task = ListenableFutureTask.create(input);
        new Thread(task).start();
        Function<Exception, X> mapper = ParallelStrategy.createMapper(PhraseErrorCode.ERROR, new String[] {"singleThreadStrategy"});
        return Futures.makeChecked(task, mapper);
      }
    });
  }

  /**
   * 
   * @return ParallelStrategy<T, E> pStrategy
   */
  public static <T, X extends CodedException> ParallelStrategy<T, X> serial() {
    return strategy(new ConclusionFunction<Callable<T>, CheckedFuture<T, X>>() {
      private final ListeningExecutorService ex = MoreExecutors.sameThreadExecutor();
      @Override
      public CheckedFuture<T, X> apply(Callable<T> input) {
        Function<Exception, X> mapper = createMapper(PhraseErrorCode.ERROR, new String[] {"sameThreadStrategy"});
        return Futures.makeChecked(ex.submit(input), mapper);
      }
    });
  }

  /**
   * @return ParallelStrategy<T, E> pStrategy
   */
  public static <T, X extends CodedException> ParallelStrategy<T, X> listenableFutureStrategy(final ListeningExecutorService es) {
    return strategy(new ConclusionFunction<Callable<T>,  CheckedFuture<T, X>>() {
      @Override
      public CheckedFuture<T, X> apply(Callable<T> task) {
        Function<Exception, X> mapper = createMapper(PhraseErrorCode.ERROR, new String[] {"listenableFutureStrategy"});
        return Futures.makeChecked(es.submit(task), mapper);
      }
    });
  }

  /**
   * 
   * @return
   */
  public static <T, X extends CodedException> ParallelStrategy<T, X> defaultListenableFutureStrategy() {
    return strategy(new ConclusionFunction<Callable<T>,  CheckedFuture<T, X>>() {
      @Override
      public CheckedFuture<T, X> apply(Callable<T> task) {
        Function<Exception, X> mapper = createMapper(PhraseErrorCode.ERROR, new String[] {"listenableFutureStrategy"});
        return Futures.makeChecked(COMPUTATION_EXECUTOR.submit(task), mapper);
      }
    });
  }

  private static ListeningExecutorService createComputationExecutor() {
    int cores = Runtime.getRuntime().availableProcessors();
    return MoreExecutors.listeningDecorator(
      Executors.newScheduledThreadPool(cores, new ThreadFactory() {
      final AtomicInteger counter = new AtomicInteger();

      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "FcComputationThreadPool-" + counter.incrementAndGet());
        t.setDaemon(true);
        return t;
      }
    }));
  }
}

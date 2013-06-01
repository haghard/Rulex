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
package ru.rulex.conclusion;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import org.junit.Test;
import ru.rulex.conclusion.execution.Callables;
import ru.rulex.conclusion.execution.ParallelStrategy;
import ru.rulex.conclusion.ConclusionFunction;
import ru.rulex.conclusion.FluentConclusionPredicate;
import ru.rulex.conclusion.PhraseExecutionException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.rulex.conclusion.FluentConclusionPredicate.*;
import static ru.rulex.conclusion.RulexMatchersDsl.*;

public class TestParallelStrategyExecution
{

  private final ListeningExecutorService service = MoreExecutors.listeningDecorator( Executors
      .newFixedThreadPool( Runtime.getRuntime().availableProcessors() / 2 ) );

  private interface FutureMerger<T>
  {
    ListenableFuture<List<T>> merged( List<ListenableFuture<T>> futures );
  }

  FutureMerger<Boolean> allMerger = new FutureMerger<Boolean>()
  {
    @Override
    public ListenableFuture<List<Boolean>> merged( List<ListenableFuture<Boolean>> futures )
    {
      return Futures.allAsList( futures );
    }
  };

  FutureMerger<Boolean> successMerger = new FutureMerger<Boolean>()
  {
    @Override
    public ListenableFuture<List<Boolean>> merged( List<ListenableFuture<Boolean>> futures )
    {
      return Futures.successfulAsList( futures );
    }
  };

  @Test
  public void testBind() throws Exception
  {
    Model en = Model.values( 40 );
    assertTrue( "testBind error !!!",
        Callables.bind( Callables.unit( en ), new ConclusionFunction<Model, Callable<Boolean>>()
        {
          @Override
          public Callable<Boolean> apply( final Model argument )
          {
            return new Callable<Boolean>()
            {
              String accessor = Model.INT_ACCESSOR;
              FluentConclusionPredicate<?> fluent = fluent();

              @Override
              public Boolean call()
              {
                return fluent.eq( argument( 23 ), descriptor( Model.class, accessor ) )
                    .and( fluent.eq( argument( 39 ), descriptor( Model.class, accessor ) ) )
                    .and( fluent.eq( argument( 39 ), descriptor( Model.class, accessor ) ) )
                    .or( fluent.eq( argument( 40 ), descriptor( Model.class, accessor ) ) )
                    .apply( argument );
              }
            };
          }
        } ).call() );
  }

  @Test
  public void testBlockToGetResult() throws Exception
  {
    // explicitly block execution thread
    assertTrue( "testBlockToGetResult 1 error !!!",
        ParallelStrategy.<Boolean, PhraseExecutionException> listenableFutureStrategy( service )
            .lift( new ConclusionFunction<Integer, Boolean>()
            {
              @Override
              public Boolean apply( Integer argument )
              {
                return always().apply( argument );
              }
            } ).apply( 2 ).get() );

    // implicitly block execution thread
    assertTrue(
        "testBlockToGetResult 2 error !!!",
        Callables.obtain(
            ParallelStrategy.<Boolean, PhraseExecutionException> listenableFutureStrategy( service )
                .lift( new ConclusionFunction<Integer, Boolean>()
                {
                  @Override
                  public Boolean apply( Integer argument )
                  {
                    return always().apply( argument );
                  }
                } ).apply( 2 ) ).call() );
  }

  @Test
  public void testFmap() throws Exception
  {
    Model en = Model.values( 40 );
    assertTrue( "testFmap error !!!", Callables.fmap( new ConclusionFunction<Model, Boolean>()
    {
      @Override
      public Boolean apply( Model argument )
      {
        return always().apply( argument );
      }
    } ).apply( Callables.unit( en ) ).call() );
  }

  @Test
  public void testListenableFutureWithParallelStrategy() throws InterruptedException
  {
    Model en = Model.values( 40 );
    final CountDownLatch latch = new CountDownLatch( 1 );
    FutureCallback<List<Boolean>> callback = new FutureCallback<List<Boolean>>()
    {
      @Override
      public void onSuccess( List<Boolean> result )
      {
        latch.countDown();
      }

      @Override
      public void onFailure( Throwable t )
      {
        fail( "testWithParallelStrategy error" );
      }
    };

    ParallelStrategy<Boolean, PhraseExecutionException> pStrategy = ParallelStrategy
        .listenableFutureStrategy( service );

    ImmutableList.Builder<ListenableFuture<Boolean>> blist = ImmutableList.builder();

    blist.add( pStrategy.lift( new ConclusionFunction<Model, Boolean>()
    {
      @Override
      public Boolean apply( Model argument )
      {
        String accessor = Model.INT_ACCESSOR;
        return fluent().eq( argument( 39 ), descriptor( Model.class, accessor ) )
            .and( fluent().eq( argument( 39 ), descriptor( Model.class, accessor ) ) )
            .and( fluent().eq( argument( 39 ), descriptor( Model.class, accessor ) ) )
            .or( fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) ) )
            .apply( argument );
      }
    } ).apply( en ) );

    blist.add( pStrategy.lift( new ConclusionFunction<Model, Boolean>()
    {
      @Override
      public Boolean apply( Model argument )
      {
        return true;
      }
    } ).apply( en ) );

    Futures.addCallback( allMerger.merged( blist.build() ), callback );

    assertTrue( "testWithParallelStrategy error !!!", latch.await( 5, TimeUnit.SECONDS ) );
  }

  /**
   * with obtain we implicitly block execution thread on {@code future.get()}
   * 
   * @throws Exception
   */
  @Test
  public void testObtainWithException() throws Exception
  {
    Model en = Model.values( 40 );
    ParallelStrategy<Boolean, PhraseExecutionException> pStrategy = ParallelStrategy
        .listenableFutureStrategy( service );
    try
    {
      Callables.bind( Callables.obtain( pStrategy.lift( new ConclusionFunction<Model, Boolean>()
      {
        @Override
        public Boolean apply( Model argument )
        {
          throw new IllegalArgumentException( "expected" );
        }
      } ).apply( en ) ), new ConclusionFunction<Boolean, Callable<Integer>>()
      {
        @Override
        public Callable<Integer> apply( Boolean argument )
        {
          // post action, executed in main thread after function completion
          // 0 could be possitive result marker
          return Callables.unit( 0 );
        }
      } ).call();
    }
    catch (PhraseExecutionException e)
    {
      e.printStackTrace();
      // expected
    }
    catch (Exception ex)
    {
      fail( "testObtainWithException error !!!" );
    }
  }
}

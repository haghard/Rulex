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

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ru.rulex.conclusion.execution.ParallelStrategy;
import ru.rulex.conclusion.ConclusionPhrase;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.PhraseExecutionException;
import ru.rulex.conclusion.PhraseSettings;
import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.ParserBuilders.Consequence;
import ru.rulex.conclusion.ParserBuilders.ConsequenceSupplier;
import ru.rulex.conclusion.PhraseBuildersFacade.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.rulex.conclusion.FluentConclusionPredicate.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.conclusion.RulexMatchersDsl.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import static org.fest.assertions.api.Assertions.*;

public class TestEventOrientedPhraseBuilders {

  private static final Logger logger = Logger.getLogger(TestEventOrientedPhraseBuilders.class);

  // TO DO: mock for this case, parallel strategy case
  // TO DO: replace anonymous predicate usage with RulexMatchersDsl methods
  @Test
  public void testEventOrientedPhrasesBuilderWithProxy() {
    final AbstractEventOrientedPhrasesBuilder builder0 = new EventOrientedPhrasesBuilder() {
      protected void build() {
        through(Model.class, "fact: [getInteger() == 211]").shouldMatch(
            query(callOn(Model.class).getInteger(), eq(211), Model.class));
      }
    };
    try {
      assertThat(builder0.async(Model.values(211)).checkedGet(1, TimeUnit.SECONDS)).isTrue().as(
          "testEventOrientedPhrasesBuilderWithProxy error !!!");
    } catch (Exception ex) {
      fail("testEventOrientedPhrasesBuilderWithProxy error !!!");
    }
  }

  /**
   * Test {@code EventOrientedPhrasesBuilder} with
   * {@code TypeSafeSelectorPredicate<T, E>}
   */
  // Work very slow 3 time slow
  @Test
  public void testEventOrientedPhrasesBuilderWithTypeSafeSelector() {
    final AbstractEventOrientedPhrasesBuilder builder = new EventOrientedPhrasesBuilder() {
      @Override
      protected void build() {
        through(Model.class, "fact: [getInteger() == 11]").shouldMatch(
            typeSafeQuery(number(Model.class, Integer.class, Model.INT_ACCESSOR), eq(11)));
      }
    };

    try {
      assertThat(builder.async(Model.values(11)).checkedGet(1, TimeUnit.SECONDS)).isTrue().as(
          "testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!");

      assertThat(builder.async(Model.values(12)).checkedGet(1, TimeUnit.SECONDS)).isFalse().as(
          "testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!");

      assertThat(builder.async(Model.values(11)).checkedGet(1, TimeUnit.SECONDS)).isTrue().as(
          "testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!");
    } catch (Exception ex) {
      fail("testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!");
    }
  }

  /**
   * Test {@code EventOrientedPhrasesBuilder} with
   * {@code SelectorPredicate<T, E>}
   */
  @Test
  public void testEventOrientedPhrasesBuilderWithSelector() {
    final Selector<Model, Integer> selector = new Selector<Model, Integer>() {
      public Integer select(Model input) {
        return input.getInteger();
      }
    };

    final AbstractEventOrientedPhrasesBuilder builder = new EventOrientedPhrasesBuilder() {
      protected void build() {
        through(Model.class, "fact: [field:getInteger() == 10]").shouldMatch(
            query(selector, eq(10)));
      }
    };

    try {
      assertThat(builder.async(Model.values(10)).checkedGet(1, TimeUnit.SECONDS)).isTrue().as(
          "testEventOrientedPhrasesBuilderWithSelector error !!!");
    } catch (Exception ex) {
      fail("testEventOrientedPhrasesBuilderWithSelector error !!!");
    }
  }

  /**
   * test {@code EventOrientedPhrasesBuilder} with different kind of
   * {@code ParallelStrategy}
   */
  @Test
  public void testEventOrientedPhrasesBuilderWithParallelStrategy() {
    final Thread mainThread = Thread.currentThread();
    final ParallelStrategy<Boolean, PhraseExecutionException> separateThreadStrategy = ParallelStrategy
        .separateThreadStrategy();

    final Selector<Model, Integer> selector = createSameThMockSelector(mainThread);
    final ConclusionPredicate<Integer> predicate = createSameThMockPredicate(mainThread);

    AbstractEventOrientedPhrasesBuilder sameThreadBuilder = new EventOrientedPhrasesBuilder() {
      protected void build() {
        through(Model.class, "fact: [field:getInteger() == 10]").shouldMatch(
            query(selector, predicate));
      }
    };

    final Selector<Model, Integer> selector0 = createSeparateThMockSelector(mainThread);
    final ConclusionPredicate<Integer> predicate0 = createSeparateThMockPredicate(mainThread);

    AbstractEventOrientedPhrasesBuilder separateThreadBuilder = new EventOrientedPhrasesBuilder() {
      protected void build() {
        rule(separateThreadStrategy, Model.class, "fact: [field:getInteger() == 10]").shouldMatch(
            query(selector0, predicate0));
      }
    };

    assertThat(sameThreadBuilder.sync(Model.values(10))).isTrue();
    assertThat(separateThreadBuilder.sync(Model.values(10))).isTrue();
  }

  /**
   * test {@code SingleEventFactConsequenceExecutionPhrasesBuilder} with
   * {@code SelectorPredicate<T, E>}
   */
  @Test
  public void testEventOrientedFactConsequencePhrasesBuilderWithParallelStrategy() {
    final Thread mainThread = Thread.currentThread();
    final ParallelStrategy<Boolean, PhraseExecutionException> separateThreadStrategy = ParallelStrategy
        .separateThreadStrategy();

    final Selector<Model, Integer> selector = createSeparateThMockSelector(mainThread);

    final ConclusionPredicate<Integer> lambda = createSeparateThMockPredicate(mainThread);

    final ConsequenceSupplier cSupplier = new ConsequenceSupplier() {
      public Consequence get() {
        return new Consequence() {
          public void action() {
            assertThat(mainThread).isNotSameAs(Thread.currentThread()).as(
                "This Consequence call should not happens in main thread, but it is !!!");
            logger.debug("consequence:consequence");
          }
        };
      }
    };

    final AbstractEventOrientedPhrasesBuilder sameThreadBuilder = new EventOrientedFactConsequencePhrasesBuilder() {
      @Override
      protected void build() {
        through(separateThreadStrategy, Model.class,
            "fact: [field:getInt() < 100] : consequence:consequence").fact(
            query(selector(selector), lambda(lambda))).consequence(cSupplier);
      }
    };
    try {
      CheckedFuture<Boolean, PhraseExecutionException> future = sameThreadBuilder.async(Model
          .values(10));
      assertThat(future.checkedGet()).isTrue().as(
          "testEventOrientedFactConsequencePhrasesBuilderWithParallelStrategy error !!!");
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("testEventOrientedFactConsequencePhrasesBuilderWithParallelStrategy error ex!!!"
          + ex.getMessage());
    }
  }

  /**
   * test EventOrientedFactConsequencePhrasesBuilder which throws
   * {@code throw new RuntimeException} and catched in catch block as a
   * {@code PhraseExecutionException} exception.
   */
  @Test
  public void testSingleEventFactConsequenceExecutionPhrasesBuilderWithException() {
    final Thread mainThread = Thread.currentThread();
    final ParallelStrategy<Boolean, PhraseExecutionException> builderStrategy = ParallelStrategy
        .separateThreadStrategy();

    AbstractEventOrientedPhrasesBuilder builder = new EventOrientedFactConsequencePhrasesBuilder() {
      @Override
      protected void build() {
        through(builderStrategy, Model.class,
            "fact: [field:getInt() < 100] : consequence:consequence").fact(
            query(selector(new Selector<Model, Integer>() {
              public Integer select(Model input) {
                assertThat(mainThread).isNotSameAs(Thread.currentThread()).as(
                    "This Selector call should not happens in main thread, but it is !!!");
                throw new RuntimeException("ExpectedRuntimeException");
              }
            }), lambda(new ConclusionPredicate<Integer>() {
              public boolean apply(Integer argument) {
                assertThat(mainThread).isNotSameAs(Thread.currentThread()).as(
                    "This Predicate call should not happens in main thread, but it is !!!");
                return argument < 100;
              }
            }))).consequence(new ConsequenceSupplier() {
          public Consequence get() {
            return new Consequence() {
              public void action() {
                assertThat(mainThread).isNotSameAs(Thread.currentThread()).as(
                    "This Consequence call should not happens in main thread, but it is !!!");
                logger.debug("consequence:consequence");
              }
            };
          }
        });
      }
    };
    try {
      CheckedFuture<Boolean, PhraseExecutionException> future = builder.async(Model.values(10));
      future.checkedGet();
    } catch (PhraseExecutionException e) {
      // expected
      e.printStackTrace();
    } catch (Exception ex) {
      fail("testSingleEventFactConsequenceExecutionPhrasesBuilderWithException error ex!!!"
          + ex.getMessage());
    }
  }

  /**
   * 
   * 
   */
  @Test
  public void testSingleEventValidationExecutionPhrasesWithSettings() {
    final String methodName = Model.INT_ACCESSOR;
    ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors
        .newFixedThreadPool(2));
    final ParallelStrategy<Boolean, PhraseExecutionException> pStrategy = ParallelStrategy
        .listenableFutureStrategy(executor);
    try {
      AbstractEventOrientedPhrasesBuilder nbuilder = ConclusionPhrase.phrase(
          "default-phrase",
          PhraseSettings.<Integer, Model> newBuilder().accessorName(methodName)
              .pStrategy(pStrategy).targetClass(Model.class).value(11)
              .predicate(new ConclusionPredicate<Integer>() {
                @Override
                public boolean apply(Integer argument) {
                  return argument == 11;
                }
              }).build());
      List<CheckedFuture<Boolean, ?>> results = new ArrayList<CheckedFuture<Boolean, ?>>();
      for (int i = 0; i < 50; i++) {
        results.add(nbuilder.async(Model.values(11)));
      }

      for (CheckedFuture<Boolean, ?> result : results)
        result.checkedGet();

    } catch (Exception e) {
      fail("testSingleEventValidationExecutionPhrasesWithSettings error " + e.getMessage());
    }
  }

  private ConclusionPredicate<Integer> createSameThMockPredicate(final Thread mainThread) {
    final ConclusionPredicate<Integer> predicate = mock(ConclusionPredicate.class);
    when(predicate.apply(any(Integer.class))).then(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        assertThat(mainThread).isEqualTo(Thread.currentThread()).as(
            "This Predicate call should happens in main thread, but is not !!!");
        return invocation.getArguments()[0].equals(10);
      }
    });
    return predicate;
  }

  private ConclusionPredicate<Integer> createSeparateThMockPredicate(final Thread mainThread) {
    final ConclusionPredicate<Integer> predicate = mock(ConclusionPredicate.class);
    when(predicate.apply(any(Integer.class))).then(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        assertThat(mainThread).isNotSameAs(Thread.currentThread()).as(
            "This Predicate call should happens in separate thread, but is not !!!");
        return invocation.getArguments()[0].equals(10);
      }
    });
    return predicate;
  }

  private Selector<Model, Integer> createSameThMockSelector(final Thread mainThread) {
    final Selector<Model, Integer> selector = mock(Selector.class);
    when(selector.select(any(Model.class))).thenAnswer(new Answer<Integer>() {
      @Override
      public Integer answer(InvocationOnMock invocation) throws Throwable {
        assertThat(mainThread).isEqualTo(Thread.currentThread()).as(
            "This selector call should happens in main thread, but is not !!!");
        return ((Model) invocation.getArguments()[0]).getInteger();
      }
    });
    return selector;
  }

  private Selector<Model, Integer> createSeparateThMockSelector(final Thread mainThread) {
    final Selector<Model, Integer> selector = mock(Selector.class);
    when(selector.select(any(Model.class))).thenAnswer(new Answer<Integer>() {
      @Override
      public Integer answer(InvocationOnMock invocation) throws Throwable {
        assertThat(mainThread).isNotSameAs(Thread.currentThread()).as(
            "This selector call should not happens in main thread, but it is !!!");
        return ((Model) invocation.getArguments()[0]).getInteger();
      }
    });
    return selector;
  }
}

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
import static org.junit.Assert.*;
import static ru.rulex.conclusion.FluentConclusionPredicate.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class TestEventOrientedPhraseBuilders {

  private static final Logger logger = Logger
      .getLogger(TestEventOrientedPhraseBuilders.class);

  //TO DO: mock for this case, parallel strategy case
  @Test
  public void testEventOrientedPhrasesBuilderWithProxy() {
    final ConclusionPredicate<Integer> intPredicate = new ConclusionPredicate<Integer>() {
      @Override public boolean apply(Integer argument) { return argument == 11; }
    };

    final AbstractEventOrientedPhrasesBuilder builder = new EventOrientedPhrasesBuilder() {
      protected void build() {
        through(Model.class, "fact: class Model [field-selector, field:getInt() == 10]")
          .shouldMatch(
            query(
              callOn(Model.class).getInteger(), lambda(intPredicate), Model.class));
      }
    };
    try {
      assertTrue("testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!",
          builder.async(Model.values(11)).checkedGet(1, TimeUnit.SECONDS));
    } catch (Exception ex) {
      fail("testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!");
    }
  }

  /**
   * Test {@code EventOrientedPhrasesBuilder} with 
   * {@code TypeSafeSelectorPredicate<T, E>} 
   */
  @Test
  public void testEventOrientedPhrasesBuilderWithTypeSafeSelector() {
    final ConclusionPredicate<Integer> intPredicate = new ConclusionPredicate<Integer>() {
      @Override
      public boolean apply(Integer argument) {
        return argument == 11;
      }
    };

    final AbstractEventOrientedPhrasesBuilder builder = new EventOrientedPhrasesBuilder() {
      @Override
      protected void build() {
        through(Model.class, "fact: class Entity [field:getInt() == 11]")
            .shouldMatch(
              typeSafeQuery(
                number(Model.class, Integer.class, Model.INT_ACCESSOR), intPredicate));
      }
    };

    try {
      assertTrue("testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!",
          builder.async(Model.values(11)).checkedGet(1, TimeUnit.SECONDS));

      assertFalse("testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!",
          builder.async(Model.values(12)).checkedGet(1, TimeUnit.SECONDS));

      assertTrue("testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!",
          builder.async(Model.values(11)).checkedGet(1, TimeUnit.SECONDS));

    } catch (Exception ex) {
      fail("testEventOrientedPhrasesBuilderWithTypeSafeSelector error !!!");
    }
  }
  
  /**
   * 
   * Test {@code EventOrientedPhrasesBuilder} with 
   * {@code SelectorPredicate<T, E>}
   *
   */
  @Test
  public void testEventOrientedPhrasesBuilderWithSelector() {
    final Selector<Model, Integer> selector = new Selector<Model, Integer>() {
      public Integer select(Model input) {
        return input.getInteger();
      }
    };
    
    final ConclusionPredicate<Integer> lambda = new ConclusionPredicate<Integer>() {
      public boolean apply(Integer argument) {
        return argument == 10;
      }
    };

    final AbstractEventOrientedPhrasesBuilder builder = new EventOrientedPhrasesBuilder() {
      protected void build() {
        through(Model.class, "fact: class Entity [field-selector, field:getInt() == 10]")
          .shouldMatch(
              query(selector(selector), lambda(lambda)));
      }
    };

    try {
      assertEquals("testEventOrientedPhrasesBuilderWithSelector error !!!",
          Boolean.TRUE, builder.async(Model.values(10)).checkedGet(1, TimeUnit.SECONDS));
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
    final ParallelStrategy<Boolean, PhraseExecutionException> separateThreadStrategy =
        ParallelStrategy.separateThreadStrategy();

    AbstractEventOrientedPhrasesBuilder sameThreadBuilder =
        new EventOrientedPhrasesBuilder() {
          protected void build() {
            through(Model.class, "fact: class Entity [field-selector, field:getInt() == 10]")
                .shouldMatch(query(selector(new Selector<Model, Integer>() {
                  public Integer select(Model input) {
                    assertSame("This selector call should happens in main thread, but is not !!!",
                        mainThread, Thread.currentThread());
                    return input.getInteger();
                  }
                }), lambda(new ConclusionPredicate<Integer>() {
                  public boolean apply(Integer argument) {
                    assertSame("This Predicate call should happens in main thread, but is not !!!",
                        mainThread, Thread.currentThread());
                    return argument == 10;
                  }
                })));
          }
        };

    AbstractEventOrientedPhrasesBuilder separateThreadBuilder =
        new EventOrientedPhrasesBuilder() {
          protected void build() {
            rule(separateThreadStrategy, Model.class, "fact: class Entity [field-selector, field:getInt() == 10]")
                .shouldMatch(query(selector(new Selector<Model, Integer>() {
                  public Integer select(Model input) {
                    assertNotSame("This selector call should not happens in main thread, but it is !!!", 
                        mainThread, Thread.currentThread());
                    return input.getInteger();
                  }
                }), lambda(new ConclusionPredicate<Integer>() {
                  public boolean apply(Integer argument) {
                    assertNotSame("This selector call should not happens in main thread, but it is !!!",
                        mainThread, Thread.currentThread());
                    return argument == 10;
                  }
                })));
          }
        };
        
     assertTrue("testEventOrientedPhrasesBuilderWithParallelStrategy sameThread error !!!",
       sameThreadBuilder.sync(Model.values(10)));
     assertTrue("testEventOrientedPhrasesBuilderWithParallelStrategy separateThread error !!!",
       separateThreadBuilder.sync(Model.values(10)));
  }
  /**
   * test {@code SingleEventFactConsequenceExecutionPhrasesBuilder} 
   * with {@code SelectorPredicate<T, E>}
   */
  @Test
  public void testEventOrientedFactConsequencePhrasesBuilderWithParallelStrategy() {
    final Thread mainThread = Thread.currentThread();
    final ParallelStrategy<Boolean, PhraseExecutionException> separateThreadStrategy =
        ParallelStrategy.separateThreadStrategy();
    
    final Selector<Model, Integer> selector = new Selector<Model, Integer>() {
      public Integer select(Model input) {
        assertNotSame("This selector call should not happens in main thread, but it is !!!",
            mainThread, Thread.currentThread());
        return input.getInteger();
      }
    };
    
    final ConclusionPredicate<Integer> lambda = new ConclusionPredicate<Integer>() {
      public boolean apply(Integer argument) {
        assertNotSame("This selector call should not happens in main thread, but it is !!!",
            mainThread, Thread.currentThread());
        return argument < 100;
      }
    };
    
    final ConsequenceSupplier cSupplier = new ConsequenceSupplier() {
      public Consequence get() {
        return new Consequence() {
          public void action() {
            assertNotSame("This Consequence call should not happens in main thread, but it is !!!",
                mainThread, Thread.currentThread());
            logger.debug("consequence:consequence");
          }
        };
      }
    };

    final AbstractEventOrientedPhrasesBuilder sameThreadBuilder = 
                      new EventOrientedFactConsequencePhrasesBuilder() {
      @Override
      protected void build() {
        through(separateThreadStrategy, Model.class, "fact: class Entity [field-selector, field:getInt() < 100] : consequence:consequence")
            .fact(query(selector(selector), lambda(lambda)))
              .consequence(cSupplier);
      }
    };
    try {
      CheckedFuture<Boolean, PhraseExecutionException> future = sameThreadBuilder.async(Model.values(10));
      assertTrue("testEventOrientedFactConsequencePhrasesBuilderWithParallelStrategy error !!!", 
          future.checkedGet());
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("testEventOrientedFactConsequencePhrasesBuilderWithParallelStrategy error ex!!!" + ex.getMessage());
    }
  }
  
  /**
   * test EventOrientedFactConsequencePhrasesBuilder
   * which throws {@code throw new RuntimeException}
   * and catched in catch block as a {@code PhraseExecutionException} 
   * exception.
   * 
   */
  @Test
  public void testSingleEventFactConsequenceExecutionPhrasesBuilderWithException() {
    final Thread mainThread = Thread.currentThread();
    final ParallelStrategy<Boolean, PhraseExecutionException> builderStrategy = 
        ParallelStrategy.separateThreadStrategy();
    
    AbstractEventOrientedPhrasesBuilder builder = 
                new EventOrientedFactConsequencePhrasesBuilder() {
      @Override
      protected void build() {
        through(builderStrategy, Model.class, 
            "fact: class Entity [field-selector, field:getInt() < 100] : consequence:consequence")
            .fact(
                query(selector(new Selector<Model, Integer>() {
                  public Integer select(Model input) {
                    assertNotSame("This Selector call should not happens in main thread, but it is !!!",
                        mainThread, Thread.currentThread());
                    throw new RuntimeException("ExpectedRuntimeException");
                  }
                }),lambda(new ConclusionPredicate<Integer>() {
                  public boolean apply(Integer argument) {
                    assertNotSame("This Predicate call should not happens in main thread, but it is !!!",
                        mainThread, Thread.currentThread());
                    return argument < 100;
                  }
                })))
            .consequence(new ConsequenceSupplier() {
                public Consequence get() {
                  return new Consequence() {
                    public void action() {
                      assertNotSame("This Consequence call should not happens in main thread, but it is !!!",
                          mainThread, Thread.currentThread());
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
      //expected
      e.printStackTrace();
    } catch (Exception ex) {
      fail("testSingleEventFactConsequenceExecutionPhrasesBuilderWithException error ex!!!" + ex.getMessage());
    }
  }
  
  /**
   * 
   * 
   */
  @Test
  public void testSingleEventValidationExecutionPhrasesWithSettings() {
      final String methodName = Model.INT_ACCESSOR;
      ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));  
      final ParallelStrategy<Boolean, PhraseExecutionException> pStrategy = 
          ParallelStrategy.listenableFutureStrategy(executor);
      try {
         AbstractEventOrientedPhrasesBuilder nbuilder = ConclusionPhrase.phrase("default-phrase", 
             PhraseSettings.<Integer, Model>newBuilder()
                .accessorName(methodName)
                .pStrategy(pStrategy)
                .targetClass(Model.class)
                .value(11)
                .predicate(new ConclusionPredicate<Integer>() {
                  @Override public boolean apply(Integer argument) {
                    return argument == 11;
                  }
                }).build());
        List<CheckedFuture<Boolean, ?>> results = new ArrayList<CheckedFuture<Boolean, ?>>();
        for (int i = 0; i < 50; i++) {
          results.add(nbuilder.async(Model.values(11)));
        }

        for (CheckedFuture<Boolean,?> result: results)
          result.checkedGet();
        
      } catch (Exception e) {
        fail("testSingleEventValidationExecutionPhrasesWithSettings error " + e.getMessage());
      }
  }
}
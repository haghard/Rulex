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

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.rulex.conclusion.FluentConclusionPredicate.*;

import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import ru.rulex.conclusion.delegate.Delegate;
import ru.rulex.conclusion.delegate.DelegateFactory;
import ru.rulex.conclusion.execution.ParallelStrategy;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.FluentConclusionPredicate;
import ru.rulex.conclusion.PhraseExecutionException;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractIterableOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.ImperativeIterableOrientedPhrasesBuilder;
import static ru.rulex.conclusion.RulexMatchersDsl.*;

public class TestImperativeIterableOrientedPhrasesBuilder
{

  private final ImmutableList<Model> list = ImmutableList.of(Model.values(121), Model.values(122));

  @Test
  @SuppressWarnings ( "unchecked")
  public void testImperativeExecutionPhrasesBuilderWithMock ()
  {
    final Delegate<Integer> delegateMock = Mockito.mock(Delegate.class);
    final StringBuilder joiner = new StringBuilder();

    when(delegateMock.execute(Mockito.<Integer> anyVararg())).thenAnswer(new Answer<Object>()
    {
      @Override
      public Object answer ( InvocationOnMock invocationOnMock ) throws Throwable
      {
        Object[] arguments = invocationOnMock.getArguments();
        for ( Object arg : arguments )
        {
          joiner.append(arg + ",");
        }
        return null;
      }
    });

    AbstractIterableOrientedPhrasesBuilder builder = new ImperativeIterableOrientedPhrasesBuilder()
    {
      @Override
      protected void build ()
      {
        through(Model.class, Integer.class, "testImperativeExecutionPhrasesBuilderWithMock")
            .lambda(DelegateFactory.from(1, 7, 13)).delegate(delegateMock);
      }
    };
    try
    {
      builder.sync(list);
      verify(delegateMock, times(1)).execute(Mockito.<Integer> anyVararg());
      assertEquals("check arguments error !!!", "1,7,13,", joiner.toString());
    } catch ( Exception ex )
    {
      fail("testAlgorithmCollectionBuilderWithMock error ex!!!");
    }
  }

  @Test
  public void testImperativeExecutionPhrasesBuilderWithSpy ()
  {
    final StringBuilder joiner = new StringBuilder();

    final Delegate<Integer> filter = new Delegate<Integer>()
    {
      @Override
      public void setContent ( Iterable<?> collection )
      {
      }

      @Override
      public boolean execute ( Integer... arguments )
      {
        for ( Integer val : arguments )
        {
          joiner.append(val + ",");
        }

        return true;
      }
    };
    final Delegate<Integer> filterSpy = spy(filter);

    AbstractIterableOrientedPhrasesBuilder builder = new ImperativeIterableOrientedPhrasesBuilder()
    {
      @Override
      protected void build ()
      {
        through(Model.class, Integer.class, "testImperativeExecutionPhrasesBuilderWithSpy").lambda(
            DelegateFactory.from(1, 7, 13)).delegate(filterSpy);
      }
    };
    try
    {
      builder.async(list).checkedGet();
      verify(filterSpy, Mockito.times(1)).execute(Mockito.<Integer> anyVararg());
      verify(filterSpy, Mockito.timeout(1)).setContent(Mockito.anyCollection());
      assertEquals("check arguments error !!!", "1,7,13,", joiner.toString());
    } catch ( Exception ex )
    {
      fail("testImperativeExecutionPhrasesBuilderWithSpy error ex!!!");
    }
  }

  @Test
  @SuppressWarnings ( "rawtypes")
  public void testSimpleImperativePhrasesBuilder ()
  {
    final Thread currentThread = Thread.currentThread();
    final ConclusionPredicate<Delegate<FluentConclusionPredicate>> compositionPredicate = DelegateFactory
        .<FluentConclusionPredicate> from(FluentConclusionPredicate.never());

    final Delegate<FluentConclusionPredicate> targetDelegate = new Delegate<FluentConclusionPredicate>()
    {
      @Override
      public void setContent ( Iterable collection )
      {
      }

      // return false since we never()
      @Override
      @SuppressWarnings ( "unchecked")
      public boolean execute ( FluentConclusionPredicate... arguments )
      {
        assertSame("testSimpleImperativePhrasesBuilder thread error ", Thread.currentThread(),
            currentThread);
        return Optional.fromNullable(arguments[0]).or(always()).apply(1);
      }
    };

    AbstractIterableOrientedPhrasesBuilder builder = new ImperativeIterableOrientedPhrasesBuilder()
    {
      @Override
      protected void build ()
      {
        through(Model.class, FluentConclusionPredicate.class, "never()").lambda(
            compositionPredicate).delegate(targetDelegate);
      }
    };

    try
    {
      assertFalse("testSimpleImperativePhrasesBuilder error !!!", builder.async(list).checkedGet());
    } catch ( Exception ex )
    {
      fail("testSimpleImperativePhrasesBuilder error ex!!!");
    }
  }

  @Test
  @SuppressWarnings ( "rawtypes")
  public void testComplexImperativePhrasesBuilder ()
  {
    // less 123 and eq 122
    final ConclusionPredicate<Delegate<FluentConclusionPredicate>> fluentPredicates = DelegateFactory
        .<FluentConclusionPredicate> from(fluent().less(argument(123),
            descriptor(Model.class, Model.INT_ACCESSOR)).and(
            fluent().eq(argument(122), descriptor(Model.class, Model.INT_ACCESSOR))));

    final Delegate<FluentConclusionPredicate> targetDelegate = new Delegate<FluentConclusionPredicate>()
    {
      Collection<Object> collection;

      @Override
      public void setContent ( Iterable<?> collection )
      {
        this.collection = ImmutableList.copyOf(collection);
      }

      @Override
      @SuppressWarnings ( "unchecked")
      public boolean execute ( FluentConclusionPredicate... fluentPredicate )
      {
        Preconditions.checkArgument(fluentPredicate[0] != null, "null FluentConclusionPredicate");
        for ( Object object : collection )
        {
          if ( fluentPredicate[0].apply(object) )
          {
            return true;
          }
        }
        return false;
      }
    };

    AbstractIterableOrientedPhrasesBuilder builder2 = new ImperativeIterableOrientedPhrasesBuilder()
    {
      @Override
      protected void build ()
      {
        through(Model.class, FluentConclusionPredicate.class, "testComplexImperativePhrasesBuilder")
            .lambda(fluentPredicates).delegate(targetDelegate);
      }
    };

    // eq 123 or eq 124
    final ConclusionPredicate<Delegate<FluentConclusionPredicate>> fluentPredicates0 = DelegateFactory
        .<FluentConclusionPredicate> from(fluent().eq(argument(123),
            descriptor(Model.class, Model.INT_ACCESSOR)).or(
            fluent().eq(argument(124), descriptor(Model.class, Model.INT_ACCESSOR))));

    AbstractIterableOrientedPhrasesBuilder builder3 = new ImperativeIterableOrientedPhrasesBuilder()
    {
      @Override
      protected void build ()
      {
        through(Model.class, FluentConclusionPredicate.class,
            "testImperativeExecutionPhrasesBuilder").lambda(fluentPredicates0).delegate(
            targetDelegate);
      }
    };
    try
    {
      assertTrue("testComplexImperativePhrasesBuilder-1 error !!!", builder2.async(list)
          .checkedGet());
      assertFalse("testComplexImperativePhrasesBuilder-2 error !!!", builder3.async(list)
          .checkedGet());
    } catch ( Exception ex )
    {
      fail("testComplexImperativePhrasesBuilder error ex!!!");
    }
  }

  @Test
  @SuppressWarnings (
  { "rawtypes", "unchecked" })
  public void testImperativePhrasesBuilderWithPStrategy ()
  {
    final Thread currentThread = Thread.currentThread();
    final ConclusionPredicate<Delegate<FluentConclusionPredicate>> compositionPredicate = DelegateFactory
        .<FluentConclusionPredicate> from(FluentConclusionPredicate.never());

    final Delegate<FluentConclusionPredicate> targetDelegate = new Delegate<FluentConclusionPredicate>()
    {
      @Override
      public void setContent ( Iterable<?> collection )
      {
      }

      @Override
      public boolean execute ( FluentConclusionPredicate... arguments )
      {
        assertNotSame("testImperativePhrasesBuilderWithPStrategy error", Thread.currentThread(),
            currentThread);
        return Optional.fromNullable(arguments[0]).or(always()).apply(1);
      }
    };
    try
    {
      ImmutableList<Model> list = ImmutableList.of();
      assertFalse("testImperativePhrasesBuilderWithPStrategy error !!!",
          new ImperativeIterableOrientedPhrasesBuilder()
          {
            @Override
            protected void build ()
            {
              rule(ParallelStrategy.<Boolean, PhraseExecutionException> separateThreadStrategy(),
                  Model.class, FluentConclusionPredicate.class, "builder").lambda(
                  compositionPredicate).delegate(targetDelegate);
            }
          }.async(list).checkedGet());

    } catch ( Exception ex )
    {
      ex.printStackTrace();
      fail("testImperativePhrasesBuilderWithPStrategy error ex!!!");
    }
  }

  @Test
  @SuppressWarnings ( "rawtypes")
  public void testStartContextAndEndContextInImperativeWay ()
  {
    final ImmutableList<Model> list = ImmutableList.of(Model.values(4), Model.values(6),
        Model.values(6), Model.values(55));

    final ConclusionPredicate<Delegate<FluentConclusionPredicate>> startEndPredicates = DelegateFactory
        .<FluentConclusionPredicate> from(
            fluent().eq(argument(4), descriptor(Model.class, Model.INT_ACCESSOR)),

            fluent().eq(argument(4), descriptor(Model.class, Model.INT_ACCESSOR)).or(
                fluent().eq(argument(55), descriptor(Model.class, Model.INT_ACCESSOR))));

    final Delegate<FluentConclusionPredicate> targetDelegate = new Delegate<FluentConclusionPredicate>()
    {
      ImmutableList<Object> list0;

      @Override
      public void setContent ( Iterable<?> iterable )
      {
        this.list0 = ImmutableList.copyOf(iterable);
      }

      @SuppressWarnings ( "unchecked")
      @Override
      public boolean execute ( FluentConclusionPredicate... arguments )
      {
        assertTrue(list0.size() == 4);
        assertTrue(arguments.length == 2);
        return arguments[0].apply(list0.get(0)) && arguments[1].apply(list0.get(list0.size() - 1));
      }
    };

    try
    {
      assertTrue("testStartContextAndEndContextInImperativeWay error !!!",
          new ImperativeIterableOrientedPhrasesBuilder()
          {
            @Override
            protected void build ()
            {
              through(Model.class, FluentConclusionPredicate.class, "ImperativeBuilder").lambda(
                  startEndPredicates).delegate(targetDelegate);
            }
          }.async(list).checkedGet());
    } catch ( Exception ex )
    {
      ex.printStackTrace();
      fail("testStartContextAndEndContextInImperativeWay error ex!!!");
    }
  }
}

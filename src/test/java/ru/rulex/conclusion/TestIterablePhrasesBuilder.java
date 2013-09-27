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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.rulex.conclusion.FluentConclusionPredicate.query;
import static ru.rulex.conclusion.FluentConclusionPredicate.typeSafeQuery;
import static ru.rulex.conclusion.FluentConclusionPredicate.number;

import org.junit.Test;

import ru.rulex.conclusion.PhraseBuildersFacade.AbstractIterableOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.IterableOrientedPhrasesBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;


public class TestIterablePhrasesBuilder
{

  private final ImmutableList<Model> list = ImmutableList.of( Model.from( 121 ),
      Model.from( 122 ) );
  private final ImmutableList<Model> failList = ImmutableList.of( Model.from( 99 ),
      Model.from( 1 ) );

  final ConclusionPredicate<Integer> intPredicate = new ConclusionPredicate<Integer>()
  {
    public boolean apply( Integer argument )
    {
      return argument > 100;
    }
  };

  final Selector<Model, Integer> intSelector = new Selector<Model, Integer>()
  {
    public Integer select( Model input )
    {
      return input.getInteger();
    }
  };

  /**
   * {@code IterableCollectionPhrasesBuilder} with fromTypeSafeSelector
   */
  @Test
  public void testIterableValidationCollectionPhrasesBuilderWithTypeSafeSelector()
  {

    final AbstractIterableOrientedPhrasesBuilder pBuilder = new IterableOrientedPhrasesBuilder()
    {
      @Override
      protected void build()
      {
        configure( Model.class, "simple-where-engine-with-typeSafeSelector" )
            .shouldMatch(
                typeSafeQuery( number( Model.class, Integer.class, Model.INT_ACCESSOR ),
                    intPredicate ) ).except( ImmutableSet.<Model> of( Model.from( 121 ) ) )
            .iteratorType( Iterators.WHERE );
      }

      @Override
      public <T> Boolean sync( Iterable<T> iterable, Runnable callback )
      {
        // TODO Auto-generated method stub
        return null;
      }
    };

    try
    {
      assertTrue( "error 1", pBuilder.async( list ).checkedGet() );
      assertTrue( "error 2", pBuilder.async( list ).checkedGet() );
      assertFalse( "error 3", pBuilder.async( failList ).checkedGet() );
    }
    catch (Exception ex)
    {
      fail( "testIterableValidationCollectionPhrasesBuilderWithTypeSafeSelector error ex!!!" );
    }
  }

  /**
   * {@code IterableCollectionPhrasesBuilder} with fromSelector
   */
  @Test
  public void testIterableValidationCollectionPhrasesBuilderWithSelector()
  {
    AbstractIterableOrientedPhrasesBuilder whereEngineBuilder = new IterableOrientedPhrasesBuilder()
    {
      protected void build()
      {
        configure( Model.class, "simple-where-engine-selector" )
            .shouldMatch( query( intSelector, intPredicate ) )
            .except( ImmutableSet.<Model> of( Model.from( 121 ) ) )
            .iteratorType( Iterators.WHERE );
      }
    };

    try
    {
      assertTrue( "error 4", whereEngineBuilder.async( list ).checkedGet() );
      assertTrue( "error 5", whereEngineBuilder.async( list ).checkedGet() );
      assertFalse( "error 6", whereEngineBuilder.async( failList ).checkedGet() );
    }
    catch (Exception ex)
    {
      fail( "testIterableValidationCollectionPhrasesBuilderWithSelector error ex!!!" );
    }
  }
}

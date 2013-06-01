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

import org.junit.Test;
import org.mockito.Mockito;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.FluentConclusionPredicate;

import static org.junit.Assert.*;
import static ru.rulex.conclusion.FluentConclusionPredicate.*;
import static ru.rulex.conclusion.RulexMatchersDsl.*;

public class TestFluentConclusionPredicate
{

  /**
   * TODO: 1 ApplyEight(pred, thatTrue, thatFalse) CompositeEight(pred, func,
   * thatTrue, thatFalse) TODO: 2 (NumberSelector val) -> 40 == 40 (true)
   */
  @Test
  public void testFluent()
  {
    try
    {
      fluent().apply( 1 );
    }
    catch (IllegalStateException ex)
    {
      // expected
    }
    catch (Exception e)
    {
      fail( "fluent() should be IllegalArgumentException !!!" + e );
    }

    assertTrue( always().apply( 1 ) );
    assertFalse( never().apply( 1 ) );

    assertFalse( fluent().and( never() ).apply( 1 ) );
    assertTrue( fluent().and( always() ).apply( 1 ) );

    assertFalse( fluent().or( never() ).apply( 1 ) );
    assertTrue( fluent().or( always() ).apply( 1 ) );

    assertFalse( fluent().and( fluent() ).apply( 3 ) );
    assertFalse( fluent().or( fluent() ).apply( 3 ) );

    assertTrue( always().and( fluent() ).apply( 5 ) );
    assertTrue( always().or( fluent() ).apply( 5 ) );

    assertFalse( never().and( fluent() ).apply( 5 ) );
    assertFalse( never().or( fluent() ).apply( 5 ) );

    assertFalse( always().and( never() ).apply( 1 ) );
    assertTrue( always().or( never() ).apply( 1 ) );
  }

  @Test
  public void testFluentPredicates()
  {
    ConclusionPredicate<Float> predicate = new ConclusionPredicate<Float>()
    {
      @Override
      public boolean apply( Float argument )
      {
        return false;
      }
    };

    ConclusionPredicate<Float> predicate2 = new ConclusionPredicate<Float>()
    {
      @Override
      public boolean apply( Float argument )
      {
        return argument == 6;
      }
    };

    ConclusionPredicate<Integer> intPredicate = new ConclusionPredicate<Integer>()
    {
      @Override
      public boolean apply( Integer argument )
      {
        return argument == 56;
      }
    };

    assertFalse( " testFluentPredicates 1 error !!!",
        bind( predicate ).and( FluentConclusionPredicate.<Number> always() ).apply( 6f ) );
    assertTrue( " testFluentPredicates 2 error !!!",
        bind( predicate2 ).and( FluentConclusionPredicate.<Number> always() ).apply( 6f ) );
    assertTrue( " testFluentPredicates 3 error !!!", bind( intPredicate ).apply( 56 ) );
  }

  @Test
  public void testCompositionsFluentPredicates()
  {
    Model mock = Mockito.mock( Model.class );
    Mockito.when( mock.getInteger() ).thenReturn( 40 );
    String accessor = "getInteger";

    assertTrue(
        " testCompositionsFluentPredicates AND error !!!",
        fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) )
            .and( fluent().less( argument( 39 ), descriptor( Model.class, accessor ) ) )
            .apply( mock ) );

    assertTrue(
        " testCompositionsFluentPredicates OR error !!!",
        fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) )
            .or( fluent().less( argument( 41 ), descriptor( Model.class, accessor ) ) )
            .apply( mock ) );

    assertTrue(
        "testCompositionsFluentPredicates AND AND error !!!",
        fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) )
            .and( fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) ) )
            .and( fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) ) ).apply( mock ) );

    assertTrue(
        "testCompositionsFluentPredicates (eq AND (eq AND eq)) OR eq ",
        fluent()
            .eq( argument( 40 ), descriptor( Model.class, accessor ) )
            .and(
                fluent().eq( argument( 39 ), descriptor( Model.class, accessor ) ).and(
                    bind( new ConclusionPredicate<Model>()
                    {
                      @Override
                      public boolean apply( Model argument )
                      {
                        return false;
                      }
                    } ) ) ).or( fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) ) )
            .apply( mock ) );

    assertTrue(
        "testCompositionsFluentPredicates2 (eq AND eq AND eq) OR eq ",
        fluent().eq( argument( 39 ), descriptor( Model.class, accessor ) )
            .and( fluent().eq( argument( 39 ), descriptor( Model.class, accessor ) ) )
            .and( fluent().eq( argument( 39 ), descriptor( Model.class, accessor ) ) )
            .or( fluent().eq( argument( 40 ), descriptor( Model.class, accessor ) ) ).apply( mock ) );
  }
}

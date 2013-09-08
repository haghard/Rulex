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
package ru.rulex.conclusion.guice;

import org.junit.Test;

import com.google.inject.Injector;

import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.GuiceEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.Phrases;
import ru.rulex.conclusion.delegate.Delegate;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.conclusion.delegate.ProxyUtils.toPredicate;
import static ru.rulex.conclusion.delegate.ProxyUtils.toSelector;
import static org.junit.Assert.fail;
import static ru.rulex.conclusion.guice.GuiceMutableDependencyAnalyzerModule.*;
import static com.google.inject.Guice.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class GuiceEventOrientedEngineModuleTest
{
  final Model orFoo = Model.values( 91, 100.91f );
  final Model andFoo = Model.values( 6, 0, "aaaaaaa", false );
  final Model disjFoo = Model.values( 6, 0, "aaaaaaa", false );

  @Test
  @SuppressWarnings("unchecked")
  public void testSelectorPredicateApi() throws Exception
  {
    // no works with varargs
    toPredicate( callOn( Delegate.class ).execute( new Integer[]
    { 1, 2, 3 } ) ).apply( new Delegate<Integer>()
    {
      @Override
      public void setContent( Iterable<?> collection )
      {
      }

      @Override
      public boolean execute( Integer... arguments )
      {
        assertThat( arguments.length ).isEqualTo( 3 );
        return false;
      }
    } );

    assertThat( toPredicate( callOn( Model.class ).getBoolean() ).apply( andFoo ) ).isFalse();
    assertThat( toSelector( callOn( Model.class ).getInteger() ).select( andFoo ) ).isGreaterThan(
        5 );
    assertThat( toSelector( callOn( Model.class ).getString() ).select( andFoo ) ).isEqualTo(
        "aaaaaaa" );
  }

  @Test
  public void shouldBeValidWithTwoConditionsAsync()
  {
    final Model foo = Model.values( 10, 0, "aaaaaaa", false );
    try
    {
      Injector injector = createInjector( $expression(
          $less( 9, callOn( Model.class ).getInteger(), "9 < en.getInput()" ),
          $eq( "aaaaaaa", callOn( Model.class ).getString(), "aaaaaaa eq en.getString()" ) ) );

      
      GuiceEventOrientedPhrasesBuilder phraseBuilder = 
          injector.getInstance( GuiceEventOrientedPhrasesBuilder.class );
      Boolean result = phraseBuilder.async( foo ).checkedGet();
      assertThat( result ).as( "shouldBeValidWithTwoConditionsAsync error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      fail( "shouldBeValidWithTwoConditionsAsync result error  ex!!!" + ex.getMessage() );
    }
  }

  @Test
  public void shouldBeValidWithTwoConditionsSync()
  {
    final Model foo = Model.values( 23, 0, "aaaaaaa", false );
    try
    {
      Injector injector = createInjector( $expression(
        $less( 22, callOn( Model.class ).getInteger(), "22 < en.getInput()" ),
        $eq( "aaaaaaa", callOn( Model.class ).getString(), "aaaaaaa eq en.getString()" ) ) );

      GuiceEventOrientedPhrasesBuilder phraseBuilder = injector
          .getInstance( GuiceEventOrientedPhrasesBuilder.class );
      Boolean result = phraseBuilder.async( foo ).checkedGet();
      assertThat( result ).as( "shouldBeValidWithTwoConditionsSync error !!!" )
          .isTrue();
    }
    catch (Exception ex)
    {
      fail( "shouldBeValidWithTwoConditionsSync result error  ex!!!"
          + ex.getMessage() );
    }
  }

  @Test
  public void shouldBeValidBetweenConditions()
  {
    Injector injector = createInjector(
        $expression( Phrases.ANY_TRUE,
          $more( 92, callOn( Model.class ).getInteger(), "92 > en.getInput()" ) ),
          $less( 56, callOn( Model.class ).getInteger(), "56 > en.getInput()" ) );

    final GuiceEventOrientedPhrasesBuilder phraseBuilder = injector
        .getInstance( GuiceEventOrientedPhrasesBuilder.class );
    try
    {
      boolean result = phraseBuilder.async( orFoo ).get();
      assertThat( result ).as( "shouldBeValidBetweenConditions error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "shouldBeValidBetweenConditions ex error !!!" );
    }
  }

  @Test
  public void shouldBeValidWithComplexConditionsAsync()
  {
    Injector injector = createInjector( $expression(
        $more( 8f, callOn( Model.class ).getFloat(), "8 > en.getFloat()" ),
          $or( "or test condition",
        $more( 7, callOn( Model.class ).getInteger(), "7 > en.getInteger()" ),
        $eq( "aaaa", callOn( Model.class ).getString(), "aaaaaaa eq en.getString()" ) ) ) );

    final GuiceEventOrientedPhrasesBuilder enginePhrase = injector
        .getInstance( GuiceEventOrientedPhrasesBuilder.class );
    try
    {
      boolean result = enginePhrase.async( disjFoo ).checkedGet();
      assertThat( result ).as( "shouldBeValidWithComplexConditionsAsync error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "shouldBeValidWithComplexConditionsAsync result error ex !!!" );
    }
  }

  @Test
  public void shouldBeValidWithComplexConditionsSync()
  {
    final Model foo = Model.values( 6, 0, "aaaaaaa", false );
    Injector injector = createInjector( $expression(
        $more( 8f, callOn( Model.class ).getFloat(), "8 > en.getFloat()" ),
          $or( "or test condition",
        $eq( true, callOn( Model.class ).getBoolean(), "false eq en.getBoolean()" ),
        $more( 7, callOn( Model.class ).getInteger(), "7 > en.getInteger()" ),
        $eq( "aaaa", callOn( Model.class ).getString(), "aaaaaaa eq en.getString()" ) ) ) );

    final GuiceEventOrientedPhrasesBuilder enginePhrase = injector
        .getInstance( GuiceEventOrientedPhrasesBuilder.class );
    try
    {
      boolean result = enginePhrase.async( foo ).checkedGet();
      assertThat( result ).as( "shouldBeValidWithComplexConditionsSync error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "shouldBeValidWithComplexConditionsSync result error ex !!!" );
    }
  }
}
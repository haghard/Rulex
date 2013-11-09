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

import com.google.inject.*;
import org.junit.Test;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.GuiceImmutablePhrasesBuilder;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.name.Names.named;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.conclusion.guice.AbstractGuiceImmutablePhraseModule.immutablePhrase;
import static ru.rulex.conclusion.guice.AbstractGuiceImmutablePhraseModule.or;
import static ru.rulex.conclusion.guice.GuiceGenericTypes.newEnclosedGenericType;
import static ru.rulex.conclusion.guice.GuiceGenericTypes.newGenericType;
import static ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableEqualsConclusionPredicate;
import static ru.rulex.conclusion.guice.PhraseDslBuilders.val;

public class GuiceEventOrientedEngineModuleTest
{
  @Test
  public void simpleConditionGuicePhraseAsync()
  {
    final Model foo = Model.from( 10, 0, "master card", false );
    try
    {
      final Injector injector = createInjector( immutablePhrase(
        val( 9 ).less( callOn( Model.class ).getInteger() ),
        val( "master card" ).eq( callOn( Model.class ).getString() ),
        val( callOn( Model.class ).getInteger() ).isNotNull(),
        val( callOn( Model.class ).getFloat() ).isNotNull()
      ) );

      final GuiceImmutablePhrasesBuilder phraseBuilder =
              injector.getInstance( GuiceImmutablePhrasesBuilder.class );

      Boolean result = phraseBuilder.async( foo ).checkedGet();
      assertThat( result ).as( "simpleConditionGuicePhraseAsync error !!!" ).isTrue();
    }
    catch ( Exception ex )
    {
      fail( "simpleConditionGuicePhraseAsync result error  ex!!!" + ex.getMessage() );
    }
  }

  @Test
  public void simpleConditionGuicePhraseSync()
  {
    final Model foo = Model.from( 23, 0, "visa", false );
    try
    {
      final Injector injector = createInjector( immutablePhrase(
        val( 22 ).less( callOn( Model.class ).getInteger() ),
        val( "visa" ).eq( callOn( Model.class ).getString() ),
        val( "^visa*" ).regExp( callOn( Model.class ).getString() ),
        val( "^visa*", "^vis*" ).regExps( callOn( Model.class ).getString() ),
        val( "visa1", "visa", "visa2" ).equalsAnyOff( callOn( Model.class ).getString() ) ) );

      final GuiceImmutablePhrasesBuilder phraseBuilder =
              injector.getInstance( GuiceImmutablePhrasesBuilder.class );

      Boolean result = phraseBuilder.sync( foo );
      assertThat( result ).as( "simpleConditionGuicePhraseSync error !!!" )
              .isTrue();
    }
    catch ( Exception ex )
    {
      fail( "simpleConditionGuicePhraseSync result error  ex!!!"
              + ex.getMessage() );
    }
  }

  @Test
  public void shouldBeValidWithComplexConditionsAsync()
  {
    final Model foo = Model.from( 6, 0, "bread", false );
    final Injector injector = createInjector( immutablePhrase(
      val( 8f ).more( callOn( Model.class ).getFloat() ),
      or( "or",
        val( 7 ).more( callOn( Model.class ).getInteger() ),
        val( "visa" ).eq( callOn( Model.class ).getString() )
      ),
      or( "or",
        val( "visa" ).eq( callOn( Model.class ).getString() ),
        val( 7 ).more( callOn( Model.class ).getInteger()
        )
      )
    ) );

    final GuiceImmutablePhrasesBuilder enginePhrase = injector
            .getInstance( GuiceImmutablePhrasesBuilder.class );
    try
    {
      boolean result = enginePhrase.async( foo ).checkedGet();
      assertThat( result ).as( "shouldBeValidWithComplexConditions error !!!" ).isTrue();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      fail( "shouldBeValidWithComplexConditions result error ex !!!" );
    }
  }

  @Test
  public void shouldBeValidWithComplexConditionsSync()
  {
    final Model foo = Model.from( 6, 0, "maestro", false );
    final Injector injector = createInjector( immutablePhrase(
      val( 8f ).more( callOn( Model.class ).getFloat() ),
      or( "or test condition",
        val( 7 ).more( callOn( Model.class ).getInteger() ),
        val( "maestro" ).eq( callOn( Model.class ).getString() )
      )
    ) );

    final GuiceImmutablePhrasesBuilder enginePhrase = injector
            .getInstance( GuiceImmutablePhrasesBuilder.class );
    try
    {
      boolean result = enginePhrase.async( foo ).checkedGet();
      assertThat( result ).as( "shouldBeValidWithComplexConditionsSync error !!!" ).isTrue();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      fail( "shouldBeValidWithComplexConditionsSync result error ex !!!" );
    }
  }

  @Test
  public void injectionTest()
  {
    final Injector injector = Guice.createInjector( new AbstractModule()
    {
      @Override
      protected void configure()
      {
        bind( Integer.class ).toInstance( 5 );
      }
    } );

    InjectableEqualsConclusionPredicate p = injector.getInstance( new Key<InjectableEqualsConclusionPredicate<Integer>>()
    {
    } );

    final TypeLiteral<Integer> genericType = TypeLiteral.get( Integer.class );
    final Injector injector2 = Guice.createInjector( new AbstractModule()
    {
      @Override
      protected void configure()
      {
        bind( Integer.class ).toInstance( 5 );
        bind( newGenericType( ConclusionPredicate.class, genericType ) )
                .annotatedWith( named( "1" ) )
                .to( newEnclosedGenericType( InjectableEqualsConclusionPredicate.class, genericType ) );
      }
    } );

    injector2.getInstance( Key.get( newGenericType( ConclusionPredicate.class, genericType ), named( "1" ) ) );
  }
}
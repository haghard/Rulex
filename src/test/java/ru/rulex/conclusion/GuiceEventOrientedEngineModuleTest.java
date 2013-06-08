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
import com.google.inject.Injector;
import ru.rulex.conclusion.Phrases;
import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.delegate.Delegate;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.conclusion.delegate.ProxyUtils.toPredicate;
import static ru.rulex.conclusion.delegate.ProxyUtils.toSelector;
import static org.junit.Assert.fail;
import static ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule.*;
import static com.google.inject.Guice.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class GuiceEventOrientedEngineModuleTest
{

  final Model andFoo = Model.values( 6, 0, "aaaaaaa", false );

  final Model orFoo = Model.values( 91, 100.91f );

  final Model disjFoo = Model.values( 6, 0, "aaaaaaa", false );

  final Selector<Model, Integer> intSelector = new Selector<Model, Integer>()
  {
    @Override
    public Integer select( Model input )
    {
      return input.getInteger();
    }
  };

  final Selector<Model, String> stringSelector = new Selector<Model, String>()
  {
    @Override
    public String select( Model input )
    {
      return input.getString();
    }
  };

  final Selector<Model, Float> floatSelector = new Selector<Model, Float>()
  {
    @Override
    public Float select( Model input )
    {
      return input.getFloat();
    }
  };

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

  /**
   * test and condition
   */
  @Test
  public void testAndGuiceModuleWithProxy()
  {
    final Model foo = Model.values( 10, 0, "aaaaaaa", false );
    try
    {
      Injector injector = createInjector( $expression(
          $less( 9, callOn( Model.class ).getInteger(), "9 < en.getInput()" ),
          $eq( "aaaaaaa", callOn( Model.class ).getString(), "aaaaaaa eq en.getString()" ) ) );

      AbstractEventOrientedPhrasesBuilder phraseBuilder = injector
          .getInstance( AbstractEventOrientedPhrasesBuilder.class );
      Boolean result = phraseBuilder.async( foo ).checkedGet();
      assertThat( result ).as( "testAndGuiceModuleWithProxy error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      fail( "testAndGuiceModuleWithProxy result error  ex!!!" + ex.getMessage() );
    }
  }

  @Test
  public void testAndGuiceModuleWithProxyAndExplicitlySelector()
  {
    final Model foo = Model.values( 23, 0, "aaaaaaa", false );
    try
    {
      Injector injector = createInjector( $expression(
          $less( 22, toSelector( callOn( Model.class ).getInteger() ), "22 < en.getInput()" ),
          $eq( "aaaaaaa", toSelector( callOn( Model.class ).getString() ),
              "aaaaaaa eq en.getString()" ) ) );

      AbstractEventOrientedPhrasesBuilder phraseBuilder = injector
          .getInstance( AbstractEventOrientedPhrasesBuilder.class );
      Boolean result = phraseBuilder.async( foo ).checkedGet();
      assertThat( result ).as( "testAndGuiceModuleWithProxyAndExplicitlySelector error !!!" )
          .isTrue();
    }
    catch (Exception ex)
    {
      fail( "testAndGuiceModuleWithProxyAndExplicitlySelector result error  ex!!!"
          + ex.getMessage() );
    }
  }

  @Test
  public void testAndGuiceModuleWithSelectors()
  {
    final Model foo = Model.values( 3, 89.56f, "aaaaaaa", false );
    try
    {
      Injector injector = createInjector( $expression(
          $less( 89.55f, floatSelector, "89.55 < en.getInput()" ),
          $eq( "aaaaaaa", stringSelector, "aaaaaaa eq en.getString()" ) ) );

      AbstractEventOrientedPhrasesBuilder phraseBuilder = injector
          .getInstance( AbstractEventOrientedPhrasesBuilder.class );
      Boolean result = phraseBuilder.async( foo ).checkedGet();
      assertThat( result ).as( "testAndGuiceModuleWithSelectors error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      fail( "testAndGuiceModuleWithSelectors result error  ex!!!" + ex.getMessage() );
    }
  }

  /**
   * test or condition on single event fields through
   * {@code Phrases.SINGLE_ANY_TRUE}
   */
  @Test
  public void testAnyGuiceModuleWithSelectors()
  {
    Injector injector = createInjector(
        $expression( Phrases.ANY_TRUE, $more( 92, intSelector, "92 > en.getInput()" ) ),
        $less( 56, intSelector, "56 > en.getInput()" ) );

    final AbstractEventOrientedPhrasesBuilder phraseBuilder = injector
        .getInstance( AbstractEventOrientedPhrasesBuilder.class );
    try
    {
      boolean result = phraseBuilder.async( orFoo ).get();
      assertThat( result ).as( "testAnyGuiceModuleWithSelectors error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "testAnyGuiceModuleWithSelectors ex error !!!" );
    }
  }

  /**
   * <p>
   * ( 8 > getInteger()) and (( 7 > getFloat()) OR ( "aaaaaaa" eq en.getString()
   * ))
   * </p>
   */
  @Test
  public void testDisjunctionGuiceModuleWithSelectors()
  {
    Injector injector = createInjector( $expression(
        $more( 8f, floatSelector, "8 > en.getFloat()" ),
        $or( "or test condition", $more( 7, intSelector, "7 > en.getInteger()" ),
            $eq( "aaaa", stringSelector, "aaaaaaa eq en.getString()" ) ) ) );

    final AbstractEventOrientedPhrasesBuilder enginePhrase = injector
        .getInstance( AbstractEventOrientedPhrasesBuilder.class );
    try
    {
      boolean result = enginePhrase.async( disjFoo ).checkedGet();
      assertThat( result ).as( "testDisjunctionGuiceModuleWithSelectors error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "testDisjunctionGuiceModuleWithSelectors result error ex !!!" );
    }
  }

  @Test
  public void testDisjunctionGuiceModuleWithProxy()
  {
    final Model foo = Model.values( 6, 0, "aaaaaaa", false );
    Injector injector = createInjector( $expression(
        $more( 8f, callOn( Model.class ).getFloat(), "8 > en.getFloat()" ),
        $or( "or test condition",
            $eq( true, callOn( Model.class ).getBoolean(), "false eq en.getBoolean()" ),
            $more( 7, callOn( Model.class ).getInteger(), "7 > en.getInteger()" ),
            $eq( "aaaa", callOn( Model.class ).getString(), "aaaaaaa eq en.getString()" ) ) ) );

    final AbstractEventOrientedPhrasesBuilder enginePhrase = injector
        .getInstance( AbstractEventOrientedPhrasesBuilder.class );
    try
    {
      boolean result = enginePhrase.async( foo ).checkedGet();
      assertThat( result ).as( "testDisjunctionGuiceModuleWithProxy error !!!" ).isTrue();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "testDisjunctionGuiceModuleWithProxy result error ex !!!" );
    }
  }

  /*
   * TODO: implement like this API , with AnnotationProcessor
  public void testAnnotationApi() {
    //@Module(addsTo = RootModule.class, injects = { C.class, D.class })
    @Expression( onClass = Model.class,
        conditions = { callOn( Model.class ).getFloat(), callOn( Model.class ).getFloat() } )
    
  }
  */
}
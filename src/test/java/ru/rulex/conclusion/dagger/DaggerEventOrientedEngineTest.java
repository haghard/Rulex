package ru.rulex.conclusion.dagger;

import org.junit.Test;
import ru.rulex.conclusion.*;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder;

import javax.inject.Named;

import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.PhraseBuildersFacade.environment;
import static ru.rulex.conclusion.PhraseBuildersFacade.var;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.dagger.UncompletedDaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class DaggerEventOrientedEngineTest
{
  @Test
  public void testDaggerBuilderWithDifferentTypes()
  {
    final AbstractEventOrientedPhraseBuilder builder = create(
      $expression(
        $less( 19, callOn( Model.class ).getInteger() ),
        $less( 19, callOn( Model.class ).getOtherInteger() ),
        $more( 56.78f, callOn( Model.class ).getFloat() ) ))
      .get( PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }

  @Test
  public void testDaggerBuilderWithSameType()
  {
    /*
    final AbstractEventOrientedPhraseBuilder builder = create(
      $expression(
        $less( 19, callOn( Model.class ).getInteger() ),
        $more( 79, callOn( Model.class ).getOtherInteger() ),
        $moreOrEquals( 56.78f, callOn( Model.class ).getFloat() ) ) )
      .get( AbstractEventOrientedPhraseBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
    */

    final String val1 = "x1";
    final String val2 = "x2";

    final DaggerEventOrientedPhrasesBuilder lazyBuilder = create(
      $lazyExpression(
        $less0( 12, val1 )
      )
    ).get( DaggerEventOrientedPhrasesBuilder.class );

    lazyBuilder.eval(
      environment(
        var( val1, callOn( Model.class ).getInteger() ))
    ).async( Model.values( 20, 78 ) );
  }

  /*
  TODO: implement like this API , with AnnotationProcessor
  public void testAnnotationApi() {
    //@Module(addsTo = RootModule.class, injects = { C.class, D.class })
    @Expression( onClass = Model.class,
        conditions = { callOn( Model.class ).getFloat(), callOn( Model.class ).getFloat() } )

  }
  */

  interface Expression
  {
    @Less( "{value}" )
    void someValue1( @Named( "value" ) Integer value );

    @More( "{value}" )
    void someValue2( @Named( "value" ) Integer value0 );
  }
}

package ru.rulex.conclusion.dagger;

import org.junit.Test;
import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractMutableEventOrientedPhraseBuilder;

import javax.inject.Named;

import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.PhraseBuildersFacade.environment;
import static ru.rulex.conclusion.PhraseBuildersFacade.var;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.dagger.MutableDaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class DaggerEventOrientedEngineTest
{
  @Test
  public void testSimpleDaggerBuilder()
  {
    final AbstractEventOrientedPhraseBuilder builder = create(
      $expression(
        $less( 19, callOn( Model.class ).getInteger() ),
        $less( 19, callOn( Model.class ).getOtherInteger() ),
        $more( 56.78f, callOn( Model.class ).getFloat() ) ))
      .get( AbstractEventOrientedPhraseBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }

  @Test
  public void testMutableDaggerBuilder()
  {
    final String val1 = "x1";
    final String val2 = "x2";

    final AbstractMutableEventOrientedPhraseBuilder mutableBuilder = create(
      $lazyExpression(
        $less0( 12, val1 ),
        $less0( 13, val2 )
      )
    ).get( AbstractMutableEventOrientedPhraseBuilder.class );

    mutableBuilder.eval(
      environment(
        var( val1, callOn( Model.class ).getInteger() ),
        var( val2, callOn( Model.class ).getInteger() )
      )
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

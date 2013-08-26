package ru.rulex.conclusion.dagger;

import org.junit.Test;
import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;

import javax.inject.Named;

import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class DaggerEventOrientedEngineTest
{
  @Test
  public void testDaggerBuilderWithDifferentTypes()
  {
    final AbstractEventOrientedPhrasesBuilder builder = create(
            $expression(
              $less( 19, callOn( Model.class ).getInteger() ),
              $less( 19, callOn( Model.class ).getOtherInteger() ),
              $more( 56.78f, callOn( Model.class ).getFloat() ) ) )
            .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }

  @Test
  public void testDaggerBuilderWithSameType()
  {
    final AbstractEventOrientedPhrasesBuilder builder = create(
      $expression(
        $less( 19, callOn( Model.class ).getInteger() ),
        $more( 79, callOn( Model.class ).getOtherInteger() ),
        $moreOrEquals( 56.78f, callOn( Model.class ).getFloat() ) ) )
          .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
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

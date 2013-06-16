package ru.rulex.conclusion.dagger;

import org.junit.Test;
import ru.rulex.conclusion.Model;
import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;

public class DaggerEventOrientedEngineTest
{
  @Test
  public void testDaggerBuilder()
  {
    AbstractEventOrientedPhrasesBuilder builder0 = create(
        $expression( 
            $less( 19, callOn( Model.class ).getInteger() ),
            $more( 56.78f, callOn( Model.class ).getFloat() ) ) )
              .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder0.sync( Model.values( 20, 78 ) ) ).isTrue();

    AbstractEventOrientedPhrasesBuilder builder = create(
        $expression(
            $less( 19, callOn( Model.class ).getInteger() ),
            $more( 79, callOn( Model.class ).getOtherInteger() ),
            $moreOrEquals( 56.78f, callOn( Model.class ).getFloat() ) ) ).get(
        AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }
}

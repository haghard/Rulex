package ru.rulex.conclusion.dagger;

import org.junit.Test;
import javax.inject.Named;
import ru.rulex.conclusion.Model;
import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.*;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;

public class DaggerEventOrientedEngineTest
{
  @Test
  public void testDaggerBuilderWithDifferentTypes()
  {
    final AbstractEventOrientedPhrasesBuilder builder = create(
        $expression(
            $less( 19, callOn( Model.class ).getInteger() ),
            $more( 56.78f, callOn( Model.class ).getFloat() ) ) )
              .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }
  
  @Test
  public void testDaggerBuilderWithSameType() {
    final AbstractEventOrientedPhrasesBuilder builder = create(
        $expression(
            $less( 19, callOn( Model.class ).getInteger() ),
            $more( 79, callOn( Model.class ).getOtherInteger() ),
            $moreOrEquals( 56.78f, callOn( Model.class ).getFloat() ) ) )
              .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }

  interface Expression {
    @Less("{value}") 
    void someValue1(@Named("value") Integer value);

    @More("{value}") 
    void someValue2(@Named("value") Integer value0);
  }
}

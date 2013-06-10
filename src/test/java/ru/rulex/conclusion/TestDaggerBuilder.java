package ru.rulex.conclusion;

import org.junit.Test;
import dagger.Module;
import dagger.Provides;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.$less;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.$more;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.$expression;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;

public class TestDaggerBuilder
{

  @Module(injects = ConclusionPredicate.class, overrides = true, library = true)
  static class TestModule
  {
    @SuppressWarnings("rawtypes")
    @Provides
    ConclusionPredicate providePredicate()
    {
      ConclusionPredicate mock = mock( ConclusionPredicate.class );
      when( mock.apply( anyInt() ) ).thenReturn( false );
      return mock;
    }
  }

  @Test
  public void testDaggerBuilder()
  {
    AbstractEventOrientedPhrasesBuilder builder = create(
        $expression( 
            $less( 19, callOn( Model.class ).getInteger() ),
            $more( 79, callOn( Model.class ).getOtherInteger() ) ) )
            .get(AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }
}

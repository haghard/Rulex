package ru.rulex.conclusion.dagger;

import org.junit.Test;
import dagger.Module;
import dagger.Provides;
import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.dagger.TestDaggerBuilder.MockModuleImpl.predicateMock;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.Selector;

public class TestDaggerBuilder
{
  @Module(injects = Selector.class, library = true)
  public static class MockModuleImpl<T extends Comparable<? super T>> implements MockModule
  {
    private final  Selector<Object, T> selector;
    private MockModuleImpl(final T mockValue )
    {
      this.selector = new Selector<Object, T>() {
        @Override
        public T select(Object argument) {
          return mockValue;
        }
      };
    }

    static  <T extends Comparable<? super T>> MockModuleImpl<T> predicateMock(T mockValue)
    {
      return new MockModuleImpl<T>( mockValue );
    }

    @SuppressWarnings("rawtypes")
    @Provides
    Selector provideSelector()
    {
      return selector;
    }
  }

  @Test
  public void testDaggerBuilder()
  {

    AbstractEventOrientedPhrasesBuilder builder = create(
        $expression(
            $lessMock( 1,  predicateMock( 5 ) ),
            $less( 19, callOn( Model.class ).getInteger() ),
            $more( 79, callOn( Model.class ).getOtherInteger() ) ,
            $moreOrEquals( 56.78f, callOn( Model.class ).getFloat() ) ) )
              .get(AbstractEventOrientedPhrasesBuilder.class);

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }

}

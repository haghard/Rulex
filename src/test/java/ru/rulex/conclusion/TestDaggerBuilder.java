package ru.rulex.conclusion;

import org.junit.Test;
import org.mockito.Mockito;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.dagger.DaggerAnalyzerModule;
import ru.rulex.conclusion.dagger.DaggerUnit;

public class TestDaggerBuilder
{
  @SuppressWarnings("unchecked")
  private static <T extends Comparable<? super T>> ConclusionPredicate<T> createLessPredicateFromGrapth(
      T value )
  {
    return ObjectGraph.create( DaggerUnit.less( value ) ).get( ConclusionPredicate.class );
  }

  @SuppressWarnings("unchecked")
  private static <T extends Comparable<? super T>> ConclusionPredicate<T> createMorePredicateFromGrapth(
      T value )
  {
    return ObjectGraph.create( DaggerUnit.more( value ) ).get( ConclusionPredicate.class );
  }

  @SuppressWarnings("unchecked")
  private static <T extends Comparable<? super T>> ConclusionPredicate<T> createMorePredicateFromGrapthWithMock(
      T value )
  {
    return ObjectGraph.create( DaggerUnit.more( value ) ).plus( new TestModule() )
        .get( ConclusionPredicate.class );
  }

  @Module(injects = ConclusionPredicate.class, overrides = true, library = true)
  static class TestModule
  {
    @SuppressWarnings("rawtypes")
    @Provides
    ConclusionPredicate providePredicate()
    {
      ConclusionPredicate mock = Mockito.mock( ConclusionPredicate.class );
      Mockito.when( mock.apply( Mockito.anyInt() ) ).thenReturn( false );
      return mock;
    }

  }

  @Test
  public void testInjection()
  {
    assertThat( createLessPredicateFromGrapth( 10 ).apply( 50 ) ).isTrue();
    assertThat( createMorePredicateFromGrapth( 50 ).apply( 10 ) ).isTrue();

    // mock call
    assertThat( createMorePredicateFromGrapthWithMock( 90 ).apply( 14 ) ).isFalse();
  }

  @Test
  public void testExpression()
  {
    AbstractEventOrientedPhrasesBuilder builder = ObjectGraph.create(
        DaggerAnalyzerModule.expression( DaggerUnit.more( 19 ), DaggerUnit.less( 12 ) ) )
        .get(AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( 18 ) ).isTrue();
  }

  @Test
  public void testDaggerBuilder()
  {
    AbstractEventOrientedPhrasesBuilder builder = ObjectGraph.create(
        DaggerAnalyzerModule.expression( DaggerUnit.less( 19, callOn( Model.class ).getInteger() ) ) )
        .get(AbstractEventOrientedPhrasesBuilder.class );

    assertThat( true ).isTrue();
  }
}

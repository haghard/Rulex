package ru.rulex.conclusion;

import java.lang.reflect.Array;

import org.junit.Test;
import org.mockito.Mockito;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import static org.fest.assertions.api.Assertions.assertThat;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.dagger.DaggerUnit;
import ru.rulex.conclusion.guice.SimpleAssertionUnit;

public class TestDaggerBuilder
{
  @SuppressWarnings("unchecked")
  private static <T extends Comparable<? super T>> ConclusionPredicate<T> createLessPredicateFromGrapth(T value) 
  {
    return ObjectGraph.create( DaggerUnit.less( value ) ).get( ConclusionPredicate.class );
  }

  @SuppressWarnings("unchecked")
  private static <T extends Comparable<? super T>> ConclusionPredicate<T> createMorePredicateFromGrapth(T value) 
  {
    return ObjectGraph.create( DaggerUnit.more( value ) ).get( ConclusionPredicate.class );
  }

  @SuppressWarnings("unchecked")
  private static <T extends Comparable<? super T>> ConclusionPredicate<T> createMorePredicateFromGrapthWithMock(T value) 
  {
    return ObjectGraph.create( DaggerUnit.more( value ) ).plus( new TestModule() ).get( ConclusionPredicate.class );
  }

  @Module(
      injects = ConclusionPredicate.class,
      overrides = true
      )
  static class TestModule 
  {
    @SuppressWarnings("rawtypes")
    @Provides ConclusionPredicate providePredicate()
    {
      ConclusionPredicate mock = Mockito.mock( ConclusionPredicate.class );
      Mockito.when( mock.apply( Mockito.anyInt() )).thenReturn( false );
      return mock;
    }
    
  }
  @Test
  public void testInjection()
  {
    assertThat( createLessPredicateFromGrapth(10).apply( 50 ) ).isTrue();
    assertThat( createMorePredicateFromGrapth(50).apply( 10 ) ).isTrue();
    
    //mock call
    assertThat( createMorePredicateFromGrapthWithMock(90).apply( 14 ) ).isFalse();
  }

  @Module( injects = AbstractEventOrientedPhrasesBuilder.class )
  static final class DaggerAnalyzerModule
  {
    protected final AbstractPhrase<?> phrase;
    private final AbstractEventOrientedPhrasesBuilder phraseBuilder;
    
    static DaggerAnalyzerModule expression(DaggerUnit element) 
    {
      return new DaggerAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), element );
    }

    static DaggerAnalyzerModule expression( DaggerUnit element0, DaggerUnit element1 ) 
    {
      return new DaggerAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), element0, element1 );
    }
    
    DaggerAnalyzerModule(AbstractPhrase<?> phrase, DaggerUnit element)
    {
      this.phrase = phrase;
      this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );
      fillPhrase( element );
    }

    private <T> void fillPhrase( DaggerUnit element ) 
    {
      ConclusionPredicate<T> predicate = ObjectGraph.create( element ).get( ConclusionPredicate.class );
      phrase.addUnit( new SimpleAssertionUnit( predicate, "desc" ) );
    }
    
    DaggerAnalyzerModule(AbstractPhrase<?> phrase0, DaggerUnit element0, DaggerUnit element1)
    {
      this.phrase = phrase0;
      this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );
      fillPhrase( element0 );
      fillPhrase( element1 );
    }

    @Provides AbstractEventOrientedPhrasesBuilder getPhraseBuilder()
    {
      return phraseBuilder;
    }
  }

  @Test
  public void testExpression() 
  {
    AbstractEventOrientedPhrasesBuilder builder =
    ObjectGraph.create( DaggerAnalyzerModule.expression( DaggerUnit.more( 19 ),  DaggerUnit.less( 12 ) ) )
      .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( 18 ) ).isTrue();

  }
}

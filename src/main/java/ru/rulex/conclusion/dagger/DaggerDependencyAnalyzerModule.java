package ru.rulex.conclusion.dagger;

import dagger.Module;
import dagger.Provides;
import dagger.ObjectGraph;
import ru.rulex.conclusion.Phrases;
import ru.rulex.conclusion.AbstractPhrase;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.guice.SimpleAssertionUnit;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;
import static ru.rulex.conclusion.delegate.ProxyUtils.toSelector;
import static dagger.ObjectGraph.create;

@Module( 
    injects = AbstractEventOrientedPhrasesBuilder.class,
    library = true)
public final class DaggerDependencyAnalyzerModule
{
  private final AbstractEventOrientedPhrasesBuilder phraseBuilder;
  protected final AbstractPhrase<?> phrase;

  public static DaggerDependencyAnalyzerModule $expression(DaggerUnit element) 
  {
    return new DaggerDependencyAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), element );
  }

  public static DaggerDependencyAnalyzerModule $expression( DaggerUnit element0, DaggerUnit element1 ) 
  {
    return new DaggerDependencyAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), element0, element1 );
  }

  public static <T extends Comparable<?>> DaggerUnit $less( final T pvalue, final T argument )
  {
    return new DaggerUnit( pvalue, toSelector( argument ), LogicOperation.lessThan );
  }

  public static <T extends Comparable<?>> DaggerUnit $more( final T pvalue, final T argument )
  {
    return new DaggerUnit( pvalue, toSelector( argument ), LogicOperation.moreThan );
  }

  DaggerDependencyAnalyzerModule(AbstractPhrase<?> phrase, DaggerUnit element)
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );
    fillPhrase( element );
  }

  @SuppressWarnings("unchecked")
  private <T> void fillPhrase( DaggerUnit element ) 
  {
    ConclusionPredicate<T> predicate = create( element ).get( ConclusionPredicate.class );
    phrase.addUnit( new SimpleAssertionUnit( predicate, "desc" ) );
  }
  
  DaggerDependencyAnalyzerModule(AbstractPhrase<?> phrase0, DaggerUnit element0, DaggerUnit element1)
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

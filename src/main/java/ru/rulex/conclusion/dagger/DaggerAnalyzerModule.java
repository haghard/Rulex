package ru.rulex.conclusion.dagger;

import ru.rulex.conclusion.AbstractPhrase;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.Phrases;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.guice.SimpleAssertionUnit;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

@Module( 
    injects = AbstractEventOrientedPhrasesBuilder.class,
    library = true)
public final class DaggerAnalyzerModule
{
  protected final AbstractPhrase<?> phrase;
  private final AbstractEventOrientedPhrasesBuilder phraseBuilder;
  
  public static DaggerAnalyzerModule expression(DaggerUnit element) 
  {
    return new DaggerAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), element );
  }

  public static DaggerAnalyzerModule expression( DaggerUnit element0, DaggerUnit element1 ) 
  {
    return new DaggerAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), element0, element1 );
  }
  
  DaggerAnalyzerModule(AbstractPhrase<?> phrase, DaggerUnit element)
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );
    fillPhrase( element );
  }

  @SuppressWarnings("unchecked")
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

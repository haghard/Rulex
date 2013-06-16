package ru.rulex.conclusion.dagger;

import java.lang.reflect.Array;

import dagger.Module;
import dagger.Provides;
import ru.rulex.conclusion.AssertionUnit;
import ru.rulex.conclusion.Phrases;
import ru.rulex.conclusion.AbstractPhrase;
import ru.rulex.conclusion.delegate.ProxyUtils;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;
import static dagger.ObjectGraph.create;

@Module( 
    injects = AbstractEventOrientedPhrasesBuilder.class,
    library = true)
//<T extends Comparable<? super T>>
public final class DaggerDependencyAnalyzerModule
{
  private final AbstractEventOrientedPhrasesBuilder phraseBuilder;
  protected final AbstractPhrase<?> phrase;

  public static DaggerDependencyAnalyzerModule $expression(DaggerPredicateModule module) 
  {
    return construct( module );
  }

  public static DaggerDependencyAnalyzerModule $expression( DaggerPredicateModule module0, DaggerPredicateModule module1 ) 
  {
    return construct(module0, module1);
  }

  public static DaggerDependencyAnalyzerModule $expression( DaggerPredicateModule module0, DaggerPredicateModule module1,
      DaggerPredicateModule module2 ) 
  {
    return construct( module0, module1, module2 );
  }

  public static DaggerDependencyAnalyzerModule $expression( DaggerPredicateModule module0, DaggerPredicateModule module1, 
      DaggerPredicateModule module2, DaggerPredicateModule module3) 
  {
    return construct(module0, module1, module2, module3);
  }
  
  private static DaggerDependencyAnalyzerModule construct(Object... modules)
  {
    DaggerPredicateModule[] array = (DaggerPredicateModule[]) Array.newInstance(DaggerPredicateModule.class, modules.length);
    System.arraycopy( modules, 0, array, 0, modules.length );
    return new DaggerDependencyAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), array); 
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $less( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ),
        LogicOperation.lessThan );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $more( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ),
        LogicOperation.moreThan );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $lessOrEquals( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ),
        LogicOperation.lessOrEquals );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $moreOrEquals( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ),
        LogicOperation.moreOrEquals );
  }

  DaggerDependencyAnalyzerModule(AbstractPhrase<?> phrase, DaggerPredicateModule[] array)
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );
    for (DaggerPredicateModule module: array)
      fillPhraseFrom( module );
  }

  private void fillPhraseFrom( DaggerPredicateModule element )
  {    
    AssertionUnit ex = create( element ).get( element.getExpressionClass() );
    phrase.addUnit( ex );
  }

  @Provides AbstractEventOrientedPhrasesBuilder getPhraseBuilder()
  {
    return phraseBuilder;
  }
}

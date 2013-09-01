package ru.rulex.conclusion.dagger;

import dagger.ObjectGraph;
import ru.rulex.conclusion.*;

import java.lang.reflect.Array;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.UncompletedDaggerPredicateModule;

import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;

@dagger.Module(
        injects = AbstractEventOrientedPhrasesBuilder.class,
        library = true )
public class UncompletedDaggerDependencyAnalyzerModule
{
  private final MutableAbstractPhrase<?> phrase;
  private final AbstractEventOrientedPhrasesBuilder phraseBuilder;

  public static UncompletedDaggerDependencyAnalyzerModule $lazyExpression( ObjectGraph graph )
  {
    return compose( graph );
  }

  public static UncompletedDaggerDependencyAnalyzerModule $lazyExpression( ObjectGraph module0, ObjectGraph module1 )
  {
    return compose( module0, module1 );
  }

  public static <T extends Comparable<? super T>> ObjectGraph $less0( final T value, final String varName )
  {
    return ObjectGraph.create(
            new DaggerPredicateModule( value , LogicOperation.lessThan ),
            new UncompletedDaggerPredicateModule( varName )
    );
  }

  private static UncompletedDaggerDependencyAnalyzerModule compose( Object... modules )
  {
    ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, modules.length );
    System.arraycopy( modules, 0, array, 0, modules.length );
    return new UncompletedDaggerDependencyAnalyzerModule( MutableAbstractPhrase.all(), array );
  }

  private UncompletedDaggerDependencyAnalyzerModule( MutableAbstractPhrase<?> phrase, ObjectGraph[] array )
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );

    for ( ObjectGraph module : array )
      providePhrases( module );
  }

  private void providePhrases( ObjectGraph graph )
  {
    phrase.addUnit( graph.get( MutableAssertionUnit.class ) );
  }

  @dagger.Provides
  AbstractEventOrientedPhrasesBuilder providePhraseBuilder()
  {
    return phraseBuilder;
  }

}
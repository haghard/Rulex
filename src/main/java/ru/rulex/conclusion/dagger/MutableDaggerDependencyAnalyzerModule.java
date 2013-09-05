package ru.rulex.conclusion.dagger;

import dagger.ObjectGraph;
import ru.rulex.conclusion.*;

import java.lang.reflect.Array;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.UncompletedDaggerPredicateModule;

import ru.rulex.conclusion.PhraseBuildersFacade.AbstractMutableEventOrientedPhraseBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerMutableBuilder;

@dagger.Module(
        injects = AbstractMutableEventOrientedPhraseBuilder.class,
        library = true )
public class MutableDaggerDependencyAnalyzerModule
{
  private final MutableAbstractPhrase<?> phrase;
  private final AbstractMutableEventOrientedPhraseBuilder<?> phraseBuilder;

  public static <T> MutableDaggerDependencyAnalyzerModule $lazyExpression( ObjectGraph graph )
  {
    return compose( graph );
  }

  public static <T> MutableDaggerDependencyAnalyzerModule $lazyExpression( ObjectGraph module0, ObjectGraph module1 )
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

  private static <T> MutableDaggerDependencyAnalyzerModule compose( Object... modules )
  {
    ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, modules.length );
    System.arraycopy( modules, 0, array, 0, modules.length );
    return new MutableDaggerDependencyAnalyzerModule( MutableAbstractPhrase.all(), array );
  }

  private MutableDaggerDependencyAnalyzerModule( MutableAbstractPhrase<Object> phrase, ObjectGraph[] array )
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerMutableBuilder( phrase );

    for ( ObjectGraph module : array )
      providePhrases( module );
  }

  private void providePhrases( ObjectGraph graph )
  {
    phrase.addUnit( graph.get( MutableAssertionUnit.class ) );
  }

  @dagger.Provides
  AbstractMutableEventOrientedPhraseBuilder providePhraseBuilder()
  {
    return phraseBuilder;
  }

}
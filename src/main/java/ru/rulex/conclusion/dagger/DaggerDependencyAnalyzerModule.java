package ru.rulex.conclusion.dagger;

import dagger.ObjectGraph;
import ru.rulex.conclusion.AssertionUnit;
import ru.rulex.conclusion.ImmutableAbstractPhrase;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.CompleteDaggerPredicateModule;
import ru.rulex.conclusion.delegate.ProxyUtils;

import java.lang.reflect.Array;

import static dagger.ObjectGraph.create;

@dagger.Module(
        injects = AbstractEventOrientedPhrasesBuilder.class,
        library = true )
public final class DaggerDependencyAnalyzerModule
{
  private final AbstractEventOrientedPhrasesBuilder phraseBuilder;
  private final ImmutableAbstractPhrase<?> phrase;


  public static DaggerDependencyAnalyzerModule $expression( ObjectGraph module )
  {
    return compose( module );
  }

  public static DaggerDependencyAnalyzerModule $expression( ObjectGraph module0, ObjectGraph module1 )
  {
    return compose( module0, module1 );
  }

  public static DaggerDependencyAnalyzerModule $expression( ObjectGraph module0, ObjectGraph module1,
                                                            ObjectGraph module2 )
  {
    return compose( module0, module1, module2 );
  }

  public static DaggerDependencyAnalyzerModule $expression( ObjectGraph module0, ObjectGraph module1,
                                                            ObjectGraph module2, ObjectGraph module3 )
  {
    return compose( module0, module1, module2, module3 );
  }

  private static DaggerDependencyAnalyzerModule compose( Object... modules )
  {
    ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, modules.length );
    System.arraycopy( modules, 0, array, 0, modules.length );
    return new DaggerDependencyAnalyzerModule( ImmutableAbstractPhrase.all(), array );
  }


  public static <T extends Comparable<? super T>> ObjectGraph $less( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.lessThan ),
            new CompleteDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }

  public static <T extends Comparable<? super T>> ObjectGraph $more( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.moreThan ),
            new CompleteDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }

  public static <T extends Comparable<? super T>> ObjectGraph $lessOrEquals( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.lessOrEquals ),
            new CompleteDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }

  public static <T extends Comparable<? super T>> ObjectGraph $moreOrEquals( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.moreOrEquals ),
            new CompleteDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }


  private DaggerDependencyAnalyzerModule( ImmutableAbstractPhrase<?> phrase, ObjectGraph[] graph )
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );

    for ( ObjectGraph graph0 : graph )
      providePhrases( graph0 );
  }

  private void providePhrases( ObjectGraph element )
  {
    phrase.addUnit( create( element ).get( AssertionUnit.class ) );
  }

  @dagger.Provides
  AbstractEventOrientedPhrasesBuilder getPhraseBuilder()
  {
    return phraseBuilder;
  }
}

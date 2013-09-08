package ru.rulex.conclusion.dagger;


import dagger.ObjectGraph;
import ru.rulex.conclusion.ImmutableAssertionUnit;
import ru.rulex.conclusion.ImmutableAbstractPhrase;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventPhrasesBuilder;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.ImmutableDaggerPredicateModule;
import ru.rulex.conclusion.delegate.ProxyUtils;
import java.lang.reflect.Array;
import static dagger.ObjectGraph.create;

@dagger.Module(
        injects = DaggerEventPhrasesBuilder.class,
        library = true )
public final class DaggerImmutableDependencyAnalyzerModule
{
  private final ImmutableAbstractPhrase<?> phrase;
  private final DaggerEventPhrasesBuilder phraseBuilder;

  public static DaggerImmutableDependencyAnalyzerModule $expression( ObjectGraph module )
  {
    return compose( module );
  }

  public static DaggerImmutableDependencyAnalyzerModule $expression( ObjectGraph module0, ObjectGraph module1 )
  {
    return compose( module0, module1 );
  }

  public static DaggerImmutableDependencyAnalyzerModule $expression( ObjectGraph module0, ObjectGraph module1,
                                                            ObjectGraph module2 )
  {
    return compose( module0, module1, module2 );
  }

  public static DaggerImmutableDependencyAnalyzerModule $expression( ObjectGraph module0, ObjectGraph module1,
                                                            ObjectGraph module2, ObjectGraph module3 )
  {
    return compose( module0, module1, module2, module3 );
  }

  private static DaggerImmutableDependencyAnalyzerModule compose( Object... modules )
  {
    ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, modules.length );
    System.arraycopy( modules, 0, array, 0, modules.length );
    return new DaggerImmutableDependencyAnalyzerModule( ImmutableAbstractPhrase.all(), array );
  }


  public static <T extends Comparable<? super T>> ObjectGraph $less( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.lessThan ),
            new ImmutableDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }

  public static <T extends Comparable<? super T>> ObjectGraph $more( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.moreThan ),
            new ImmutableDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }

  public static <T extends Comparable<? super T>> ObjectGraph $lessOrEquals( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.lessOrEquals ),
            new ImmutableDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }

  public static <T extends Comparable<? super T>> ObjectGraph $moreOrEquals( final T value, final T argument )
  {
    return create(
            new DaggerPredicateModule( value, LogicOperation.moreOrEquals ),
            new ImmutableDaggerPredicateModule( ProxyUtils.toCastedSelector( argument ) ) );
  }


  private DaggerImmutableDependencyAnalyzerModule( ImmutableAbstractPhrase<Object> phrase, ObjectGraph[] graph )
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerEventPhrasesBuilder( phrase );

    for ( ObjectGraph graph0 : graph )
      providePhrases( graph0 );
  }

  @SuppressWarnings("unchecked")
  private void providePhrases( ObjectGraph element )
  {
    phrase.addUnit( element.get( ImmutableAssertionUnit.class ) );
  }

  @dagger.Provides
  @SuppressWarnings("rawtypes")
  DaggerEventPhrasesBuilder getPhraseBuilder()
  {
    return phraseBuilder;
  }
}

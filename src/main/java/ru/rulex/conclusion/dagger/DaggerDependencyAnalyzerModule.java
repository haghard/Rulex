package ru.rulex.conclusion.dagger;

import java.lang.reflect.Array;

import ru.rulex.conclusion.*;
import ru.rulex.conclusion.delegate.ProxyUtils;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerEventOrientedPhrasesBuilder;

import javax.inject.Inject;

import static dagger.ObjectGraph.create;

@dagger.Module(
    injects = AbstractEventOrientedPhrasesBuilder.class,
    library = true)
public final class DaggerDependencyAnalyzerModule
{
  private final AbstractEventOrientedPhrasesBuilder phraseBuilder;
  private final AbstractPhrase<?> phrase;

  public static DaggerDependencyAnalyzerModule $lazyExpression(DaggerPredicateModule module) {

  }

  public static DaggerDependencyAnalyzerModule $lazyExpression(DaggerPredicateModule module0, DaggerPredicateModule module1) {

  }

  public static DaggerDependencyAnalyzerModule $expression(DaggerPredicateModule module)
  {
    return compose( module );
  }

  public static DaggerDependencyAnalyzerModule $expression( DaggerPredicateModule module0, DaggerPredicateModule module1 ) 
  {
    return compose(module0, module1);
  }

  public static DaggerDependencyAnalyzerModule $expression( DaggerPredicateModule module0, DaggerPredicateModule module1,
      DaggerPredicateModule module2 ) 
  {
    return compose( module0, module1, module2 );
  }

  public static DaggerDependencyAnalyzerModule $expression( DaggerPredicateModule module0, DaggerPredicateModule module1, 
      DaggerPredicateModule module2, DaggerPredicateModule module3) 
  {
    return compose(module0, module1, module2, module3);
  }
  
  private static DaggerDependencyAnalyzerModule compose(Object... modules)
  {
    DaggerPredicateModule[] array = (DaggerPredicateModule[]) Array.newInstance(DaggerPredicateModule.class, modules.length);
    System.arraycopy( modules, 0, array, 0, modules.length );
    return new DaggerDependencyAnalyzerModule( Phrases.ALL_TRUE.withNarrowedType(), array );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $less( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ), LogicOperation.lessThan );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $less( final T value, final String varName )
  {
    return new DaggerPredicateModule( value, LogicOperation.lessThan );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $more( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ), LogicOperation.moreThan );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $more( final T value, final String varName )
  {

  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $lessOrEquals( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ), LogicOperation.lessOrEquals );
  }

  public static <T extends Comparable<? super T>> DaggerPredicateModule $moreOrEquals( final T value, final T argument )
  {
    return new DaggerPredicateModule( value, ProxyUtils.toCastedSelector( argument ), LogicOperation.moreOrEquals );
  }

  private DaggerDependencyAnalyzerModule(AbstractPhrase<?> phrase, DaggerPredicateModule[] array)
  {
    this.phrase = phrase;
    this.phraseBuilder = new DaggerEventOrientedPhrasesBuilder( phrase );
    for (DaggerPredicateModule module: array) {
      providePhrases( module );
    }
  }

  private void providePhrases( DaggerPredicateModule element )
  {    
    final AssertionUnit ex = create( element ).get( element.getExpressionClass() );
    phrase.addUnit( ex );
  }

  @dagger.Provides
  AbstractEventOrientedPhrasesBuilder getPhraseBuilder()
  {
    return phraseBuilder;
  }


  static class IntAssertionUnit<T> implements AssertionUnit<T>
  {
    private final ConclusionPredicate<Integer> predicate;
    private final Selector<Object, Integer> selector;

    @Inject
    IntAssertionUnit( ConclusionPredicate<Integer> predicate, Selector<Object, Integer> selector )
    {
      this.predicate = predicate;
      this.selector = selector;
    }

    @Override
    public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
    {
      return new FluentConclusionPredicate.SelectorPredicate<Object, Integer>( predicate, selector ).apply( event );
    }
  }

  static class FloatAssertionUnit<T> implements AssertionUnit<T>
  {
    private final ConclusionPredicate<Float> predicate;
    private final Selector<Object, Float> selector;

    @Inject
    FloatAssertionUnit( ConclusionPredicate<Float> predicate, Selector<Object, Float> selector )
    {
      this.predicate = predicate;
      this.selector = selector;
    }

    @Override
    public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
    {
      return new FluentConclusionPredicate.SelectorPredicate<Object, Float>( predicate, selector ).apply( event );
    }
  }

  static class StringAssertionUnit<T> implements AssertionUnit<T>
  {
    private final ConclusionPredicate<String> predicate;
    private final Selector<Object, String> selector;

    @Inject
    StringAssertionUnit( ConclusionPredicate<String> predicate, Selector<Object, String> selector )
    {
      this.predicate = predicate;
      this.selector = selector;
    }

    @Override
    public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
    {
      return new FluentConclusionPredicate.SelectorPredicate<Object, String>( predicate, selector ).apply( event );
    }
  }

}

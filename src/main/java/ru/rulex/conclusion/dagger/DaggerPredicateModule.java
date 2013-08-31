package ru.rulex.conclusion.dagger;

import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.AssertionUnit;
import com.google.common.collect.ImmutableMap;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.dagger.PredicateFactory.Factory;

import ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.IntAssertionUnit;
import ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.FloatAssertionUnit;
import ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.StringAssertionUnit;

import ru.rulex.conclusion.dagger.PredicateFactory.LessPredicateFactory;
import ru.rulex.conclusion.dagger.PredicateFactory.MorePredicateFactory;
import ru.rulex.conclusion.dagger.PredicateFactory.MoreOrEqualsPredicateFactory;
import ru.rulex.conclusion.dagger.PredicateFactory.LessOrEqualsPredicateFactory;

import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

 
@dagger.Module(
    injects = { IntAssertionUnit.class, FloatAssertionUnit.class },
    complete = false,
    library = true)
@SuppressWarnings("rawtypes")
public class DaggerPredicateModule
{
  private Comparable<?> value;
  private final Factory factory;
  private final SelectorPipeline selector;
  private static ImmutableMap<LogicOperation, Factory<?>> map;

  static
  {
    ImmutableMap.Builder<LogicOperation, Factory<?>> mapBuilder = ImmutableMap.builder();
    mapBuilder.put( LogicOperation.lessThan, newInstance( LogicOperation.lessThan ) );
    mapBuilder.put( LogicOperation.moreThan, newInstance( LogicOperation.moreThan ) );
    mapBuilder.put( LogicOperation.lessOrEquals, newInstance( LogicOperation.lessOrEquals ) );
    mapBuilder.put( LogicOperation.moreOrEquals, newInstance( LogicOperation.moreOrEquals ) );
    map = mapBuilder.build();
  }

  @SuppressWarnings("unchecked")
  static <T extends Comparable<? super T>, E extends Factory<T>> E newInstance( LogicOperation operation )
  {
    switch (operation)
    {
      case lessThan: return (E) new LessPredicateFactory<T>();
      case moreThan: return (E) new MorePredicateFactory<T>();
      case moreOrEquals: return (E) new MoreOrEqualsPredicateFactory<T>();
      case lessOrEquals: return (E) new LessOrEqualsPredicateFactory<T>();
      default: throw new IllegalArgumentException( "Unsupported operation in DaggerPredicateModule" );
    }
  }

  protected <T extends Comparable<? super T>> DaggerPredicateModule( T value, Factory<?> factory,
      SelectorPipeline selector )
  {
    this.value = value;
    this.factory = factory;
    this.selector = selector;
  }

  public <T extends Comparable<? super T>> DaggerPredicateModule( T value, LogicOperation operation )
  {
    this( value, map.get( operation ), null );
  }

  public <T extends Comparable<? super T>> DaggerPredicateModule( T value, SelectorPipeline selector,
      LogicOperation operation )
  {
    this( value, map.get( operation ), selector );
  }

  @dagger.Provides
  Selector<Object, Integer> intSelector()
  {
    return selector.cast();
  }

  @dagger.Provides
  Selector<Object, Float> floatSelector()
  {
    return selector.cast();
  }

  @dagger.Provides
  Selector<Object, String> stringSelector()
  {
    return selector.cast();
  }


  /**
   * Return ConclusionPredicate with Integer
   * 
   * @return ConclusionPredicate
   */
  @dagger.Provides
  @SuppressWarnings({ "unchecked" })
  ConclusionPredicate<Integer> intPredicate()
  {
    return callOn( ConclusionPredicate.class, 
        ConclusionPredicate.class.cast( factory.createPredicate( value ) ) );
  }

  /**
   * Return ConclusionPredicate with Float
   * @return ConclusionPredicate
   */
  @dagger.Provides
  @SuppressWarnings({ "unchecked" })
  ConclusionPredicate<Float> floatPredicate()
  {
    return callOn( ConclusionPredicate.class,
        ConclusionPredicate.class.cast( factory.createPredicate(  value ) ) );
  }

  public Class<? extends AssertionUnit> getExpressionClass()
  {
    return selector.getExpressionClass();
  }

  @dagger.Provides
  public IntAssertionUnit intExpression(ConclusionPredicate<Integer> predicate, Selector<Object, Integer> selector) {
    return new IntAssertionUnit<Integer>( predicate, selector );
  }

  @dagger.Provides
  public FloatAssertionUnit floatExpression(ConclusionPredicate<Float> predicate, Selector<Object, Float> selector) {
    return new FloatAssertionUnit<Float>( predicate, selector );
  }

  @dagger.Provides
  public StringAssertionUnit stringExpression(ConclusionPredicate<String> predicate, Selector<Object, String> selector) {
    return new StringAssertionUnit<String>( predicate, selector );
  }
}
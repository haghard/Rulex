package ru.rulex.conclusion.dagger;

import dagger.Module;
import dagger.Provides;
import ru.rulex.conclusion.Selector;
import com.google.common.collect.ImmutableMap;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.dagger.PredicateCreators.PredicateCreator;
import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;
import ru.rulex.conclusion.dagger.PredicateCreators.LessPredicateCreator;
import ru.rulex.conclusion.dagger.PredicateCreators.MorePredicateCreator;

@Module(
    injects = ConclusionPredicate.class,
    library = true)
@SuppressWarnings("rawtypes")
public final class DaggerUnit
{
  private static ImmutableMap<LogicOperation, PredicateCreator> map;
  private final ValueSuppler<? extends Comparable<?>> suppler = Generefier.INSTANCE.generify();
  private PredicateCreator builder;
  private Comparable<?> value;
  private Selector<Object, Comparable> selector;

  static
  {
    ImmutableMap.Builder<LogicOperation, PredicateCreator> mapBuilder = ImmutableMap.builder();
    mapBuilder.put( LogicOperation.lessThan, builderInstance( LogicOperation.lessThan ) );
    mapBuilder.put( LogicOperation.moreThan, builderInstance( LogicOperation.moreThan ) );
    map = mapBuilder.build();
  }

  @SuppressWarnings("unchecked")
  static <T extends Comparable<? super T>, E extends PredicateCreator<T>> E builderInstance(
      LogicOperation operation )
  {
    switch (operation)
    {
    case lessThan:
      return (E) new LessPredicateCreator<T>();
    case moreThan:
      return (E) new MorePredicateCreator<T>();
    default:
      throw new IllegalArgumentException( "Invalid operation" );
    }
  }

  protected <T> DaggerUnit( Comparable<?> value, PredicateCreator<?> builder, Selector<Object, T> selector )
  {
    this.value = value;
    this.builder = builder;
    this.selector = (Selector<Object, Comparable>) selector;
  }

  public <T extends Comparable> DaggerUnit( T pvalue, Selector<Object, T> selector, LogicOperation operation )
  {
    this( pvalue, map.get( operation ), selector );
  }

  protected DaggerUnit less(Comparable<?> value, PredicateCreator<?> builder)
  {
    return new DaggerUnit(value, builder, selector);
  }

  @Provides
  @SuppressWarnings({"unchecked" })
  ConclusionPredicate predicate()
  {
    ConclusionPredicate conclusionPredicate = ConclusionPredicate.class.cast( builder.createPredicate( suppler.supply( value ) ) );
    return new SelectorPredicate( conclusionPredicate, selector );
  }
}
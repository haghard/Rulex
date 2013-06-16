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
import ru.rulex.conclusion.dagger.PredicateCreators.MoreOrEqualsPredicateCreator;
import ru.rulex.conclusion.dagger.PredicateCreators.LessOrEqualsPredicateCreator;

import static dagger.ObjectGraph.create;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

@Module(
    injects = ConclusionPredicate.class,
    library = true)
@SuppressWarnings("rawtypes")
public final class DaggerPredicateModule
{
  private static ImmutableMap<LogicOperation, PredicateCreator<?>> map;
  private final ValueSuppler<? extends Comparable<?>> suppler = Generefier.INSTANCE.generify();
  private PredicateCreator builder;
  private Comparable<?> value;
  private Selector<Object, Comparable<?>> selector;

  static
  {
    ImmutableMap.Builder<LogicOperation, PredicateCreator<?>> mapBuilder = ImmutableMap.builder();
    mapBuilder.put( LogicOperation.lessThan, builderInstance( LogicOperation.lessThan ) );
    mapBuilder.put( LogicOperation.moreThan, builderInstance( LogicOperation.moreThan ) );
    mapBuilder.put( LogicOperation.lessOrEquals, builderInstance( LogicOperation.lessOrEquals ) );
    mapBuilder.put( LogicOperation.moreOrEquals, builderInstance( LogicOperation.moreOrEquals ) );
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
    case moreOrEquals:
      return (E) new MoreOrEqualsPredicateCreator<T>();
    case lessOrEquals:
      return (E) new LessOrEqualsPredicateCreator<T>();
    default:
      throw new IllegalArgumentException( "Invalid operation" );
    }
  }

  protected <T extends Comparable<? super T>> DaggerPredicateModule( Comparable<?> value, PredicateCreator<?> builder, 
      Selector<Object, Comparable<?>> selector )
  {
    this.value = value;
    this.builder = builder;
    this.selector = selector;
  }

  public <T extends Comparable<? super T>> DaggerPredicateModule( T value, Selector<Object,
      Comparable<?>> selector, LogicOperation operation )
  {
    this( value, map.get( operation ), selector );
  }

  @Provides
  @SuppressWarnings({"unchecked" })
  ConclusionPredicate predicate()
  {
    final ConclusionPredicate conclusionPredicate = callOn( ConclusionPredicate.class,
        ConclusionPredicate.class.cast(builder.createPredicate(suppler.supply(value))));
    return new SelectorPredicate( conclusionPredicate, selector );
  }
}
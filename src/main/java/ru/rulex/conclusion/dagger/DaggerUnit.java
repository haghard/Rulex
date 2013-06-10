package ru.rulex.conclusion.dagger;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.dagger.PredicateCreators.PredicateCreator;
import ru.rulex.conclusion.dagger.PredicateCreators.LessCreator;
import ru.rulex.conclusion.dagger.PredicateCreators.MoreCreator;
import com.google.common.collect.ImmutableMap;

import dagger.Module;
import dagger.Provides;

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
      return (E) new LessCreator<T>();
    case moreThan:
      return (E) new MoreCreator<T>();
    default:
      throw new IllegalArgumentException( "Invalid operation" );
    }
  }

  public static <T extends Comparable<? super T>> DaggerUnit less( T value )
  {
    return new DaggerUnit( value, map.get( LogicOperation.lessThan ) );
  }

  public static <T extends Comparable<? super T>> DaggerUnit more( T value )
  {
    return new DaggerUnit( value, map.get( LogicOperation.moreThan ) );
  }

  private DaggerUnit( Comparable<?> value, PredicateCreator<?> builder )
  {
    this.value = value;
    this.builder = builder;
  }

  @Provides
  @SuppressWarnings({"unchecked" })
  ConclusionPredicate predicate()
  {
    return ConclusionPredicate.class.cast( builder.createPredicate( suppler.supply( value ) ) );
  }

  public static <T> DaggerUnit less( final T pvalue, final T argument )
  {
    // TODO: implement this
    return null;
  }

  public static <T> DaggerUnit more( final T pvalue, final T argument )
  {
    // TODO: implement this
    return null;
  }
}
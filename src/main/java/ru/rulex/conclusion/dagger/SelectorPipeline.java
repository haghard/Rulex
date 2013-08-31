package ru.rulex.conclusion.dagger;

import java.util.ArrayDeque;
import java.util.Queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import ru.rulex.conclusion.AssertionUnit;
import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.StringAssertionUnit;
import ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.FloatAssertionUnit;
import ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.IntAssertionUnit;

@SuppressWarnings("rawtypes")
public enum SelectorPipeline implements Selector<Object, Object>
{
  INSTANCE
  {

    // Map for association Class MethodName
    final ImmutableMap<String, Class<? extends AssertionUnit>> methodToUnitMapping = ImmutableMap.of(
            "getInteger", IntAssertionUnit.class,
            "getOtherInteger", IntAssertionUnit.class,
            "getFloat", FloatAssertionUnit.class,
            "getString", StringAssertionUnit.class );

    final Queue<Class<? extends AssertionUnit>> classesQueue = new ArrayDeque<Class<? extends AssertionUnit>>();

    final Queue<Selector> selectorsQueue = new ArrayDeque<Selector>();

    @Override
    @SuppressWarnings("unchecked")
    public Object select( Object argument )
    {
      final Selector selector = selectorsQueue.poll();
      Preconditions.checkNotNull( selector );
      return selector.select( argument );
    }

    @Override
    public <T, E> void setDelegate( Selector<T, E> delegate )
    {
      selectorsQueue.offer( delegate );
    }

    public Class<? extends AssertionUnit> getExpressionClass()
    {
      return classesQueue.poll();
    }

    @Override
    public void setExpressionClass( String methodName )
    {
      classesQueue.offer( methodToUnitMapping.get( methodName ) );
    }
  };

  public abstract <T, E> void setDelegate( Selector<T, E> delegate );

  public abstract Class<? extends AssertionUnit> getExpressionClass();

  public abstract void setExpressionClass( String methodName );

  @SuppressWarnings("unchecked")
  <E, T> Selector<E, T> cast()
  {
    return (Selector<E, T>) this;
  }
}
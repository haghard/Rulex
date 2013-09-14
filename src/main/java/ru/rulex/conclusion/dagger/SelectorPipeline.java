package ru.rulex.conclusion.dagger;

import java.util.ArrayDeque;
import java.util.Queue;

import ru.rulex.conclusion.Selector;
import com.google.common.base.Preconditions;

public enum SelectorPipeline implements Selector<Object, Object>
{
  PIPELINE_INSTANCE
  {

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
  };

  public abstract <T, E> void setDelegate( Selector<T, E> delegate );

  @SuppressWarnings("unchecked")
  public <E, T> Selector<E, T> cast()
  {
    return (Selector<E, T>) this;
  }
}
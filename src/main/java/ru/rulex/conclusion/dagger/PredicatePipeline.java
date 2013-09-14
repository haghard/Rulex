package ru.rulex.conclusion.dagger;

import com.google.common.base.Preconditions;
import ru.rulex.conclusion.ConclusionPredicate;

import java.util.ArrayDeque;
import java.util.Queue;


public enum PredicatePipeline implements ConclusionPredicate<Object>
{
  PIPELINE_INSTANCE
  {
    final Queue<ConclusionPredicate> selectorsQueue = new ArrayDeque<ConclusionPredicate>();

    @Override
    public boolean apply( Object argument )
    {
      final ConclusionPredicate predicate = selectorsQueue.poll();
      Preconditions.checkNotNull( predicate );
      return predicate.apply( argument );
    }

    @Override
    public <T> void setDelegate( ConclusionPredicate<T> delegate )
    {
      selectorsQueue.offer( delegate );
    }
  };

  public abstract <T> void setDelegate( ConclusionPredicate<T> delegate );

  @SuppressWarnings( "unchecked" )
  public <T> ConclusionPredicate<T> cast()
  {
    return ( ConclusionPredicate<T> ) this;
  }
}

package ru.rulex.conclusion;

/**
 * 
 * @author haghard
 *
 * @param <T>
 * 
 */
public interface MutableAssertionUnit<T> extends ImmutableAssertionUnit<T>
{

  void setVar(String varName);

  String getVar();

  void setSelector( Selector<T, ?> selector );

  public enum DefaultMutableAssertionUnit implements MutableAssertionUnit<Object> {
    INSTANCE;

    @Override
    public void setVar( String varName )
    {
    	throw new UnsupportedOperationException("");
    }

    @Override
    public String getVar()
    {
    	throw new UnsupportedOperationException("");
    }

    @Override
    public void setSelector( Selector<Object, ?> selector )
    {
    	throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
    {
    	throw new UnsupportedOperationException("");
    }

    @SuppressWarnings("unchecked")
	public <T> MutableAssertionUnit<T> withNarrowType()
    {
      return (MutableAssertionUnit<T>)this;
    }
  }
}

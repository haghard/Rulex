package ru.rulex.conclusion;


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

    }

    @Override
    public String getVar()
    {
      return null;
    }

    @Override
    public void setSelector( Selector<Object, ?> selector )
    {

    }

    @Override
    public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
    {
      return false;
    }

    public <T> MutableAssertionUnit<T> withNarrowType()
    {
      return (MutableAssertionUnit<T>)this;
    }
  }
}

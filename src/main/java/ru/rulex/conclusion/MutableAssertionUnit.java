package ru.rulex.conclusion;


public interface MutableAssertionUnit<T> extends ImmutableAssertionUnit<T>
{

  void setVar(String varName);

  void setSelector( Selector<T, ?> selector );
}

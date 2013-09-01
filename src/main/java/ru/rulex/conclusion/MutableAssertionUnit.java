package ru.rulex.conclusion;


public interface MutableAssertionUnit<T> extends AssertionUnit<T>
{

  void setVar(String varName);

  void setSelector( Selector<T, ?> selector );
}

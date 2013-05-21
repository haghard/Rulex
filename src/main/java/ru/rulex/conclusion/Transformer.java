package ru.rulex.conclusion;

public interface Transformer<T, E>
{
  E transform( T argument );
}

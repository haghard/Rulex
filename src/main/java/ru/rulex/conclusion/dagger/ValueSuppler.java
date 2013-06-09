package ru.rulex.conclusion.dagger;
/**
 * 
 * @author haghard
 *
 * @param <T>
 */
public interface ValueSuppler<T>
{
  T supply( Object value );
}

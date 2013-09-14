package ru.rulex.conclusion.dagger;

import dagger.ObjectGraph;

interface ExpressionBuilder<T>
{
  ObjectGraph eq( final T argument );
  ObjectGraph less( final T argument );
  ObjectGraph more( final T argument );
  ObjectGraph lessOrEquals ( T argument );
  ObjectGraph moreOrEquals ( T argument );
}
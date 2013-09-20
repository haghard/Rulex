package ru.rulex.conclusion.dagger;

import dagger.ObjectGraph;

interface ExpressionBuilder<T>
{
  ObjectGraph eq(T value);
}
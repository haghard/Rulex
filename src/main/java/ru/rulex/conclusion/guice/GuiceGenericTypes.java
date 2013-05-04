/*
 * Copyright (C) 2013 The Conclusions Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.rulex.conclusion.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class GuiceGenericTypes {

  @SuppressWarnings("unchecked")
  public static <T, E> TypeLiteral<E> newGenericType(Class<E> genericType, TypeLiteral<T> literal) {
    Type newType = Types.newParameterizedType(genericType, literal.getType());
    return (TypeLiteral<E>) TypeLiteral.get(newType);
  }

  @SuppressWarnings("unchecked")
  public static <T, E> TypeLiteral<E> newEnclosedGenericType(Class<E> genericType, TypeLiteral<T> literal) {
    Type newType =
        Types.newParameterizedTypeWithOwner(GuicefyConclusionPredicates.class, genericType, literal.getType());
    return (TypeLiteral<E>) TypeLiteral.get(newType);
  }

  @SuppressWarnings("unchecked")
  public static <T> TypeLiteral<ArrayList<T>> arrayListOf(final Class<T> parameterType) {
    Type paramType = Types.newParameterizedType(List.class, parameterType);
    return (TypeLiteral<ArrayList<T>>) TypeLiteral.get(paramType);
  }

  @SuppressWarnings("unchecked")
  public static <T> TypeLiteral<ImmutableList<T>> immutableListOf(final Class<T> parameterType) {
    Type paramType = Types.newParameterizedType(ImmutableList.class, parameterType);
    return (TypeLiteral<ImmutableList<T>>) TypeLiteral.get(paramType);
  }

}

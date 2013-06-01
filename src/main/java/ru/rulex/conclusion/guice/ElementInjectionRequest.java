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

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

public interface ElementInjectionRequest extends Runnable
{

  void setBinder( Binder binder );

  TypeLiteral<?> getLiteral();

  Matcher<Object> matcher();

  String description();
}
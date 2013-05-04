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
package ru.rulex.external;

import groovy.lang.Closure;
/**
 * 
 * This adaptor allows 'groovy.lang.Closure' functions 
 * to be used and will know how to invoke them
 *
 */
public class GroovyLanguageAdapter implements JvmBasedLanguageAdapter {

  @Override
  public Object call(Object function, Object[] args) {
    return ((Closure<?>) function).call(args);
  }

  @Override
  public Class<?> getFunctionClass() {
    return Closure.class;
  }
}

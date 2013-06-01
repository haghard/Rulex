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

public interface JvmBasedLanguageAdapter
{

  /**
   * Invoke the function and return the results.
   * 
   * @param function
   * @param args
   * @return Object results from function execution
   */
  Object call( Object function, Object[] args );

  /**
   * The Class of the Function that this adaptor serves.
   * <p>
   * Example: groovy.lang.Closure
   * <p>
   * This should not return classes of java.* packages.
   * 
   * @return Class of classes that this adaptor should be invoked for.
   */
  public Class<?> getFunctionClass();
}

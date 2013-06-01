/*
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file without in compliance with the License. You may obtain a copy
 * of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.rulex.conclusion;

/**
 * A interface that can supply select needed value of type <code>E</code> from
 * object of type <code>T</code>. Practically, this could be a just anonymous
 * inner class with predefined <code>T, E</code> Anonymous inner example:
 * 
 * <pre class="code">
 * <code class="java">
 *  new Selector<Entity, Integer>() {
 *    Integer select(Entity entity){
 *      entity.getValue();
 *    }
 *  }
 * }
 * </code>
 * </pre>
 * 
 * Reflection example:
 * 
 * <pre class="code">
 * <code class="java">
 *  new Selector<T, E>() {
 *    public E select(T input) {
 *      E returnValue = null;
 *      try {
 *        final Method reflectionMethod = clazz.getDeclaredMethod(method);
 *        returnValue = (E) reflectionMethod.invoke(input);
 *      } catch (Exception e) {
 *        throw new RuntimeException("reflection error");
 *      }
 *      return returnValue;
 *    }
 *  };
 * }
 * </code>
 * </pre>
 * 
 * </p>
 */
public interface Selector<T, E> extends FunctionalInterface
{

  /**
   * @param argument
   * @return E
   */
  E select( T argument );
}

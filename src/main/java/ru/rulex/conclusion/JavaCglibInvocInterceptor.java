/*
 * Copyright 2013 Project Forward Conclusion Contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.rulex.conclusion;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
/**
 *
 * Base on {@code InvocationInterceptor} from lambdaJ
 * An interceptor that manages invocations on both a native Java proxy and a cglib one.
 *
 */
public abstract class JavaCglibInvocInterceptor implements MethodInterceptor,
    java.lang.reflect.InvocationHandler {

  public final Object intercept(Object proxy, Method method, Object[] args,
      MethodProxy methodProxy) throws Throwable {
    return invoke(proxy, method, args);
  }

}
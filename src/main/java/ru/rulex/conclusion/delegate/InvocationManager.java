/*
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.rulex.conclusion.delegate;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
/**
 * 
 * @author haghard
 *
 */
public class InvocationManager {

  //private final Logger LOG = Logger.getLogger( getClass() );

  private Queue<Invokable<?, ?>> invokableList = new ArrayDeque<Invokable<?, ?>>();

  public void pushInvokable(Invokable<?, ?> invokable) {
    this.invokableList.offer(invokable);
  }

  public Invokable<?, ?> poolInvokable() {
    Invokable<?, ?> invokable = invokableList.poll();
    Preconditions.checkNotNull(invokable, "invokable was not setted in InvocationManager");
    return invokable;
  }
}

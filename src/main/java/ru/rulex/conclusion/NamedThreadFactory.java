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
package ru.rulex.conclusion;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory
    implements ThreadFactory {

  private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

  private final ThreadGroup group;

  private final AtomicInteger threadNumber = new AtomicInteger(1);

  private final String namePrefix;

  public NamedThreadFactory() {
    this("ThreadPool(" + POOL_NUMBER.getAndIncrement() + "-thread-");
  }

  public NamedThreadFactory(String namePrefix) {
    SecurityManager s = System.getSecurityManager();
    this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    this.namePrefix = namePrefix + "(pool" + POOL_NUMBER.getAndIncrement() + "-thread-";
  }

  public Thread newThread(Runnable r) {
    Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement() + ")", 0L);
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }
}

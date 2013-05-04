/**
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

public class ConclusionStatePathTrace {

  ConclusionState conclusionState = ConclusionState.InitialState;

  private ConclusionStatePathTrace() {
  }

  public static ConclusionStatePathTrace defaultInstance() {
    return new ConclusionStatePathTrace();
  }

  public void addBlockingError(String format) {
    conclusionState = ConclusionState.CriticalErrorState;
  }

  public static String getStackTrace(Throwable th) {
    StringBuffer buffer = new StringBuffer();
    return buffer.toString();
  }

  public ConclusionState getCurrentState() {
    return conclusionState;
  }

  public boolean isWorkingState() {
    return conclusionState != ConclusionState.CriticalErrorState;
  }

  public enum ConclusionState {

    InitialState("init"),
    CriticalErrorState("critical-error");

    private final String message;

    ConclusionState(String message) {
      this.message = message;
    }
  }
}

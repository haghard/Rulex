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

import java.util.ResourceBundle;

public class PhraseExecutionException extends CodedException {

  private static final ResourceBundle ERROR_TABLE = ResourceBundle
      .getBundle(PhraseExecutionException.class.getName());

  public PhraseExecutionException(ErrorCode code, String[] fields,
      Exception exception) {
    super(code, fields, exception);
    initResource();
  }

  public PhraseExecutionException(ErrorCode code, String field,
      Throwable exception) {
    super(code, field, exception);
    initResource();
  }

  public PhraseExecutionException(ErrorCode code, Throwable exception) {
    super(code, exception);
    initResource();
  }

  public PhraseExecutionException(ErrorCode code, Exception exception) {
    super(code, exception);
    initResource();
  }
  
  public PhraseExecutionException(ErrorCode code, String field) {
    super(code, field);
    initResource();
  }

  public PhraseExecutionException(ErrorCode code, String[] fields) {
    super(code, fields);
    initResource();
  }

  public PhraseExecutionException(ErrorCode code) {
    super(code);
    initResource();
  }

  /**
   * Set the resource bundle table for this subclass of CodedException.
   */
  private void initResource() {
    errors = ERROR_TABLE;
  }
}

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

import java.sql.BatchUpdateException;
import java.sql.SQLException;

/**
 * This is a utility class which returns a stack trace as a string. Helpful
 * when, for example, you would like to report the trace in a REST xml response.
 * 
 */
public final class StackTrace
{

  /**
   * Given an exception, return the stack trace in a single string.
   * 
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter) Report a
   *      stack trace in a string.
   * @param exception
   * @return
   */
  public static String getStrackTrace( final Throwable exception )
  {
    StringBuffer buffer = new StringBuffer();
    Throwable cause = exception;
    while (cause != null)
    {
      StackTraceElement[] elements = cause.getStackTrace();
      buffer.append( cause.getClass().getCanonicalName() );
      for (StackTraceElement element : elements)
      {
        buffer.append( "      at " );
        buffer.append( element.toString() );
        buffer.append( '\n' );
      }
      // todo for batch sql exception
      if (cause instanceof BatchUpdateException)
      {
        getErrorInfo( buffer, (BatchUpdateException) cause );
      }
      if (cause instanceof SQLException)
      {
        cause = ((SQLException) cause).getNextException();
      }
      else
      {
        cause = cause.getCause();
      }

      if (cause != null)
      {
        buffer.append( "Caused by:\n" );
      }
    }

    return buffer.toString();
  }

  private static void getErrorInfo( StringBuffer buffer, BatchUpdateException error )
  {
    if (buffer != null)
    {
      buffer.append( "Database Error Details:\n" );
      buffer.append( "   Error Code: " );
      buffer.append( error.getErrorCode() );
      buffer.append( "\n" );
      buffer.append( "   State: " );
      buffer.append( error.getSQLState() );
      buffer.append( "\n" );
      buffer.append( "   Batch Update Counts: " );
      for (int count : error.getUpdateCounts())
      {
        buffer.append( "\n" );
        buffer.append( count );
      }
    }
  }
}

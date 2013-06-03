package ru.rulex.conclusion;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author haghard
 * 
 */
public abstract class CodedException extends Exception
{

  /**
   * The error code for the specific error.
   */
  protected ErrorCode error;

  /**
   * Any fields which would need to be filled in within the error message.
   */
  protected String[] errorFields;

  /**
   * Aggregate of errors.
   */
  protected List<String> issues = new ArrayList<String>();

  /**
   * The shadow handle to the resource bundle. Since statics can only be
   * shadowed and not overridden, this provides the mechanism needed to access a
   * subclasses resource bundle from a super-class non-static method.
   */
  protected transient ResourceBundle errors = null;

  /**
   * Empty Constructor. Required for subclasses.
   */
  public CodedException()
  {
    super( "" );
    init( null, null );
  }

  /**
   * An exception with a specific error code.
   * 
   * @param code
   *          The code key used to look up the error text.
   */
  public CodedException( final ErrorCode code )
  {
    super( "" );
    init( code, null );
  }

  /**
   * An exception with an error code and fields in the message.
   * 
   * @param code
   *          The code key used to look up the error text.
   * @param fields
   *          An array of field values to replace fields in the error message.
   */
  public CodedException( final ErrorCode code, final String[] fields )
  {
    super( "" );
    init( code, fields );
  }

  /**
   * An exception with an error code and fields in the message.
   * 
   * @param code
   *          The code key used to look up the error text.
   * @param issues
   *          An array of error messages.
   */
  public CodedException( final ErrorCode code, final List<String> issues )
  {
    super( "" );
    this.issues = issues;
    init( code, null );
  }

  /**
   * An exception with an error code and fields in the message.
   * 
   * @param code
   *          The code key used to look up the error text.
   * @param field
   *          A single value with which to replace the error message field.
   */
  public CodedException( final ErrorCode code, final String field )
  {
    super( "" );
    String[] fields = new String[1];
    fields[0] = field;
    init( code, fields );
  }

  /**
   * Creates a new Coded Exception, given an error code and root cause.
   * 
   * @param code
   *          The Error of this particular exception.
   * @param exception
   *          The root cause of the problem.
   */
  public CodedException( final ErrorCode code, final Throwable exception )
  {
    super( "", (Exception) exception );
    init( code, null );
  }

  /**
   * Creates a coded exception with fields and a root cause.
   * 
   * @param code
   *          The code key used to look up the error text.
   * @param field
   *          A single value with which to replace the error message field.
   * @param exception
   *          The source of the error.
   */
  public CodedException( final ErrorCode code, final String field, final Throwable exception )
  {
    super( "", (Exception) exception );
    String[] fields = new String[1];
    fields[0] = field;
    init( code, fields );
  }

  /**
   * Creates a coded exception with fields and a root cause.
   * 
   * @param code
   *          The code key used to look up the error text.
   * @param fields
   *          An array of field values to replace fields in the error message.
   * @param exception
   *          The source of the error.
   */
  public CodedException( final ErrorCode code, final String[] fields, final Throwable exception )
  {
    super( "", (Exception) exception );
    init( code, fields );
  }

  /**
   * Initialize the instance.
   * 
   * @param code
   *          The code key used to look up the error text.
   * @param fields
   *          An array of field values to replace fields in the error message.
   */
  protected final void init( final ErrorCode code, final String[] fields )
  {
    if ( code != null )
    {
      error = code;
    }
    errorFields = fields;
  }

  /**
   * <code>getMessage</code> - Check the error code to see if it's an
   * <code>ExtendedError</code>. Based on the result, pull the error string from
   * the appropriate resource bundle (business logic errors or general CAM
   * errors).
   * 
   * @return a <code>String</code> value
   */
  public final String getMessage()
  {
    String message;
    // determine if it's a valid extended error.
    if ( (errors != null) && (error != null) )
    {
      try
      {
        message = errors.getString( error.toString() ); // use the subclass
                                                        // resource bundle

        // if there are error fields, fill them in.
        if ( errorFields != null )
        {
          message = MessageFormat.format( message, (Object[]) errorFields );
        }
      }
      catch (MissingResourceException e)
      {
        message = "An undefined error occurred. The id is " + error.toString();
      }

    }
    else
    {
      message = "An unknown error occurred.";
    }
    return message;
  }

  /**
   * Given an exception, return the stack trace in a single string.
   * 
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter) Report a
   *      stack trace in a string.
   * @return
   */
  public String getStrackTrace()
  {
    return StackTrace.getStrackTrace( this );
  }

  public boolean hasIssues()
  {
    return !issues.isEmpty();
  }

  /**
   * Returns a String representation of all the issues
   * 
   * @return
   */
  public String getIssuesAsString()
  {
    StringBuffer sb = new StringBuffer();
    for (String issue : issues)
    {
      sb.append( issue );
      sb.append( "\n" );
    }
    if ( issues.isEmpty() )
    {
      sb.append( getMessage() );
    }
    return sb.toString();
  }

  /**
   * @return the issues
   */
  public List<String> getIssues()
  {
    return issues;
  }
}

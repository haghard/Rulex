package ru.rulex.conclusion.dagger;
/**
 * 
 * @author haghard
 * 
 */
public enum LogicOperation
{
  lessThan("less", OperationType.Binary)
  {
    @Override boolean eval(int result) { return result < 0; }
  },

  lessOrEquals("lessOrEq", OperationType.Binary)
  {
    @Override boolean eval(int result) { return result <= 0; }
  },

  moreThan("moreThan", OperationType.Binary)
  {
    @Override boolean eval(int result) { return result > 0; }
  },

  moreOrEquals("moreOrEquals", OperationType.Binary)
  {
    @Override boolean eval(int result) { return result >= 0; }
  };

  private final String name;
  private final OperationType operationType;

  LogicOperation( String name, OperationType operationType )
  {
    this.name = name;
    this.operationType = operationType;
  }

  public String getName()
  {
    return name;
  }

  public OperationType getOperationType()
  {
    return operationType;
  }

  public enum OperationType
  {
    Unary, Binary
  }

  public static LogicOperation parse( String alias )
  {
    for ( LogicOperation operation : values() )
      if ( operation.name.equalsIgnoreCase( alias ) )
        return operation;

    throw new IllegalArgumentException( "LogicOperation not found by alias: " + alias );
  }

  abstract boolean eval(int result);
}

package ru.rulex.conclusion.dagger;
/**
 * 
 * @author haghard
 * 
 */
public enum LogicOperation
{

  eq("eq", OperationType.Binary),

  lessThan("less", OperationType.Binary),

  lessOrEquals("lessOrEq", OperationType.Binary),

  moreThan("moreThan", OperationType.Binary),

  moreOrEquals("moreOrEquals", OperationType.Binary),

  matchAnyOff("matchAnyOff", OperationType.Binary);

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

}

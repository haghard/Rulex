package ru.rulex.conclusion.groovy;

import java.math.BigDecimal;

public class TradeEvent
{
  private Integer objectId;
  private Integer eventType;
  private BigDecimal eventPrice;
  private String eventName;

  public TradeEvent(){}

  public TradeEvent( Integer objectId, Integer eventType, BigDecimal eventPrice, String eventName )
  {
    this.objectId = objectId;
    this.eventType = eventType;
    this.eventPrice = eventPrice;
    this.eventName = eventName;
  }

  public Integer objectId()
  {
    return objectId;
  }

  public Integer eventType()
  {
    return eventType;
  }

  public BigDecimal eventPrice()
  {
    return eventPrice;
  }

  public String eventName()
  {
    return eventName;
  }
}

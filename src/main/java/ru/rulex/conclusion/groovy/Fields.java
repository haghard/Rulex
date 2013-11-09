package ru.rulex.conclusion.groovy;

import java.math.BigDecimal;

import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public enum Fields
{
  objectId()
  {
    @Override public Integer eval() {
      return callOn( TradeEvent.class ).objectId();
    }
  },

  eventType()
  {
    @Override public Integer eval() {
      return callOn( TradeEvent.class ).eventType();
    }
  },

  objectPrice()
  {
    @Override public BigDecimal eval() {
      return callOn( TradeEvent.class ).eventPrice();
    }
  },

  objectName()
  {
    @Override public String eval()
    {
      return callOn( TradeEvent.class ).eventName();
    }
  };

  public abstract <T extends Comparable<? super T>> T eval();
}

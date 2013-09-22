package ru.rulex.conclusion.groovy;

import ru.rulex.conclusion.delegate.ProxyUtils;

import java.math.BigDecimal;

public enum Fields
{

  objectId()
  {
    @Override public Integer eval() {
      return ProxyUtils.callOn( TradeEvent.class ).objectId();
    }
  },

  eventType()
  {
    @Override public Integer eval() {
      return ProxyUtils.callOn( TradeEvent.class ).eventType();
    }
  },

  objectPrice()
  {
    @Override public BigDecimal eval() {
      return ProxyUtils.callOn( TradeEvent.class ).eventPrice();
    }
  },

  objectName()
  {
    @Override public String eval()
    {
      return ProxyUtils.callOn( TradeEvent.class ).eventName();
    }
  };

  public abstract <T extends Comparable<? super T>> T eval();

}

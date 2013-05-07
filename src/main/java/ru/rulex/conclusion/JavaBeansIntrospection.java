package ru.rulex.conclusion;

import static java.lang.String.format;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.fest.util.IntrospectionError;
/**
 * 
 * @author haghard
 * 
 * Experemental class
 * 
 */
public final class JavaBeansIntrospection {

  public static <T> PropertyDescriptor getProperty(String propertyName, T target) {
    checkArgument(!isNullOrEmpty(propertyName));
    checkNotNull(target);
    BeanInfo javaBeanInfo = null;
    Class<?> type = target.getClass();
    try {
      javaBeanInfo = Introspector.getBeanInfo(type);
    } catch (Throwable t) {
      throw new IntrospectionError(format("Unable to get BeanInfo for type %s", type.getName()), t);
    }

    for (PropertyDescriptor descriptor : javaBeanInfo.getPropertyDescriptors()) {
      if (propertyName.equals(descriptor.getName())) {
        return descriptor;
      }
    }
    throw new IllegalArgumentException(format("propertyNotFoundErrorMessage %s", propertyName));
  }

  public static void main(String[] args) {
    try {
      Model model = Model.values(211);
      PropertyDescriptor pDescriptor = JavaBeansIntrospection.getProperty("integer", model);
      System.out.print(pDescriptor.getReadMethod().invoke(model));
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}

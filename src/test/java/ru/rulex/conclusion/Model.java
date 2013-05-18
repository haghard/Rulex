/*
 * Copyright (C) 2013 The Conclusions Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.rulex.conclusion;

import static org.mockito.Mockito.*;

/**
 * @author haghard
 * @param <T>
 */
public abstract class Model
{
   
   public abstract Integer getInteger();
   
   public abstract Integer getOtherInteger();
   
   public abstract Float getFloat();
   
   public abstract String getString();
   
   public abstract Boolean getBoolean();
   
   public static final String INT_ACCESSOR = "getInteger";
   
   public static final String FLOAT_ACCESSOR = "getFloat";
   
   public static final String STRING_ACCESSOR = "getString";
   
   public static final String BOOLEAN_ACCESSOR = "getBoolean";
   
   public static Model values( Integer intValue )
   {
      Model mock = mock( Model.class );
      when( mock.getInteger() ).thenReturn( intValue );
      return mock;
   }
   
   public static Model values( int i, int f )
   {
      Model mock = mock( Model.class );
      when( mock.getInteger() ).thenReturn( i );
      when( mock.getOtherInteger() ).thenReturn( f );
      return mock;
   }
   
   public static Model values( int i, float f )
   {
      Model mock = mock( Model.class );
      when( mock.getInteger() ).thenReturn( i );
      when( mock.getFloat() ).thenReturn( f );
      return mock;
   }
   
   public static Model values( int i, float f, String str )
   {
      Model mock = mock( Model.class );
      when( mock.getInteger() ).thenReturn( i );
      when( mock.getFloat() ).thenReturn( f );
      when( mock.getString() ).thenReturn( str );
      return mock;
   }
   
   public static Model values( int i, float f, String str, boolean b )
   {
      Model mock = mock( Model.class );
      when( mock.getInteger() ).thenReturn( i );
      when( mock.getFloat() ).thenReturn( f );
      when( mock.getString() ).thenReturn( str );
      when( mock.getBoolean() ).thenReturn( b );
      return mock;
   }
}

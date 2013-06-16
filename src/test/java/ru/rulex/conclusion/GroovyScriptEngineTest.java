/*
 * Copyright (C) 2013 The Conclusions Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.rulex.conclusion;

import java.io.File;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.*;

public class GroovyScriptEngineTest
{

  String[] roots =
  { "./groovy-script" };

  @Test
  public void testRunLocalGuiceBasedScriptsAll()
  {
    GroovyShell groovyShell = new GroovyShell();
    try
    {
      groovyShell.evaluate( new File( "./groovy-script/GroovyExampleAll.groovy" ) );
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "testRunLocalGuiceBasedScriptsAll error !!!" );
    }
  }

  @Test
  public void testRunLocalGuiceBasedScripts()
  {
    try
    {
      GroovyScriptEngine gse = new GroovyScriptEngine( roots );
      Model foo = Model.values( 91, 109.90f );

      Binding binding = new Binding();
      binding.setVariable( "foo", foo );
      gse.run( "GroovyExample.groovy", binding );
      assertTrue("testRunLocalGuiceScripts error !!!",
              (Boolean) binding.getVariable( "output" ));
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "testRunLocalGuiceBasedScripts error !!!" );
    }
  }

  @Test
  public void testNativeRunLocalGuiceBasedScripts()
  {
    try
    {
      GroovyScriptEngine gse = new GroovyScriptEngine( roots );
      Model foo = Model.values( 11, 89.49f, "asd" );

      Binding binding = new Binding();
      binding.setVariable( "foo", foo );
      gse.run( "SingleEventGroovyScript.groovy", binding );
      assertTrue("testNativeRunLocalGuiceBasedScripts error !!!",
              (Boolean) binding.getVariable( "output" ));
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "testNativeRunLocalGuiceBasedScripts error !!!" );
    }
  }

  @Test
  public void testListRunLocalGuiceBasedScripts()
  {
    try
    {
      int targetId = 128;
      final ImmutableList<Model> list = ImmutableList.of( Model.values( 121 ), Model.values( 122 ),
          Model.values( targetId ) );

      GroovyScriptEngine gse = new GroovyScriptEngine( roots );
      Binding binding = new Binding();
      binding.setVariable( "list", list );
      binding.setVariable( "value", targetId );
      gse.run( "ListGroovyScript.groovy", binding );
      assertTrue( "testListRunLocalGuiceBasedScripts null error !!!",
          (Boolean) binding.getVariable( "output" ) );

      binding.setVariable( "list", list );
      binding.setVariable( "value", targetId + 1 );
      gse.run( "ListGroovyScript.groovy", binding );
      assertTrue( "testListRunLocalGuiceBasedScripts null error !!!",
          (Boolean) binding.getVariable( "output" ) );

    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail( "testListRunLocalGuiceBasedScripts error !!!" );
    }
  }
}

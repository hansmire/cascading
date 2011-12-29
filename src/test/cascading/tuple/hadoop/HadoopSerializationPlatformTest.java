/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.tuple.hadoop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cascading.PlatformTestCase;
import cascading.test.HadoopPlatform;
import cascading.test.PlatformRunner;
import cascading.tuple.Tuple;
import cascading.tuple.TupleInputStream;
import cascading.tuple.TupleOutputStream;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Test;

/**
 *
 */
@PlatformRunner.Platform({HadoopPlatform.class})
public class HadoopSerializationPlatformTest extends PlatformTestCase
  {
  public HadoopSerializationPlatformTest()
    {
    }

  @Test
  public void testInputOutputSerialization() throws IOException
    {
    long time = System.currentTimeMillis();

    JobConf jobConf = new JobConf();

    jobConf.set( "io.serializations", TestSerialization.class.getName() + "," + WritableSerialization.class.getName() ); // disable/replace WritableSerialization class
    jobConf.set( "cascading.serialization.tokens", "1000=" + BooleanWritable.class.getName() + ",10001=" + Text.class.getName() ); // not using Text, just testing parsing

    TupleSerialization tupleSerialization = new TupleSerialization( jobConf );

    File file = new File( getOutputPath( "serialization" ) );

    file.mkdirs();
    file = new File( file, "/test.bytes" );

    TupleOutputStream output = new HadoopTupleOutputStream( new FileOutputStream( file, false ), tupleSerialization.getElementWriter() );

    for( int i = 0; i < 501; i++ ) // 501 is arbitrary
      {
      String aString = "string number " + i;
      double random = Math.random();

      output.writeTuple( new Tuple( i, aString, random, new TestText( aString ), new Tuple( "inner tuple", new BytesWritable( "some string".getBytes() ) ), new BytesWritable( Integer.toString( i ).getBytes( "UTF-8" ) ), new BooleanWritable( false ) ) );
      }

    output.close();

    assertEquals( "wrong size", 89967L, file.length() ); // just makes sure the file size doesnt change from expected

    TupleInputStream input = new HadoopTupleInputStream( new FileInputStream( file ), tupleSerialization.getElementReader() );

    int k = -1;
    for( int i = 0; i < 501; i++ )
      {
      Tuple tuple = input.readTuple();
      int value = tuple.getInteger( 0 );
      assertTrue( "wrong diff", value - k == 1 );
      assertTrue( "wrong type", tuple.get( 3 ) instanceof TestText );
      assertTrue( "wrong type", tuple.get( 4 ) instanceof Tuple );
      assertTrue( "wrong type", tuple.get( 5 ) instanceof BytesWritable );

      byte[] bytes = ( (BytesWritable) tuple.get( 5 ) ).getBytes();
      String string = new String( bytes, 0, bytes.length > 1 ? bytes.length - 1 : bytes.length, "UTF-8" );
      assertEquals( "wrong value", Integer.parseInt( string ), i );
      assertTrue( "wrong type", tuple.get( 6 ) instanceof BooleanWritable );
      k = value;
      }

    input.close();

    System.out.println( "time = " + ( System.currentTimeMillis() - time ) );
    }
  }
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

package cascading.operation.regex;

import java.beans.ConstructorProperties;
import java.util.regex.Matcher;

import cascading.flow.FlowProcess;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;

/**
 * Class RegexReplace is used to replace a matched regex with a replacement value.
 * <p/>
 * RegexReplace only expects one field value. If more than one argument value is passed, only the
 * first is handled, the remainder are ignored.
 */
public class RegexReplace extends RegexOperation<Matcher> implements Function<Matcher>
  {
  /** Field replacement */
  private final String replacement;
  /** Field replaceAll */
  private boolean replaceAll = true;

  /**
   * Constructor RegexReplace creates a new RegexReplace instance,
   *
   * @param fieldDeclaration of type Fields
   * @param patternString    of type String
   * @param replacement      of type String
   * @param replaceAll       of type boolean
   */
  @ConstructorProperties({"fieldDeclaration", "patternString", "replacement", "replaceAll"})
  public RegexReplace( Fields fieldDeclaration, String patternString, String replacement, boolean replaceAll )
    {
    this( fieldDeclaration, patternString, replacement );
    this.replaceAll = replaceAll;
    }

  /**
   * Constructor RegexReplace creates a new RegexReplace instance.
   *
   * @param fieldDeclaration of type Fields
   * @param patternString    of type String
   * @param replacement      of type String
   */
  @ConstructorProperties({"fieldDeclaration", "patternString", "replacement"})
  public RegexReplace( Fields fieldDeclaration, String patternString, String replacement )
    {
    super( 1, fieldDeclaration, patternString );
    this.replacement = replacement;
    }

  @Override
  public void prepare( FlowProcess flowProcess, OperationCall<Matcher> operationCall )
    {
    operationCall.setContext( getPattern().matcher( "" ) );
    }

  /** @see Function#operate(cascading.flow.FlowProcess, cascading.operation.FunctionCall) */
  public void operate( FlowProcess flowProcess, FunctionCall<Matcher> functionCall )
    {
    // coerce to string
    String value = functionCall.getArguments().getString( 0 );

    // make safe
    if( value == null )
      value = "";

    Tuple output = new Tuple();

    Matcher matcher = functionCall.getContext().reset( value );

    if( replaceAll )
      output.add( matcher.replaceAll( replacement ) );
    else
      output.add( matcher.replaceFirst( replacement ) );

    functionCall.getOutputCollector().add( output );
    }

  @Override
  public boolean equals( Object object )
    {
    if( this == object )
      return true;
    if( !( object instanceof RegexReplace ) )
      return false;
    if( !super.equals( object ) )
      return false;

    RegexReplace that = (RegexReplace) object;

    if( replaceAll != that.replaceAll )
      return false;
    if( replacement != null ? !replacement.equals( that.replacement ) : that.replacement != null )
      return false;

    return true;
    }

  @Override
  public int hashCode()
    {
    int result = super.hashCode();
    result = 31 * result + ( replacement != null ? replacement.hashCode() : 0 );
    result = 31 * result + ( replaceAll ? 1 : 0 );
    return result;
    }
  }

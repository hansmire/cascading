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

package cascading.pipe;

/**
 * The Merge Pipe allows for multiple branches, with the same fields to be spliced back into a single stream.
 * <p/>
 * The behavior is similar to the {@link GroupBy} merging features, but Merge does not perform any grouping or
 * sorting on keys. Thus, when using a MapReduce platform, no Reducer is required.
 */
public class Merge extends Splice
  {
  /**
   * Constructor Merge creates a new Merge instance.
   *
   * @param pipes
   */
  public Merge( Pipe... pipes )
    {
    super( null, pipes );
    }

  /**
   * Constructor Merge creates a new Merge instance.
   *
   * @param name
   * @param pipes
   */
  public Merge( String name, Pipe... pipes )
    {
    super( name, pipes );
    }
  }

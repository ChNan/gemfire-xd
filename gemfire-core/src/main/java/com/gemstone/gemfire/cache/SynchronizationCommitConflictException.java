/*
 * Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */

package com.gemstone.gemfire.cache;

/** Thrown when a commit operation of a JTA enlisted cache transaction fails
 *
 * @author Mitch Thomas
 *
 * @see javax.transaction.UserTransaction#commit
 * @since 4.0
 */
public class SynchronizationCommitConflictException extends CacheRuntimeException {
private static final long serialVersionUID = 2619806460255259492L;
  /**
   * Constructs an instance of
   * <code>SynchronizationCommitConflictException</code> with the
   * specified detail message.
   * @param msg the detail message
   */
  public SynchronizationCommitConflictException(String msg) {
    super(msg);
  }
  
  /**
   * Constructs an instance of
   * <code>SynchronizationCommitConflictException</code> with the
   * specified detail message and cause.
   * @param msg the detail message
   * @param cause the causal Throwable
   */
  public SynchronizationCommitConflictException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
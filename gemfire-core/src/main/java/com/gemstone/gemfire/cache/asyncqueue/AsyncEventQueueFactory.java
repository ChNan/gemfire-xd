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
package com.gemstone.gemfire.cache.asyncqueue;

import com.gemstone.gemfire.cache.wan.GatewayEventFilter;
import com.gemstone.gemfire.cache.util.Gateway.OrderPolicy;

/**
 * Factory to create the <code>AsyncEventQueue</code>. 
 * Below example illustrates how to get the instance of factory and create the 
 * <code>AsyncEventQueue</code>.
<PRE>
  Cache c = new CacheFactory().create();
  // get AsyncEventQueueFactory from cache
  AsyncEventQueueFactory factory = cache.createAsyncEventQueueFactory();
  
  // set the attributes on factory
  factory.setBatchSize(batchSize);
  factory.setBatchConflationEnabled(isConflation);
  factory.setMaximumQueueMemory(maxMemory);
  factory.setParallel(isParallel);
             .
             .
  // create instance of AsyncEventListener           
  AsyncEventListener asyncEventListener = new <AsyncEventListener class>;
  // create AsyncEventQueue by providing the id and instance of AsyncEventListener
  AsyncEventQueue asyncQueue = factory.create(asyncQueueId, asyncEventListener);
</PRE>
 * 
 * @author pdeole
 * @since 7.0
 */
public interface AsyncEventQueueFactory {

  /**
   * Sets the disk store name for overflow or persistence.
   * 
   * @param name
   */
  public AsyncEventQueueFactory setDiskStoreName(String name);

  /**
   * Sets the maximum amount of memory (in MB) for an
   * <code>AsyncEventQueue</code>'s queue.
   * Default is 100 MB.
   * 
   * @param memory
   *          The maximum amount of memory (in MB) for an
   *          <code>AsyncEventQueue</code>'s queue
   */
  public AsyncEventQueueFactory setMaximumQueueMemory(int memory);
  
  /**
   * Sets whether or not the writing to the disk is synchronous.
   * Default is true.
   * 
   * @param isSynchronous
   *          boolean if true indicates synchronous writes
   *  
   */
  public AsyncEventQueueFactory setDiskSynchronous(boolean isSynchronous);

  /**
   * Sets the batch size for an <code>AsyncEventQueue</code>'s queue.
   * Default is 100.
   * 
   * @param size
   *          The size of batches sent to its <code>AsyncEventListener</code>
   */
  public AsyncEventQueueFactory setBatchSize(int size);
  
  /**
   * Sets the batch time interval (in milliseconds) for a <code>AsyncEventQueue</code>.
   * Default is 1000 ms.
   * 
   * @param interval
   *          The maximum time interval that can elapse before a partial batch
   *          is sent from a <code>AsyncEventQueue</code>.
   */
  public AsyncEventQueueFactory setBatchTimeInterval(int interval);

  /**
   * Sets whether the <code>AsyncEventQueue</code> is persistent or not.
   * Default is false.
   * 
   * @param isPersistent
   *          Whether to enable persistence for an <code>AsyncEventQueue</code>.
   */
  public AsyncEventQueueFactory setPersistent(boolean isPersistent);

  /**
   * Indicates whether all VMs need to distribute events to remote site. In this
   * case only the events originating in a particular VM will be in dispatched
   * in order.
   * Default is false.
   * 
   * @param isParallel
   *          boolean to indicate whether distribution policy is parallel
   */

  public AsyncEventQueueFactory setParallel(boolean isParallel);
  
  /**
   * Sets whether to enable batch conflation for <code>AsyncEventQueue</code>.
   * Default is false.
   * 
   * @param     isConflation        
   *              Whether or not to enable batch conflation for batches sent from a 
   *              <code>AsyncEventQueue</code>
   */
  public AsyncEventQueueFactory setBatchConflationEnabled(boolean isConflation);
  
  /**
   * Sets the number of dispatcher thread.
   * Default is 1.
   * 
   * @param numThreads
   */
  public AsyncEventQueueFactory setDispatcherThreads(int numThreads);

  /**
   * Sets the alert threshold (in milliseconds) for entries in a <code>AsyncEventQueue</code> 's
   * queue.
   * Default is 0.
   * 
   * @param threshold
   *          the alert threshold for entries in a <code>AsyncEventQueue</code>
   *          's queue
   */
  public AsyncEventQueueFactory setAlertThreshold(int threshold);

  /**
   * Sets the manual start boolean property for this
   * <code>AsyncEventQueue</code>.
   * Default is false meaning the AsyncEventQueue is automatically started once created.
   * 
   * @param start
   *          the manual start boolean property for this
   *          <code>AsyncEventQueue</code>
   */
  public AsyncEventQueueFactory setManualStart(boolean start);

  /**
   * Removes a <code>GatewayEventFilter</code> to the attributes of
   * AsyncEventQueue being created by factory.
   * 
   * @param filter
   *          GatewayEventFilter
   */
  public AsyncEventQueueFactory addGatewayEventFilter(GatewayEventFilter filter);

  /**
   * Removes the provided <code>GatewayEventFilter</code> from the attributes of
   * AsyncEventQueue being created by factory.
   * 
   * @param filter
   */
  public AsyncEventQueueFactory removeGatewayEventFilter(
      GatewayEventFilter filter);

  /**
   * Sets the order policy for multiple dispatchers.
   * Default is KEY.
   * 
   * @param policy
   */
  public AsyncEventQueueFactory setOrderPolicy(OrderPolicy policy);

  /**
   * Creates the <code>AsyncEventQueue</code>. It accepts Id of AsyncEventQueue
   * and instance of AsyncEventListener. Multiple queues can be created using
   * Same listener instance. So, the instance of <code>AsyncEventListener</code>
   * should be thread safe in that case. The <code>AsyncEventListener</code>
   * will start receiving events when the <code>AsyncEventQueue</code> is
   * created.
   * 
   * 
   * @param id
   *          Id of AsyncEventQueue
   * @param listener
   *          <code>AsyncEventListener</code> to be added to the regions that
   *          are configured to use this queue.
   */
  public AsyncEventQueue create(String id, AsyncEventListener listener);
}
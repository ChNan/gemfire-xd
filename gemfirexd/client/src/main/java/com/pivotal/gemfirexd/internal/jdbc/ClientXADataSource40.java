/*

   Derby - Class com.pivotal.gemfirexd.internal.jdbc.ClientXADataSource40

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

/*
 * Changes for GemFireXD distributed data platform (some marked by "GemStone changes")
 *
 * Portions Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
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

package com.pivotal.gemfirexd.internal.jdbc;

import java.sql.SQLException;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import com.pivotal.gemfirexd.internal.client.ClientXAConnection40;
import com.pivotal.gemfirexd.internal.client.am.ClientMessageId;
import com.pivotal.gemfirexd.internal.client.am.SqlException;
import com.pivotal.gemfirexd.internal.client.net.NetLogWriter;
import com.pivotal.gemfirexd.internal.shared.common.reference.SQLState;

/**
 * <p>
 * This is Derby's network XADataSource for use with JDBC4.0.
 * </p>
 * An XADataSource is a factory for XAConnection objects.  It represents a
 * RM in a DTP environment.  An object that implements the XADataSource
 * interface is typically registered with a JNDI service provider.   	
 * <P>
 * ClientXADataSource40 supports the JDBC 4.0 specification
 * for the J2SE 6.0 Java Virtual Machine environment. Use ClientXADataSource
 * if your application runs in the following environments:
 * <UL>
 * <LI> JDBC 3.0 - Java 2 - JDK 1.4, J2SE 5.0
 * <LI> JDBC 2.0 - Java 2 - JDK 1.2,1.3
 * </UL>
 *
 * <P>ClientXADataSource40 is serializable and referenceable.</p>
 *
 * <P>See ClientDataSource40 for DataSource properties.</p>
 */
public class ClientXADataSource40 extends ClientXADataSource {
    
	/**
     * Returns false unless <code>interfaces</code> is implemented 
     * 
     * @param  interfaces             a Class defining an interface.
     * @return true                   if this implements the interface or 
     *                                directly or indirectly wraps an object 
     *                                that does.
     * @throws java.sql.SQLException  if an error occurs while determining 
     *                                whether this is a wrapper for an object 
     *                                with the given interface.
     */
// GemStone changes BEGIN
  // made non-generic so can override the method in base class so that can
  // be compiled with both JDK 1.6 and 1.4
  public boolean isWrapperFor(Class interfaces) throws SQLException {
  /* (original code)
    public boolean isWrapperFor(Class<?> interfaces) throws SQLException {
  */
// GemStone changes END
        return interfaces.isInstance(this);
    }
    
    /**
     * Returns <code>this</code> if this class implements the interface
     *
     * @param  interfaces a Class defining an interface
     * @return an object that implements the interface
     * @throws java.sql.SQLExption if no object if found that implements the 
     * interface
     */
// GemStone changes BEGIN
    // made non-generic so can override the method in base class so that can
    // be compiled with both JDK 1.6 and 1.4
    public Object unwrap(java.lang.Class interfaces) throws SQLException {
    /* (original code)
    public <T> T unwrap(java.lang.Class<T> interfaces)
                                   throws SQLException {
    */
// GemStone changes END
        try { 
            return interfaces.cast(this);
        } catch (ClassCastException cce) {
            throw new SqlException(null,new ClientMessageId(
                    SQLState.UNABLE_TO_UNWRAP), interfaces).getSQLException(
                        null /* GemStoneAddition */);
        }
    }
}
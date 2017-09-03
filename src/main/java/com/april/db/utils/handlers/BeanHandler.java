/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.april.db.utils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.april.db.utils.ResultSetHandler;
import com.april.db.utils.RowProcessor;

/**
 * <code>ResultSetHandler</code> implementation that converts the first 
 * <code>ResultSet</code> row into a JavaBean. This class is thread safe.
 * 
 * @see com.april.db.utils.ResultSetHandler
 */
public class BeanHandler implements ResultSetHandler {

    /**
     * The Class of beans produced by this handler.
     */
    private Class type = null;

    /**
     * The RowProcessor implementation to use when converting rows 
     * into beans.
     */
    private RowProcessor convert = ArrayHandler.ROW_PROCESSOR;

    /** 
     * Creates a new instance of BeanHandler.
     * 
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     */
    public BeanHandler(Class type) {
        this.type = type;
    }

    /** 
     * Creates a new instance of BeanHandler.
     * 
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     * @param convert The <code>RowProcessor</code> implementation 
     * to use when converting rows into beans.
     */
    public BeanHandler(Class type, RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }

    /**
     * Convert the first row of the <code>ResultSet</code> into a bean with the
     * <code>Class</code> given in the constructor.
     * 
     * @return An initialized JavaBean or <code>null</code> if there were no 
     * rows in the <code>ResultSet</code>.
     * 
     * @throws SQLException if a database access error occurs
     * @see com.april.db.utils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    public Object handle(ResultSet rs) throws SQLException {
        return rs.next() ? this.convert.toBean(rs, this.type) : null;
    }

}

/*
 * Copyright 2008-2009 The MxUpdate Team
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
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.update.AbstractBusObject_mxJPO;

/**
 * @author tmoxter
 * @version $Id$
 */
public class ObjectGenerator_mxJPO
        extends AbstractBusObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -8831221903916014033L;

    private final static String RELATIONSHIP_NUMBER_GENERATOR = "eService Number Generator";

    /**
     * Select statement for the number generator type.
     */
    private final static String SELECT_NUM_TYPE = "from[" + RELATIONSHIP_NUMBER_GENERATOR + "].to.type";

    /**
     * Select statement for the number generator name.
     */
    private final static String SELECT_NUM_NAME = "from[" + RELATIONSHIP_NUMBER_GENERATOR + "].to.name";

    /**
     * Select statement for the number generator revision.
     */
    private final static String SELECT_NUM_REVI = "from[" + RELATIONSHIP_NUMBER_GENERATOR + "].to.revision";

    /**
     * Related number generator type.
     *
     * @see #prepare(Context)
     */
    private String numGenType = null;

    /**
     * Related number generator name.
     *
     * @see #prepare(Context)
     */
    private String numGenName = null;

    /**
     * Related number generator revision.
     *
     * @see #prepare(Context)
     */
    private String numGenRevi = null;

    /**
     * Related number generator revision.
     *
     * @param _content  context for this request
     * @see #active
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        super.prepare(_context);
        // read current state
        final BusinessObject bus = new BusinessObject(getBusType(),
                                                      getBusName(),
                                                      getBusRevision(),
                                                      getBusVault());
        final StringList selects = new StringList(3);
        selects.addElement(SELECT_NUM_TYPE);
        selects.addElement(SELECT_NUM_NAME);
        selects.addElement(SELECT_NUM_REVI);
        final BusinessObjectWithSelect s = bus.select(_context, selects);
        this.numGenType = s.getSelectData(SELECT_NUM_TYPE);
        this.numGenName = s.getSelectData(SELECT_NUM_NAME);
        this.numGenRevi = s.getSelectData(SELECT_NUM_REVI);
    }

    /**
     * Appends the information if the trigger is active or not. If the trigger
     * is active, a promote must be done within the TCL update script.
     *
     * @param _out      writer instance
     */
    @Override
    protected final void write(final Writer _out)
            throws IOException
    {
        super.write(_out);
        if ((this.numGenName != null) && (this.numGenRevi != null))  {
            _out.append("\nmql connect bus  \"${OBJECTID}\" \\\n")
                .append("    relationship \"").append(RELATIONSHIP_NUMBER_GENERATOR).append("\" \\\n")
                .append("    to \"").append(this.numGenType).append("\" \"")
                        .append(this.numGenName).append("\" \"")
                        .append(this.numGenRevi).append("\"");
        }
    }

    /**
     * The pre MQL code gets in the front the MQL code to disconnect from the
     * number generator if already connected.
     *
     * @param _context          context for this request
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder();

        // disconnect from number generator
        if ((this.numGenType != null) && !"".equals(this.numGenType)
                && (this.numGenName != null) && !"".equals(this.numGenName)
                && (this.numGenRevi != null) && !"".equals(this.numGenRevi))  {
            preMQLCode.append("disconnect bus ").append(this.getBusOid())
                      .append(" relationship \"").append(RELATIONSHIP_NUMBER_GENERATOR)
                      .append("\" to \"").append(this.numGenType).append("\" \"")
                              .append(this.numGenName).append("\" \"")
                              .append(this.numGenRevi).append("\"")
                      .append(";\n");
        }

        // append rest of pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_context, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}

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
import org.mxupdate.update.util.InfoAnno_mxJPO;
import org.mxupdate.util.Mapping_mxJPO.BusTypeDef;

/**
 * @author tmoxter
 * @version $Id$
 */
@InfoAnno_mxJPO(busType = BusTypeDef.Trigger)
public class Trigger_mxJPO
        extends AbstractBusObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 8068133769051567270L;

    /**
     * Is the trigger active?
     *
     * @see #prepare(Context)   used to read the information if the trigger is
     *                          active
     */
    private boolean active = false;

    /**
     * Reads the information if the trigger is active.
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
        final StringList selects = new StringList(1);
        selects.addElement("current");
        final BusinessObjectWithSelect s = bus.select(_context, selects);
        active = "Active".equals(s.getSelectData("current"));
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
        if (this.active)  {
            _out.append("\nmql promote bus \"${OBJECTID}\"");
        }
    }

    /**
     * If a trigger object is updated, the trigger must be demoted if the
     * trigger is active. The MQL code for the demote is set in the front of
     * the given MQL code in <code>_preMQLCode</code>.
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

        // demote if required
        if (this.active)  {
            preMQLCode.append("demote bus ").append(this.getBusOid())
                      .append(";\n");
        }

        // append rest of pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_context, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}

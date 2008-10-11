/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.Writer;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;



/**
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(busType = "eService Trigger Program Parameters",
                                                     filePrefix="trigger",
                                                     filePath = "datamodel/trigger",
                                                     description = "trigger")
public class Trigger_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractBusObject_mxJPO
{
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
}

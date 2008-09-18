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
@net.sourceforge.mxupdate.update.util.BusType_mxJPO(type="eService Object Generator",filePrefix="objectgenerator")
@net.sourceforge.mxupdate.update.util.Path_mxJPO("datamodel/objectgenerator")
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("object generator")
public class ObjectGenerator_mxJPO
        extends net.sourceforge.mxupdate.update.MatrixBusObject_mxJPO
{
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
}

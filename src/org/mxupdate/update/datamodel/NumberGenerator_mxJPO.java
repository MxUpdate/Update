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

import java.io.IOException;
import java.io.Writer;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AttributeDef;
import org.mxupdate.update.AbstractBusObject_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
 * @author tmoxter
 * @version $Id$
 */
public class NumberGenerator_mxJPO
        extends AbstractBusObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -3678077121553752020L;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public NumberGenerator_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Writes the information to update the business objects. Because for the
     * next number attribute must be enclosing within an if clause, the
     * original method is overwritten.
     *
     * @param _out      writer instance
     */
    @Override
    protected void write(final Writer _out)
            throws IOException
    {
        writeHeader(_out);
        _out.append("mql mod bus \"${OBJECTID}\"")
            .append(" \\\n    description \"").append(convertTcl(getDescription())).append("\"");
        String nextNumber = null;
        for (final Attribute attr : getAttrValuesSorted())  {
            if (AttributeDef.NumberGeneratorNextNumber.getMxName().equals(attr.name))  {
                nextNumber = attr.value;
            } else  {
                _out.append(" \\\n    \"").append(convertTcl(attr.name))
                    .append("\" \"").append(convertTcl(attr.value)).append("\"");
            }
        }
        _out.append("\n")
            .append("\n# update of the next number attribute only if not already set")
            .append("\nset sTmp [mql print bus \"${OBJECTID}\" select attribute\\[").append(AttributeDef.NumberGeneratorNextNumber.getMxName()).append("\\] dump]")
            .append("\nif {[string length \"${sTmp}\"]==0}  {")
            .append("\n  mql mod bus \"${OBJECTID}\" \"").append(AttributeDef.NumberGeneratorNextNumber.getMxName()).append("\" \"").append(nextNumber).append("\"")
            .append("\n}");
    }
}

/*
 * Copyright 2008-2011 The MxUpdate Team
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

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * @author The MxUpdate Team
 * @version $Id$
 */
public class NumberGenerator_mxJPO
    extends BusObject_mxJPO
{
    /**
     * String of the key within the parameter cache to define the name of the
     * attribute which holds the next number of the number generator.
     *
     * @see #extractSymbolicNameFromCode(ParameterCache_mxJPO, StringBuilder)
     */
    private static final String PARAM_ATTR_NUMBERGENERATOR_NEXTNUMBER = "AttrNumberGenNextNumber";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the number generator object
     */
    public NumberGenerator_mxJPO(final TypeDef_mxJPO _typeDef,
                                 final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Writes the information to update the business objects. Because for the
     * next number attribute must be enclosing within an if clause, the
     * original method is overwritten.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException if the TCL update code for the number generator
     *                     could not be written
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.writeHeader(_paramCache, _out);
        _out.append("mql mod bus \"${OBJECTID}\"")
            .append(" \\\n    description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append("\"");
        String nextNumber = null;
        final String attrNextNumber = _paramCache.getValueString(NumberGenerator_mxJPO.PARAM_ATTR_NUMBERGENERATOR_NEXTNUMBER);
        for (final AttributeValue attr : this.getAttrValuesSorted())  {
            if (attrNextNumber.equals(attr.name))  {
                nextNumber = attr.value;
            } else  {
                _out.append(" \\\n    \"").append(StringUtil_mxJPO.convertTcl(attr.name))
                    .append("\" \"").append(StringUtil_mxJPO.convertTcl(attr.value)).append("\"");
            }
        }
        _out.append("\n")
            .append("\n# update of the next number attribute only if not already set")
            .append("\nset sTmp [mql print bus \"${OBJECTID}\" select attribute\\[")
                    .append(attrNextNumber).append("\\] dump]")
            .append("\nif {[string length \"${sTmp}\"]==0}  {")
            .append("\n  mql mod bus \"${OBJECTID}\" \"")
                    .append(attrNextNumber)
                    .append("\" \"").append(nextNumber).append("\"")
            .append("\n}");
    }
}

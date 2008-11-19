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

import net.sourceforge.mxupdate.update.AbstractBusObject_mxJPO;
import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * @author tmoxter
 * @version $Id$
 */
@InfoAnno_mxJPO(busType = "eService Number Generator",
                busIgnoreAttributes = {"eService Next Number"},
                title = "NUMBERGENERATOR",
                filePrefix = "NUMBERGENERATOR_",
                fileSuffix = ".tcl",
                filePath = "datamodel/numbergenerator",
                description = "number generator")
public class NumberGenerator_mxJPO
        extends AbstractBusObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -3678077121553752020L;

    final static String ATTRIBUTE_ESERVICE_NEXT_NUMBER = "eService Next Number";

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
            .append(" \\\n    description \"").append(convert(getDescription())).append("\"");
        String nextNumber = null;
        for (final Attribute attr : getAttrValuesSorted())  {
            if (ATTRIBUTE_ESERVICE_NEXT_NUMBER.equals(attr.name))  {
                nextNumber = attr.value;
            } else  {
                _out.append(" \\\n    \"").append(convert(attr.name))
                    .append("\" \"").append(convert(attr.value)).append("\"");
            }
        }
        _out.append("\n")
            .append("\n# update of the next number attribute only if not already set")
            .append("\nset sTmp [mql print bus \"${OBJECTID}\" select attribute\\[").append(ATTRIBUTE_ESERVICE_NEXT_NUMBER).append("\\] dump]")
            .append("\nif {[string length \"${sTmp}\"]==0}  {")
            .append("\n  mql mod bus \"${OBJECTID}\" \"").append(ATTRIBUTE_ESERVICE_NEXT_NUMBER).append("\" \"").append(nextNumber).append("\"")
            .append("\n}");
    }
}

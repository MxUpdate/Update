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

import matrix.db.Context;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * Data model type class.
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "type",
                                                     title = "TYPE",
                                                     filePrefix = "TYPE_",
                                                     fileSuffix = ".tcl",
                                                     filePath = "datamodel/type",
                                                     description = "type")
public class Type_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMWithAttributes_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 7501426743362043943L;

    /**
     * Is the type abstract?
     */
    private boolean abstractFlag = false;

    /**
     * From which type is this type derived?
     */
    private String derived = "ADMINISTRATION";

    /**
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/abstract".equals(_url))  {
            this.abstractFlag = true;

        } else if ("/attributeDefRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/derivedFrom".equals(_url))  {
            // to be ignored ...
        } else if ("/derivedFrom/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/derivedFrom/typeRefList/typeRef".equals(_url))  {
            this.derived = _content;

        } else  {
            super.parse(_url, _content);
        }
    }

    @Override
    protected void writeObject(Writer _out) throws IOException
    {
        _out.append(" \\\n    derived \"").append(convert(this.derived)).append("\"")
            .append(" \\\n    ").append(isHidden() ? "" : "!").append("hidden")
            .append(" \\\n    abstract ").append(Boolean.toString(this.abstractFlag));
        this.writeTriggers(_out);
    }

    /**
     * Appends the MQL statement to reset this attribute:
     * <ul>
     * <li>set not hidden</li>
     * <li>reset description</li>
     * <li>remove all triggers</li>
     * </ul>
     *
     * @param _context  context for this request
     * @param _cmd      string builder used to append the MQL statements
     */
    @Override
    protected void appendResetMQL(final Context _context,
                                  final StringBuilder _cmd)
    {
        _cmd.append("mod ").append(getInfoAnno().adminType())
            .append(" \"").append(getName()).append('\"')
            .append(" !hidden description \"\"");
        // reset triggers
        this.appendResetTriggerMQLStatements(_cmd);
    }
}

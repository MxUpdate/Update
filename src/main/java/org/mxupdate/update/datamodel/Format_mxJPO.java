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

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Format_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Reference to the edit program.
     */
    private String commandEdit = null;

    /**
     * Reference to the print program.
     */
    private String commandPrint = null;

    /**
     * Reference to the view program.
     */
    private String commandView = null;

    /**
     * File suffix of the format.
     */
    private String fileSuffix = null;

    /**
     * File type of the format.
     */
    private String fileType = null;

    /**
     * Version of the format.
     */
    private String version = null;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the format object
     */
    public Format_mxJPO(final TypeDef_mxJPO _typeDef,
                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all format specific URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/editCommand".equals(_url))  {
            this.commandEdit = _content;
        } else if ("/printCommand".equals(_url))  {
            this.commandPrint = _content;
        } else if ("/viewCommand".equals(_url))  {
            this.commandView = _content;

        } else if ("/fileCreator".equals(_url))  {
            // to be ignored, because identically to fileType
        } else if ("/fileSuffix".equals(_url))  {
            this.fileSuffix = _content;
        } else if ("/fileType".equals(_url))  {
            this.fileType = _content;

        } else if ("/version".equals(_url))  {
            this.version = _content;

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes specific information about the cached format to the given
     * writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the format could not be
     *                     written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        _out.append(" \\\n    ").append(this.isHidden() ? "hidden" : "nothidden")
            .append(" \\\n    version \"").append((this.version != null) ? StringUtil_mxJPO.convertTcl(this.version) : "").append('\"')
            .append(" \\\n    suffix \"").append((this.fileSuffix != null) ? StringUtil_mxJPO.convertTcl(this.fileSuffix) : "").append('\"')
            .append(" \\\n    mime \"").append((this.fileType != null) ? StringUtil_mxJPO.convertTcl(this.fileType) : "").append('\"')
            .append(" \\\n    view \"").append((this.commandView != null) ? StringUtil_mxJPO.convertTcl(this.commandView) : "").append('\"')
            .append(" \\\n    edit \"").append((this.commandEdit != null) ? StringUtil_mxJPO.convertTcl(this.commandEdit) : "").append('\"')
            .append(" \\\n    print \"").append((this.commandPrint != null) ? StringUtil_mxJPO.convertTcl(this.commandPrint) : "").append('\"');
    }
}

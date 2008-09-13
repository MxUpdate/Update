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

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.AdminType_mxJPO("format")
@net.sourceforge.mxupdate.update.util.Path_mxJPO("datamodel/format")
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("format")
public class Format_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMObject_mxJPO
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
     * Parses all format specific URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    public void parse(final String _url,
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
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "nothidden")
            .append(" \\\n    version \"").append((this.version != null) ? convert(this.version) : "").append('\"')
            .append(" \\\n    suffix \"").append((this.fileSuffix != null) ? convert(this.fileSuffix) : "").append('\"')
            .append(" \\\n    mime \"").append((this.fileType != null) ? convert(this.fileType) : "").append('\"')
            .append(" \\\n    view \"").append((this.commandView != null) ? convert(this.commandView) : "").append('\"')
            .append(" \\\n    edit \"").append((this.commandEdit != null) ? convert(this.commandEdit) : "").append('\"')
            .append(" \\\n    print \"").append((this.commandPrint != null) ? convert(this.commandPrint) : "").append('\"');
    }
}

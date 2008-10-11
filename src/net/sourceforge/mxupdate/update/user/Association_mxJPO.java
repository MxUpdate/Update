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

package net.sourceforge.mxupdate.update.user;

import java.io.IOException;
import java.io.Writer;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "association",
                                                     filePrefix = "ASSOCIATION",
                                                     filePath = "user/association",
                                                     description = "association")
public class Association_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO
{
    /**
     * Stores the definition of this association instance.
     */
    private String definition = null;

    /**
     * Parses all association specific URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/definition".equals(_url))  {
            this.definition = _content;

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes specific information about the cached associations to the given
     * writer instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "!hidden")
            .append(" \\\n    definition \"").append(convert(this.definition)).append("\"");
    }
}

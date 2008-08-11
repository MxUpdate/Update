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

package net.sourceforge.mxupdate.update.userinterface;

import java.io.IOException;
import java.io.Writer;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 */
public class Inquiry_mxJPO
       extends net.sourceforge.mxupdate.update.userinterface.AbstractUIObject_mxJPO
{
    /**
     * Code for the inquiry.
     */
    private String code = null;

    /**
     * Format for the inquiry.
     */
    private String format = null;

    /**
     * Pattern for the inquiry.
     */
    private String pattern = null;

    public Inquiry_mxJPO()
    {
        super("inquiry");
    }

    @Override
    public void parse(final String _url,
                      final String _content)
    {
        if ("/code".equals(_url))  {
            this.code = _content;
        } else if ("/fmt".equals(_url))  {
            this.format = _content;
        } else if ("/pattern".equals(_url))  {
            this.pattern = _content;
        } else  {
            super.parse(_url, _content);
        }
    }

    @Override
    protected void writeObject(Writer _out) throws IOException
    {
        _out.append(" \\\n    pattern \"").append(convert(this.pattern)).append("\"")
            .append(" \\\n    format \"").append(convert(this.format)).append("\"")
            .append(" \\\n    file \"${FILE}\"");
        for (final net.sourceforge.mxupdate.update.MatrixObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _out.append(" \\\n    add argument \"").append(convert(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(convert(prop.getValue())).append("\"");
            }
        }
   }

    @Override
    protected void writeEnd(final Writer _out)
            throws IOException
    {
        _out.append("\n\n# do not change the next three lines, they are needed as separator information:")
            .append("\n################################################################################")
            .append("\n# INQUIRY CODE                                                                 #")
            .append("\n################################################################################")
            .append("\n\n").append(this.code);
    }

}

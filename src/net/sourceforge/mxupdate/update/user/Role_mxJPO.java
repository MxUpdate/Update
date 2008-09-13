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
import java.util.Set;
import java.util.TreeSet;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.AdminType_mxJPO("role")
@net.sourceforge.mxupdate.update.util.Path_mxJPO("user/role")
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("role")
public class Role_mxJPO
        extends net.sourceforge.mxupdate.update.user.AbstractUserObject_mxJPO
{
    /**
     * Set to hold all parent roles.
     */
    final Set<String> parentRoles = new TreeSet<String>();

    /**
     * Parses all role specific URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    public void parse(final String _url,
                      final String _content)
    {
        if ("/parentRole".equals(_url))  {
            // to be ignored ...
        } else if ("/parentRole/roleRef".equals(_url))  {
            this.parentRoles.add(_content);

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes specific information about the cached role to the given
     * writer instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        for (final String role : this.parentRoles)  {
            _out.append("\nmql mod role \"")
                .append(convert(role))
                .append("\" child \"${NAME}\"");
        }
    }
}

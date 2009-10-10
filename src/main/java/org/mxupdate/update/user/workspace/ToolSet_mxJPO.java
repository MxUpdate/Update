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

package org.mxupdate.update.user.workspace;

import java.io.IOException;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store the workspace object for one tool set.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class ToolSet_mxJPO
    extends AbstractWorkspaceObject_mxJPO
{
    /**
     * Is the tool set active?
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private boolean active = false;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     */
    public ToolSet_mxJPO(final AbstractUser_mxJPO _user)
    {
        super(_user, "toolset");
    }

    /**
     * <p>Parses all tool set specific URL values. This includes:
     * <ul>
     * <li>{@link #active active flag}</li>
     * </ul></p>
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override()
    public void parse(final String _url,
                      final String _content)
    {
        if ("/active".endsWith(_url))  {
            this.active = true;
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * <p>Writes all tool set specific values to the TCL update file
     * <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #active active flag}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    @Override()
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
        throws IOException
    {
        super.write(_paramCache, _out);
        _out.append(" \\\n    ").append(this.active ? "active" : "!active");

        // methods
        for (final AdminProperty_mxJPO prop : this.getProperties())  {
            if (prop.isSetting() && "%Member".equals(prop.getName()))  {
                _out.append(" \\\n    program \"").append(StringUtil_mxJPO.convertTcl(prop.getRefAdminName())).append("\"");
            }
        }
    }
}

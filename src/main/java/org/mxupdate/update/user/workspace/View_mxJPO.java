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

package org.mxupdate.update.user.workspace;

import java.io.IOException;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store the workspace object for one view.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class View_mxJPO
    extends AbstractWorkspaceObject_mxJPO
{
    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     */
    public View_mxJPO(final AbstractUser_mxJPO _user)
    {
        super(_user, "view");
    }

    /**
     * <p>Writes all view specific values to the TCL update file
     * <code>_out</code>. This includes:
     * <ul>
     * <li>all assign visual workspace objects defined via the properties with
     *     the name &quot;%Member&quot;</li>
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

        // methods
        for (final AdminProperty_mxJPO prop : this.getProperties())  {
            if (prop.isSetting() && "%Member".equals(prop.getName()))  {
                _out.append(" \\\n    ");
                if ("4".equals(prop.getFlags()))  {
                    _out.append("in");
                }
                _out.append("active ").append(prop.getRefAdminType())
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getRefAdminName())).append("\"");
            }
        }
    }
}

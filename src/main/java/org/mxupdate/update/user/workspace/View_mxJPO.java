/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
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

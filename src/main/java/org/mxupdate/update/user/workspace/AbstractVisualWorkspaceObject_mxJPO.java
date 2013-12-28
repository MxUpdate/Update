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
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * User specific class to store common information of a visual workspace object
 * functionality (used for visuals).
 *
 * @author The MxUpdate Team
 */
abstract class AbstractVisualWorkspaceObject_mxJPO
    extends AbstractWorkspaceObject_mxJPO
{
    /**
     * Is the visual workspace object active?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private boolean active = false;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     * @param _mxAdminType  administration type of the visual workspace object
     */
    AbstractVisualWorkspaceObject_mxJPO(final AbstractUser_mxJPO _user,
                                        final String _mxAdminType)
    {
        super(_user, _mxAdminType);
    }

    /**
     * Defines if this visual workspace object is active.
     *
     * @param _active   <i>true</i> if the visual workspace object is active;
     *                  otherwise <i>false</i>
     * @see #active
     */
    protected void setActive(final boolean _active)
    {
        this.active = _active;
    }

    /**
     * <p>Parses all common visual workspace object specific URL values.
     * This includes:
     * <ul>
     * <li>{@link #active active flag}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if ("/active".endsWith(_url))  {
            this.active = true;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * <p>Writes all visual workspace object specific values to the TCL
     * update file <code>_out</code>. This includes:
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
    }
}

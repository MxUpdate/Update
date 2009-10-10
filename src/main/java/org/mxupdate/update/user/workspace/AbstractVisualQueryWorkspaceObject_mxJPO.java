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
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store common information of a visual workspace object
 * with query functionality (used for visuals).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
abstract class AbstractVisualQueryWorkspaceObject_mxJPO
    extends AbstractQueryWorkspaceObject_mxJPO
{
    /**
     * Is the visual workspace object active?
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private boolean active = false;

    /**
     * Applies to business object / relationship or all.
     */
    private AppliesTo appliesTo = Cue_mxJPO.AppliesTo.BOTH;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     * @param _mxAdminType  administration type of the visual query workspace
     *                      object
     */
    AbstractVisualQueryWorkspaceObject_mxJPO(final AbstractUser_mxJPO _user,
                                             final String _mxAdminType)
    {
        super(_user, _mxAdminType);
    }

    /**
     * <p>Parses all common visual query workspace object specific URL values.
     * This includes:
     * <ul>
     * <li>{@link #active active flag}</li>
     * <li>{@link #appliesTo applies to business object, connection or both}
     *     </li>
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
        } else if ("/targetType".equals(_url))  {
            if ("objects".equals(_content))  {
                this.appliesTo = AbstractVisualQueryWorkspaceObject_mxJPO.AppliesTo.BUSINESSOBJECT;
            } else if ("relationships".equals(_content))  {
                this.appliesTo = AbstractVisualQueryWorkspaceObject_mxJPO.AppliesTo.CONNECTION;
            } else if ("omni".equals(_content))  {
                this.appliesTo = AbstractVisualQueryWorkspaceObject_mxJPO.AppliesTo.BOTH;
            } else  {
                throw new Error("Unknown target type (applies to) '" + _content + "' for cue defined!");
            }
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * <p>Writes all visual query workspace object specific values to the TCL
     * update file <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #active active flag}</li>
     * <li>{@link #appliesTo applies to business object, connection or both}</li>
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
        _out.append(" \\\n    ").append(this.active ? "active" : "!active")
            .append(" \\\n    appliesto \"").append(this.appliesTo.mxValue).append("\"");
    }

    /**
     * Writes the pattern for the business type, name and revision to the TCL
     * update file in <code>_out</code>.
     *
     * @param _out          appendable instance to the TCL update file
     * @param _type         pattern type
     * @param _name         pattern name
     * @param _revision     pattern revision
     * @throws IOException if the TCL update code could not written
     */
    @Override()
    protected void writeTypeNameRevision(final Appendable _out,
                                         final String _type,
                                         final String _name,
                                         final String _revision)
        throws IOException
    {
        _out.append(" \\\n    type \"").append(StringUtil_mxJPO.convertTcl(_type)).append("\"")
            .append(" \\\n    name \"").append(StringUtil_mxJPO.convertTcl(_name)).append("\"")
            .append(" \\\n    revision \"").append(StringUtil_mxJPO.convertTcl(_revision)).append("\"");
    }

    /**
     * Enumeration for which a cue applies to.
     */
    protected enum AppliesTo
    {
        /**
         * Cue applies to business objects.
         */
        BUSINESSOBJECT("businessobject"),

        /**
         * Cue applies to connections.
         */
        CONNECTION("relationship"),

        /**
         * Cue applies to business objects and to connections.
         */
        BOTH("all");

        /**
         * Related value needed within TCL update.
         */
        final String mxValue;

        /**
         * Default constructor.
         *
         * @param _mxValue  related value used for the TCL update
         */
        private AppliesTo(final String _mxValue)
        {
            this.mxValue = _mxValue;
        }
    }
}

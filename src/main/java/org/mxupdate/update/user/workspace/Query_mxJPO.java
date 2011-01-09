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
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store the workspace object for one query.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Query_mxJPO
    extends AbstractQueryWorkspaceObject_mxJPO
{
    /**
     * Must the types expand for the query?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private boolean expandType = false;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     */
    public Query_mxJPO(final AbstractUser_mxJPO _user)
    {
        super(_user, "query");
    }

    /**
     * <p>Parses all query specific URL values. This includes:
     * <ul>
     * <li>{@link #expandType expand type flag}</li>
     * </ul></p>
     *
     * @param _paramCache   paramater cache with MX context
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if ("/queryStatement/expandType".equals(_url))  {
            this.expandType = true;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes the pattern for the business type, name and revision and the
     * {@link #expandType expand type flag} to the TCL update file in
     * <code>_out</code>.
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
        _out.append(" \\\n    businessobject \"").append(StringUtil_mxJPO.convertTcl(_type)).append("\"")
            .append(" \"").append(StringUtil_mxJPO.convertTcl(_name)).append("\"")
            .append(" \"").append(StringUtil_mxJPO.convertTcl(_revision)).append("\"");

        // expand type flag
        if (this.expandType)  {
            _out.append(" \\\n    expandtype");
        } else  {
            _out.append(" \\\n    !expandtype");
        }
    }
}

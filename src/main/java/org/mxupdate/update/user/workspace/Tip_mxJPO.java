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
 * User specific class to store the workspace object for one tip.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Tip_mxJPO
    extends AbstractVisualQueryWorkspaceObject_mxJPO
{
    /**
     * Expression of the tip.
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String expression;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     */
    public Tip_mxJPO(final AbstractUser_mxJPO _user)
    {
        super(_user, "tip");
    }

    /**
     * <p>Parses all tip specific URL values. This includes:
     * <ul>
     * <li>{@link #expression}</li>
     * </ul></p>
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override()
    public void parse(final String _url,
                      final String _content)
    {
        if ("/expression".equals(_url))  {
            this.expression = _content;
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * <p>Writes all tip specific values to the TCL update file
     * <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #expression}</li>
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
        if (this.expression != null)  {
            _out.append(" \\\n    expression \"").append(StringUtil_mxJPO.convertTcl(this.expression)).append("\"");
        }
    }
}

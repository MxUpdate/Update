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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.Writer;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
*
* @author tmoxter
* @version $Id$
*/
@net.sourceforge.mxupdate.update.util.AdminType_mxJPO("expression")
@net.sourceforge.mxupdate.update.util.Path_mxJPO("datamodel/expression")
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("expression")
public class Expression_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMObject_mxJPO
{
    /**
     * Hold the expression itself.
     */
    private String expression = null;

    /**
     * Parses all expression specific expression URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    public void parse(final String _url,
                      final String _content)
    {
        if (_url.startsWith("/expression"))  {
            // to be ignored and read from method prepare because
            // the expression export does not work correctly for XML tags

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * The ranges are sorted.
     *
     * @param _context   context for this request
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        final String cmd = new StringBuilder()
                .append("print expression \"").append(convert(getName()))
                .append("\" select value dump")
                .toString();
        MQLCommand mql = new MQLCommand();
        mql.executeCommand(_context, cmd);
        this.expression = mql.getResult().trim();
        super.prepare(_context);
    }

    /**
     * Writes specific information about the cached expression to the given
     * writer instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "!hidden");
        _out.append(" \\\n    value \"");
        final String expr = convert(this.expression);
        // bug-fix: expression with starting and ending ' (but without ')
        // must have a " as first and last character
        if (expr.matches("^'[^']*'$"))  {
            _out.append("\\\"").append(expr).append("\\\"");
        } else  {
            _out.append(expr);
        }
        _out.append('\"');
    }
}
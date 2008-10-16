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
import java.util.Stack;

import matrix.db.Context;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "table",
                                                     adminTypeSuffix = "system",
                                                     title = "TABLE",
                                                     filePrefix = "TABLE_",
                                                     fileSuffix = ".tcl",
                                                     filePath = "userinterface/table",
                                                     description = "web table")
public class Table_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -518184934631890227L;

    /**
     * Stores all table columns of this web table instance.
     */
    final Stack<net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO> columns = new Stack<net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO>();

    @Override
    protected void parse(final String _url,
                      final String _content)
    {
        if ("/columnList".equals(_url))  {
            // to be ignored...
        } else if ("/columnList/column".equals(_url))  {
            this.columns.add(new net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO());
        } else if (_url.startsWith("/columnList/column/"))  {
            this.columns.peek().parse(_url.substring(18), _content);
        } else  {
            super.parse(_url, _content);
        }
    }

    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        for (final net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO column : this.columns)  {
            _out.append(" \\\n    column");
            column.write(_out);
        }
    }

    /**
     * Appends the MQL statement to reset this web table:
     * <ul>
     * <li>remove all columns of the web table</li>
     * <li>remove all properties</li>
     * <li>set to not hidden</li>
     * </ul>
     *
     * @param _context  context for this request
     * @param _cmd      string builder used to append the MQL statements
     */
    @Override
    protected void appendResetMQL(final Context _context,
                                  final StringBuilder _cmd)
    {
        _cmd.append("mod table \"").append(this.getName()).append("\" system")
            .append(" !hidden");

        // remove all columns
        for (final net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO column : this.columns)  {
            _cmd.append(" column delete name \"").append(column.getName()).append('\"');
        }
        // reset properties
        appendResetProperties(_cmd);
    }
}
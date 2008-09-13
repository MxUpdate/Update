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

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.AdminType_mxJPO(value="table",suffix="system")
@net.sourceforge.mxupdate.update.util.Path_mxJPO("userinterface/table")
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("web table")
public class Table_mxJPO
        extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO
{

    final Stack<net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO> columns = new Stack<net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO>();

    public Table_mxJPO()
    {
        super();
    }

    @Override
    public void parse(final String _url,
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
}
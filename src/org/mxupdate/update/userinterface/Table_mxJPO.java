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

package org.mxupdate.update.userinterface;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Stack;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class Table_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -518184934631890227L;

    /**
     * Stores all table columns of this web table instance.
     *
     * @see #parse(String, String)
     * @see #writeObject(Appendable)
     */
    final Stack<TableColumn_mxJPO> columns = new Stack<TableColumn_mxJPO>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Table_mxJPO(final TypeDef_mxJPO _typeDef,
                       final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all column information from the web table.
     *
     * @param _url      URL to parse
     * @param _content  related content of the URL to parse
     * @see #columns
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/columnList".equals(_url))  {
            // to be ignored...
        } else if ("/columnList/column".equals(_url))  {
            this.columns.add(new TableColumn_mxJPO());
        } else if (_url.startsWith("/columnList/column/"))  {
            this.columns.peek().parse(_url.substring(18), _content);
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes each column to the appendable instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written
     * @see #columns
     * @see TableColumn_mxJPO#write(Writer)
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        for (final TableColumn_mxJPO column : this.columns)  {
            _out.append(" \\\n    column");
            column.write(_out);
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this web table. Following steps
     * are done:
     * <ul>
     * <li>remove all columns of the web table</li>
     * <li>set to not hidden</li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     */
    @Override
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        // set to not hidden
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(' ').append(this.getTypeDef().getMxAdminSuffix())
                .append(" !hidden");

        // remove all columns
        for (final TableColumn_mxJPO column : this.columns)  {
            preMQLCode.append(" column delete name \"").append(column.getName()).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

}
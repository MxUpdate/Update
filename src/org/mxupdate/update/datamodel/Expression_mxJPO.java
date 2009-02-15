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

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
*
 * @author Tim Moxter
* @version $Id$
*/
public class Expression_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2903151847643967098L;

    /**
     * Hold the expression itself.
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeObject(Appendable)
     */
    private String expression = null;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the expression object
     */
    public Expression_mxJPO(final TypeDef_mxJPO _typeDef,
                            final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all expression specific expression URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
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
     * Extract the value of the expression from MX.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the preparation from derived class failed or
     *                         the expression value could not be extraced from
     *                         MX
     * @see #expression
     */
    @Override
    protected void prepare(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final String cmd = new StringBuilder()
                .append("print expression \"").append(convertTcl(getName()))
                .append("\" select value dump")
                .toString();
        this.expression = execMql(_paramCache.getContext(), cmd);
        super.prepare(_paramCache);
    }

    /**
     * Writes specific information about the cached expression to the given
     * writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the expression could not
     *                     be written
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "!hidden");
        _out.append(" \\\n    value \"");
        final String expr = convertTcl(this.expression);
        // bug-fix: expression with starting and ending ' (but without ')
        // must have a " as first and last character
        if (expr.matches("^'[^']*'$"))  {
            _out.append("\\\"").append(expr).append("\\\"");
        } else  {
            _out.append(expr);
        }
        _out.append('\"');
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this expression. Following
     * steps are done:
     * <ul>
     * <li>set to not hidden</li>
     * <li>reset description and value (expression itself)</li>
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
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" !hidden description \"\" value \"\";\n");

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
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
import java.util.Map;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Form_mxJPO
        extends AbstractUIWithFields_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 8819919834817411562L;

    /**
     * TCL procedure used to order fields of form, because Matrix has a bug
     * that sometimes the fields of a form are not in the correct order.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String ORDER_PROC
            = "proc orderFields {_name _fields}  {\n"
                + "foreach offset [list 100000 1] {\n"
                    + "foreach field $_fields {"
                        + "mql mod form \"${_name}\" "
                            + "field modify name \"$field\" "
                            + "order ${offset}\n"
                        + "incr offset\n"
                    + "}\n"
                + "}"
            + "}";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Form_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * The fields of the web form are parsed.
     *
     * @param _url      URL to parse
     * @param _content  related content of the URL to parse
     * @see #fields
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if (_url.startsWith("/footer"))  {
            // to be ignored ...
        } else if (_url.startsWith("/header"))  {
            // to be ignored ...
        } else if (_url.startsWith("/height"))  {
            // to be ignored ...
        } else if (_url.startsWith("/leftMargin"))  {
            // to be ignored ...
        } else if (_url.startsWith("/rightMargin"))  {
            // to be ignored ...
        } else if (_url.startsWith("/webform"))  {
            // to be ignored ...
        } else if (_url.startsWith("/width"))  {
            // to be ignored ...
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes all field of the web form to the TCL update file.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the fields could not be
     *                     written
     * @see AbstractUIWithFields_mxJPO.Field#write(Appendable)
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        for (final Field field : this.getFields())  {
            _out.append(" \\\n    field");
            field.write(_out);
        }
    }

    /**
     * At the end of the TCL update file a call to a procedure must be included
     * to order all web form fields correctly.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     */
    @Override
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
            throws IOException
    {
        _out.append("\n\norderFields \"${NAME}\" [list \\\n");
        for (final Field field : this.getFields())  {
            _out.append("    \"").append(convertTcl(field.getName())).append("\" \\\n");
        }
        _out.append("]");
    }

    /**
     * Creates given web form object with given name. Because the MQL add
     * command for a web form must include the string &quot;web&quot; a
     * specific create method must be written.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the web form could not be created within MX
     */
    @Override
    public void create(final ParameterCache_mxJPO _paramCache)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("add ").append(this.getTypeDef().getMxAdminName())
                        .append(" \"").append(this.getName()).append("\" web;");
        execMql(_paramCache.getContext(), cmd);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this form. Following steps are
     * done:
     * <ul>
     * <li>remove all fields of the web form</li>
     * <li>set to not hidden</li>
     * </ul>
     * The update of web forms works sometimes not correctly for the correct
     * order of fields. Because of that, the TCL update code is includes a
     * procedure to order the form fields.
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
     * @see #ORDER_PROC
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
        // reset HRef, description, alt and label
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" !hidden");

        // remove all fields
        for (final Field field : this.getFields())  {
            preMQLCode.append(" field delete name \"").append(field.getName()).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // append procedure to order fields of the form
        final StringBuilder tclCode = new StringBuilder()
                .append(ORDER_PROC)
                .append('\n')
                .append(_preTCLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, tclCode, _tclVariables, _sourceFile);
    }

}
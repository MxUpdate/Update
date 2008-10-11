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

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "form",
                                                     filePrefix = "FORM_",
                                                     fileSuffix = ".tcl",
                                                     filePath = "userinterface/form",
                                                     description = "web form")
public class Form_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO
{
    /**
     * Stores all fields of this form instance.
     */
    final Stack<net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO> fields = new Stack<net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO>();

    @Override
    protected void parse(String _url, String _content)
    {

        if ("/fieldList".equals(_url))  {
            // to be ignored ...
        } else if ("/fieldList/field".equals(_url))  {
            this.fields.add(new net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO());
        } else if (_url.startsWith("/fieldList/field/"))  {
            this.fields.peek().parse(_url.substring(16), _content);

        } else if (_url.startsWith("/footer"))  {
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

    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        for (final net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO field : this.fields)  {
            _out.write(" \\\n    field");
            field.write(_out);
        }
    }

    @Override
    protected void writeEnd(final Writer _out)
            throws IOException
    {
        _out.append("\n\norderFields \"${NAME}\" [list \\\n");
        for (final net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO field : this.fields)  {
            _out.append("    \"").append(convert(field.name)).append("\" \\\n");
        }
        _out.append("]");
    }

    /**
     * Appends the MQL statement to reset this form:
     * <ul>
     * <li>remove all fields of the web form</li>
     * <li>remove all properties</li>
     * </ul>
     *
     * @param _cmd      string builder used to append the MQL statements
     */
    @Override
    protected void appendResetMQL(final StringBuilder _cmd)
    {
        _cmd.append("mod form \"").append(this.getName()).append('\"');

        // remove all fields
        for (final net.sourceforge.mxupdate.update.userinterface.TableColumn_mxJPO field : this.fields)  {
            _cmd.append(" field delete name \"").append(field.getName()).append('\"');
        }
        // reset properties
        appendResetProperties(_cmd);
    }

}
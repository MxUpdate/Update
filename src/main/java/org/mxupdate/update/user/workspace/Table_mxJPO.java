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
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import matrix.util.MatrixException;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.userinterface.AbstractUIWithFields_mxJPO.Field;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store the workspace object for one table.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Table_mxJPO
    extends AbstractVisualWorkspaceObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for user specific
     * tables.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Table_mxJPO.IGNORED_URLS.add("/columnList");
    }

    /**
     * Regular expression for the user definition of the table. The plus
     * between the backslashes and the dollar sign is because of the wrong JPO
     * conversion within MX.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private static final String EXPR_LINE_USER = "\\\\\n    user \"\\" + "$\\{NAME\\}\"";

    /**
     * Replace expression for the user and units definition of the table. The
     * plus between the backslashes and the dollar sign is because of the wrong
     * JPO conversion within MX.
     *
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private static final String REPLACE_LINE_USER_WITH_UNITS = "\\\\\n    user \"\\" + "$\\{NAME\\}\" \\\\\n    units points";

    /**
     * Stores all columns of this user specific table instance.
     *
     * @see #parse(String, String)
     */
    private final Stack<Field> fields = new Stack<Field>();

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     */
    public Table_mxJPO(final AbstractUser_mxJPO _user)
    {
        super(_user, "table");
    }

    /**
     * <p>Parses all common workspace object specific URL values. This
     * includes:
     * <ul>
     * <li>{@link #name}</li>
     * <li>{@link #hidden hidden flag}</li>
     * <li>{@link #visibleFor user for which this workspace object is visible}
     *     </li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #IGNORED_URLS
     */
    @Override()
    public void parse(final String _url,
                      final String _content)
    {
        if (!Table_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/columnList/column".equals(_url))  {
                this.fields.add(new Field());
            } else if (_url.startsWith("/columnList/column/"))  {
                this.fields.peek().parse(_url.substring(18), _content);
            } else  {
                super.parse(_url, _content);
            }
        }
    }

    /**
     * Because the table export does not set the correct active flag, a MQL
     * print on the table is made and evaluated for the string
     * &quot; active&quot;.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException of preparation failed
     */
    @Override()
    public void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        super.prepare(_paramCache);

        // print table and prove table
        final String bck = MqlUtil_mxJPO.execMql(_paramCache,
                                                 new StringBuilder()
                                                        .append("escape print table \"")
                                                        .append(StringUtil_mxJPO.convertMql(this.getName()))
                                                        .append("\" user \"")
                                                        .append(StringUtil_mxJPO.convertMql(this.getUser().getName())));
        this.setActive(Pattern.compile("\n[ ]*active[ ]*\n").matcher(bck).find());
    }


    /**
     * <p>Writes all table specific values to the TCL update file
     * <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #fields table columns}</li>
     * </ul></p>
     * <p>Because the units definition must behind the user definition, a
     * regular expression is used to replace the user definition by a
     * concatenation of the user with the units definition. As units
     * &quot;points&quot; are used.</p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     * @see #EXPR_LINE_USER
     * @see #REPLACE_LINE_USER_WITH_UNITS
     */
    @Override()
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
        throws IOException
    {
        // the units definition must be directly behind the user!
        final StringBuilder cmd = new StringBuilder();
        super.write(_paramCache, cmd);
        _out.append(cmd.toString().replaceFirst(Table_mxJPO.EXPR_LINE_USER, Table_mxJPO.REPLACE_LINE_USER_WITH_UNITS));

        // web table colums
        for (final Field field : this.fields)  {
            _out.append(" \\\n    column");
            field.write(_out);
        }
    }
}

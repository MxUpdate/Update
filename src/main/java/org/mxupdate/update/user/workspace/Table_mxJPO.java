/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.update.user.workspace;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * User specific class to store the workspace object for one table.
 *
 * @author The MxUpdate Team
 */
public class Table_mxJPO
    extends AbstractVisualWorkspaceObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for user specific
     * tables.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
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
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
//    private final Stack<AbstractField> fields = new Stack<AbstractField>();

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
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override()
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if (Table_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
//        } else if ("/columnList/column".equals(_url))  {
//            this.fields.add(new AbstractField());
//            parsed = true;
//        } else if (_url.startsWith("/columnList/column/"))  {
//            parsed = this.fields.peek().parse(_paramCache, _url.substring(18), _content);
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Because the table export does not set the correct active flag, a MQL
     * print on the table is made and evaluated for the string
     * &quot; active&quot;.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException of preparation failed
     */
//TODO: must be rewritten...
/*
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
*/

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
        /*for (final AbstractField field : this.fields)  {
            _out.append(" \\\n    column");
            field.write(_out);
        }*/
    }
}

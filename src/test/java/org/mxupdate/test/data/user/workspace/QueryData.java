/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.data.user.workspace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * The class is used to define all query objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <USER> class of the related user for which this query is defined
 */
public class QueryData<USER extends AbstractUserData<?>>
    extends AbstractQueryWorkspaceObjectData<QueryData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
        QueryData.REQUIRED_EXPORT_VALUES.put("user", "");
        QueryData.REQUIRED_EXPORT_VALUES.put("vault", "");
        QueryData.REQUIRED_EXPORT_VALUES.put("owner", "");
    }

    /**
     * Must the types expand for the query?
     */
    private boolean expandType = false;

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this query is defined
     * @param _name     name of the query
     */
    public QueryData(final AbstractTest _test,
                     final USER _user,
                     final String _name)
    {
        super(_test, "query", _user, _name, QueryData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Defines if the types for this query must be expanded.
     *
     * @param _expandType   <i>true</i> if types must be expanded; otherwise
     *                      <i>false</i>
     * @return this query instance
     */
    public QueryData<USER> setExpandType(final boolean _expandType)
    {
        this.expandType = _expandType;
        return this;
    }

    /**
     *
     * Appends the type, name and revision defined in the values as
     * business object pattern to the <code>_cmd</code> string builder for
     * the create.
     *
     * @param _cmd  string builder with the MQL commands for the create
     * @throws MatrixException if failed
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        // cache type, name and revision
        final String type = this.getValue("type").toString();
        final String name = this.getValue("name").toString();
        final String revision = this.getValue("revision").toString();

        // remove type, name and revision from values
        this.getValues().remove("type");
        this.getValues().remove("name");
        this.getValues().remove("revision");

        super.append4Create(_cmd);

        // and append again type name revision
        this.setValue("type", type);
        this.setValue("name", name);
        this.setValue("revision", revision);

        _cmd.append(" businessobject")
            .append(" \"").append(AbstractTest.convertMql(type)).append("\"")
            .append(" \"").append(AbstractTest.convertMql(name)).append("\"")
            .append(" \"").append(AbstractTest.convertMql(revision)).append("\"");

        // expand type flag
        if (this.expandType)  {
            _cmd.append(" expandtype");
        } else  {
            _cmd.append(" !expandtype");
        }
    }

    /**
     * Appends the type, name and revision defined in the values as
     * business object pattern to the <code>_cmd</code> string builder for
     * the configuration item file.
     *
     * @param _cmd  string builder with the TCL commands of the configuration
     *              item file
     */
    @Override()
    protected void append4CIFileValues(final StringBuilder _cmd)
    {
        // cache type, name and revision
        final String type = this.getValue("type").toString();
        final String name = this.getValue("name").toString();
        final String revision = this.getValue("revision").toString();

        // cache type, name and revision
        this.getValues().remove("type");
        this.getValues().remove("name");
        this.getValues().remove("revision");

        super.append4CIFileValues(_cmd);

        // and append again type name revision
        this.setValue("type", type);
        this.setValue("name", name);
        this.setValue("revision", revision);

        // business object pattern
        _cmd.append(" \\\n    businessobject")
            .append(" \"").append(AbstractTest.convertTcl(type)).append("\"")
            .append(" \"").append(AbstractTest.convertTcl(name)).append("\"")
            .append(" \"").append(AbstractTest.convertTcl(revision)).append("\"");

        // expand type flag
        if (this.expandType)  {
            _cmd.append(" \\\n    expandtype");
        } else  {
            _cmd.append(" \\\n    !expandtype");
        }
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // cache type, name and revision
        final String type = this.getValue("type").toString();
        final String name = this.getValue("name").toString();
        final String revision = this.getValue("revision").toString();

        // cache type, name and revision
        this.getValues().remove("type");
        this.getValues().remove("name");
        this.getValues().remove("revision");

        super.checkExport(_exportParser);

        // and append again type name revision
        this.setValue("type", type);
        this.setValue("name", name);
        this.setValue("revision", revision);

        // check busines object pattern
        Assert.assertEquals(_exportParser.getLines("/mql/businessobject/@value").size(),
                            1,
                            "check that minimum and maximum one business object pattern is defined");
        Assert.assertEquals(_exportParser.getLines("/mql/businessobject/@value").get(0),
                            "\"" + AbstractTest.convertTcl(type) + "\" \"" + AbstractTest.convertTcl(name) + "\" \""
                                        + AbstractTest.convertTcl(revision) + "\"",
                            "check that business object pattern is correct defined");

        // check expand type flag
        final Set<String> main = new HashSet<String>(_exportParser.getLines("/mql/"));
        if (this.expandType)  {
            Assert.assertTrue(main.contains("expandtype") || main.contains("expandtype \\"),
                              "check that query has expand type");
        } else  {
            Assert.assertTrue(main.contains("!expandtype") || main.contains("!expandtype \\"),
                              "check that has no expand type");
        }
    }
}

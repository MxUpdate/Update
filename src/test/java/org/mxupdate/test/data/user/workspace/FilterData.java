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
 * The class is used to define all filter objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <USER> class of the related user for which this filter is defined
 */
public class FilterData<USER extends AbstractUserData<?>>
    extends AbstractVisualQueryWorkspaceObjectData<FilterData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
        FilterData.REQUIRED_EXPORT_VALUES.put("user", "");
        FilterData.REQUIRED_EXPORT_VALUES.put("appliesto", "");
        FilterData.REQUIRED_EXPORT_VALUES.put("type", "");
        FilterData.REQUIRED_EXPORT_VALUES.put("name", "");
        FilterData.REQUIRED_EXPORT_VALUES.put("revision", "");
        FilterData.REQUIRED_EXPORT_VALUES.put("vault", "");
        FilterData.REQUIRED_EXPORT_VALUES.put("owner", "");
    }

    /**
     * Defines the direction of the connections to which the filter applies.
     */
    private Direction direction = Direction.BOTH;

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this filter is defined
     * @param _name     name of the filter
     */
    public FilterData(final AbstractTest _test,
                      final USER _user,
                      final String _name)
    {
        super(_test, "filter", _user, _name, FilterData.REQUIRED_EXPORT_VALUES);
        this.setValue("appliesto", "relationship");
    }

    /**
     * Defines the direction of the connections this filter applies.
     *
     * @param _direction    direction of the connections
     * @return this filter data instance
     * @see #direction
     */
    public FilterData<USER> setDirection(final Direction _direction)
    {
        this.direction = _direction;
        return this;
    }

    /**
     * Returns the part of the CI file to create this filter of an user.
     *
     * @return part of the CI file to create this filter of an user
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder().append(super.ciFile());

        // active flag
        if (this.direction != null)  {
            cmd.append(" \\\n    ").append(this.direction.toString().toLowerCase());
        }

        return cmd.toString();
    }

    /**
     * Appends the MQL commands to define the {@link #direction} within a
     * create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #values
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // direction
        if (this.direction != null)  {
            _cmd.append(" ").append(this.direction.toString().toLowerCase());
        }
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * The {@link #active active flag} is checked.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check direction
        final Set<String> main = new HashSet<String>(_exportParser.getLines("/mql/"));
        switch (this.direction)  {
            case TO:
                Assert.assertTrue(main.contains("to") || main.contains("to \\"),        "check to direction");
                Assert.assertFalse(main.contains("from") || main.contains("from \\"),   "check no from direction");
                Assert.assertFalse(main.contains("both") || main.contains("both \\"),   "check no both direction");
                break;
            case FROM:
                Assert.assertFalse(main.contains("to") || main.contains("to \\"),       "check no to direction");
                Assert.assertTrue(main.contains("from") || main.contains("from \\"),    "check from direction");
                Assert.assertFalse(main.contains("both") || main.contains("both \\"),   "check no both direction");
                break;
            case BOTH:
            default:
                Assert.assertFalse(main.contains("to") || main.contains("to \\"),       "check no to direction");
                Assert.assertFalse(main.contains("from") || main.contains("from \\"),   "check no from direction");
                Assert.assertTrue(main.contains("both") || main.contains("both \\"),    "check both direction");
        }
    }

    /**
     * Enumeration for the direction of connection.
     */
    public enum Direction
    {
        /**
         * To direction.
         */
        TO,

        /**
         * From direction.
         */
        FROM,

        /**
         * Both (to and from) directions.
         */
        BOTH;
    }
}

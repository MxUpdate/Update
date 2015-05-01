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

package org.mxupdate.test.data.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * Property definition list.
 *
 * @author The MxUpdate Team
 */
public class PropertyDefList
    extends HashSet<PropertyDef>
{
    /** Serial Version UID. */
    private static final long serialVersionUID = -4233652216452909465L;

    /**
     * Creates all depending administration objects for given this instance.
     * Only the depending properties could be created.
     *
     * @return this data instance
     * @throws MatrixException if create failed
     */
    public PropertyDefList createDependings()
        throws MatrixException
    {
        for (final PropertyDef prop : this)  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }

        return this;
    }

    /**
     * Appends the MQL commands to define all properties within a create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     */
    public void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        for (final PropertyDef property : this)  {
            _cmd.append(" property \"").append(AbstractTest.convertMql(property.getName())).append("\"");
            if (property.getTo() != null)  {
                property.getTo().create();
                _cmd.append(" to ").append(property.getTo().getCI().getMxType()).append(" \"")
                    .append(AbstractTest.convertMql(property.getTo().getName())).append("\"");
                if (property.getTo().getCI() == AbstractTest.CI.UI_TABLE)  {
                    _cmd.append(" system");
                }
            }
            if (property.getValue() != null)  {
                _cmd.append(" value \"").append(AbstractTest.convertMql(property.getValue())).append("\"");
            }
        }
    }

    /**
     * Checks that all properties within the export file are correct defined
     * and equal to the defined properties of this CI file.
     *
     * @param _exportParser     parsed export
     * @deprecated replaced by new format embedded in delta description
     */
    @Deprecated()
    public void checkExportPropertiesAddFormat(final ExportParser _exportParser,
                                               final CI _ci)
    {
        // only if this instance is a configuration item the check is done..
        if (_ci != null)  {
            final Set<String> propDefs = new HashSet<String>();
            for (final ExportParser.Line rootLine : _exportParser.getRootLines())  {
                if (rootLine.getValue().startsWith("escape add property"))  {
                    final StringBuilder propDef = new StringBuilder().append("mql ").append(rootLine.getValue());
                    for (final ExportParser.Line childLine : rootLine.getChildren())  {
                        propDef.append(' ').append(childLine.getTag()).append(' ')
                               .append(childLine.getValue());
                    }
                    propDefs.add(propDef.toString());
                }
            }
            for (final PropertyDef prop : this)  {
                final String propDefStr = prop.getCITCLString(_ci);
                Assert.assertTrue(
                        propDefs.contains(propDefStr),
                        "check that property is defined in ci file (have " + propDefStr + ", but found " + propDefs + ")");
                propDefs.remove(propDefStr);
            }

            Assert.assertEquals(propDefs.size(), 0, "check that not too much properties are defined (have " + propDefs + ")");
        }
    }

    /**
     * Checks that all properties within the export file are correct defined
     * and equal to the defined properties of this CI file.
     *
     * @param _propLines    property lines
     */
    public void checkExport(final List<String> _propLines)
    {
        final Set<String> propDefs = new HashSet<String>(_propLines);

        for (final PropertyDef prop : this)  {
            final String propDefStr = prop.getCIUpdateFormat();
            Assert.assertTrue(
                    propDefs.contains(propDefStr),
                    "check that property is defined in ci file (have " + propDefStr + ", but found " + propDefs + ")");
            propDefs.remove(propDefStr);
        }

        Assert.assertEquals(propDefs.size(), 0, "check that not too much properties are defined (have " + propDefs + ")");
    }

    /**
     * Appends the complete CI definition in update format.
     *
     * @param _prefix   prefix in front of a property definition
     * @param _str      string builder
     */
    public void appendUpdate(final String _prefix,
                             final StringBuilder _str)
    {
        for (final PropertyDef prop : this)  {
            _str.append(_prefix).append("property ").append(prop.getCIUpdateFormat()).append('\n');
        }
    }
}

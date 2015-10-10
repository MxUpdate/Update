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

package org.mxupdate.test.data.datamodel.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.ExportParser.Line;
import org.mxupdate.test.data.datamodel.PathTypeData;
import org.testng.Assert;

/**
 * List of path types used locally.
 *
 * @author The MxUpdate Team
 */
public class LocalPathTypeDataList
    extends ArrayList<PathTypeData>
{
    /** Required dummy serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Method to append all local path types to CI file.
     *
     * @param _prefix   prefix
     * @param _cmd      string builder to append
     */
    public void append4Update(final String _prefix,
                              final StringBuilder _cmd)
    {
        for (final PathTypeData pathType : this)  {
            _cmd.append(_prefix).append("local pathtype \"").append(pathType.getName()).append("\" {\n");
            pathType.append4Update(_prefix + "    ", _cmd);
            _cmd.append(_prefix).append("}\n");
        }
    }

    /**
     * Checks that the attribute list is correct exported.
     *
     * @param _exportParser     export parser
     */
    public void checkExport(final ExportParser _exportParser)
    {
        // prepare list of defined attributes
        final SortedMap<String,PathTypeData> defPathTypes = new TreeMap<>();
        for (final PathTypeData pathType : this)  {
            defPathTypes.put("pathtype \"" + AbstractTest.convertUpdate(pathType.getName()) +  "\" {", pathType);
        }
        // prepare list of parsed attributes
        final SortedMap<String,Line> linePathTypes = new TreeMap<>();
        for (final Line line : _exportParser.getRootLines())  {
            for (final Line sub : line.getChildren())  {
                if ("local".equals(sub.getTag()) && sub.getValue().startsWith("pathtype"))  {
                    linePathTypes.put(sub.getValue(), sub);
                }
            }
        }
        // and check that both list are equal
        Assert.assertEquals(linePathTypes.keySet(), defPathTypes.keySet(), "check all local path types are defined");
        final Iterator<PathTypeData> defIter = defPathTypes.values().iterator();
        final Iterator<Line> lineIter = linePathTypes.values().iterator();
        while (defIter.hasNext())  {
            final PathTypeData defAttr = defIter.next();
            final Line lineAttr = lineIter.next();
            defAttr.checkExport(new ExportParser("pathtype", new Line[]{lineAttr}));
        }
    }
}

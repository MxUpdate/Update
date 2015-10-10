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

package org.mxupdate.test.test.update.datamodel.relationshipci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PathTypeData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the export of {@link Relationship_mxJPO relationship CI}.
 *
 * @author The MxUpdate Team
 */
public class RelationshipCI_5ExportTest
    extends AbstractTest
{
    /**
     * Positive test local path types.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test local path types")
    public void positiveTestSortLocalPathTypes()
        throws Exception
    {
        final String code = new RelationshipData(this, "Test")
                .create()
                .addLocalPathType(new PathTypeData(this, "PathType 2"))
                .addLocalPathType(new PathTypeData(this, "PathType 1"))
                .update("")
                .export()
                .getCode();
        Assert.assertTrue(
                code.indexOf("PathType 1") < code.indexOf("PathType 2"),
                "check correct order");
    }

    /**
     * Removes the MxUpdate programs, attributes and types.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_PATHTYPE);  // as first, so that local attributes of path types are deleted!
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
    }
}

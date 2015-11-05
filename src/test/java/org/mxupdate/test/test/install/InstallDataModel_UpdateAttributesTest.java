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

package org.mxupdate.test.test.install;

import java.util.Arrays;
import java.util.TreeSet;

import org.mxupdate.install.InstallDataModel_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the JPO method {@link InstallDataModel_mxJPO#updateAttributes}.
 *
 * @author The MxUpdate Team
 */
public class InstallDataModel_UpdateAttributesTest
    extends AbstractTest
{
    /**
     * Positive test that attribute is created.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test that attribute is created")
    public void positiveTestAttributeIsCreated()
        throws Exception
    {
        final String attrName = AbstractTest.PREFIX + "Test";

        Assert.assertEquals(this.mql().cmd("escape list attribute ").arg(attrName).exec(this.getContext()), "");

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        new InstallDataModel_mxJPO() {
            {
                this.updateAttributes(
                        paramCache,
                        new TreeSet<>(Arrays.asList(new String[]{attrName})),
                        "APPL VERSION",
                        "FILE DATE",
                        "INSTALLED DATE");
            }
        };

        new AttributeData(this, "Test")
                .setSingle("kind", "string")
                .defText("property", "\"application\" value \"MxUpdate\"")
                .defText("property", "\"author\" value \"The MxUpdate Team\"")
                .defText("property", "\"original name\" value \"" + attrName + "\"")
                .defText("property", "\"version\" value \"APPL VERSION\"")
                .checkExport();

        Assert.assertEquals(
                this.mql().cmd("escape print attribute ").arg(attrName).cmd(" select ").arg("property[installer].value").cmd(" dump").exec(this.getContext()),
                "The MxUpdate Team");
        Assert.assertEquals(
                this.mql().cmd("escape print attribute ").arg(attrName).cmd(" select ").arg("property[file date].value").cmd(" dump").exec(this.getContext()),
                "FILE DATE");
        Assert.assertEquals(
                this.mql().cmd("escape print attribute ").arg(attrName).cmd(" select ").arg("property[installed date].value").cmd(" dump").exec(this.getContext()),
                "INSTALLED DATE");

        this.mql().cmd("escape mod attribute ").arg(attrName).cmd(" add property ").arg("author")  .cmd(" value ").arg("Dummy").exec(this.getContext());
        this.mql().cmd("escape mod attribute ").arg(attrName).cmd(" add property ").arg("installer").cmd(" value ").arg("Dummy").exec(this.getContext());

        new InstallDataModel_mxJPO() {
            {
                this.updateAttributes(
                        paramCache,
                        new TreeSet<>(Arrays.asList(new String[]{attrName})),
                        "APPL VERSION 2",
                        "FILE DATE 2",
                        "INSTALLED DATE 2");
            }
        };

        new AttributeData(this, "Test")
                .setSingle("kind", "string")
                .defText("property", "\"application\" value \"MxUpdate\"")
                .defText("property", "\"author\" value \"The MxUpdate Team\"")
                .defText("property", "\"original name\" value \"" + attrName + "\"")
                .defText("property", "\"version\" value \"APPL VERSION 2\"")
                .checkExport();

        Assert.assertEquals(
                this.mql().cmd("escape print attribute ").arg(attrName).cmd(" select ").arg("property[installer].value").cmd(" dump").exec(this.getContext()),
                "Dummy");
        Assert.assertEquals(
                this.mql().cmd("escape print attribute ").arg(attrName).cmd(" select ").arg("property[file date].value").cmd(" dump").exec(this.getContext()),
                "FILE DATE");
        Assert.assertEquals(
                this.mql().cmd("escape print attribute ").arg(attrName).cmd(" select ").arg("property[installed date].value").cmd(" dump").exec(this.getContext()),
                "INSTALLED DATE");
     }

    /**
     * Cleanup all test data.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.DM_ATTRIBUTE);
    }
}

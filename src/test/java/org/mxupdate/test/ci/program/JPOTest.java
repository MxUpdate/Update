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

package org.mxupdate.test.ci.program;

import org.mxupdate.test.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of JPO programs.
 *
 * @author The MxUpdate Team
 */
@Test()
public class JPOTest
    extends AbstractTest
{
    /**
     * Check that the end tag of CDATA is correct translated to
     * 'Inserted_by_ENOVIA'.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the end tag of CDATA is correct translated to 'Inserted_by_ENOVIA'")
    public void checkCDataTranslation()
        throws Exception
    {
        final String name = AbstractTest.PREFIX + "_Test";
        this.mql("add prog " + name + " java code '<]]>'");
        final String xml = this.mql("export prog " + name + " xml");
        Assert.assertTrue(xml.indexOf("<code><![CDATA[<]Inserted_by_ENOVIA]Inserted_by_ENOVIA>]]></code>") >= 0,
                          "check translation of the CDATA conversion");
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.PRG_JPO);
        this.cleanup(CI.USR_PERSON);
    }
}

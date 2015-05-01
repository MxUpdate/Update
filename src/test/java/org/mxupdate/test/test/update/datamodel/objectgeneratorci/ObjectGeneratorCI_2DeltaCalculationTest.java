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

package org.mxupdate.test.test.update.datamodel.objectgeneratorci;

import java.io.File;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.BusData;
import org.mxupdate.test.data.datamodel.NotificationData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the notification CI delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class ObjectGeneratorCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<BusObject_mxJPO,NotificationData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) description",
                    new BusData(this, CI.DM_OBJECTGENERATOR, "TestName", "Revision"),
                    new BusData(this, CI.DM_OBJECTGENERATOR, "TestName", "Revision").defKeyNotDefined("type").setValue("description", "abc")},
            {"2) Subject Text",
                    new BusData(this, CI.DM_OBJECTGENERATOR, "TestName", "Revision"),
                    new BusData(this, CI.DM_OBJECTGENERATOR, "TestName", "Revision").defKeyNotDefined("type").setKeyValue("attribute", "eService Name Prefix", "test")},
       };
    }

    /**
     * Positive test to connect to number generator.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to connect to number generator")
    public void positiveTestConnect()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final WrapperCIInstance<BusObject_mxJPO> n1 = new WrapperCIInstance<BusObject_mxJPO>(new BusObject_mxJPO(paramCache.getMapping().getTypeDef(CI.DM_NUMBERGENERATOR.updateType), "MXUPDATE_Test________N1"));
        final WrapperCIInstance<BusObject_mxJPO> n2 = new WrapperCIInstance<BusObject_mxJPO>(new BusObject_mxJPO(paramCache.getMapping().getTypeDef(CI.DM_NUMBERGENERATOR.updateType), "MXUPDATE_Test________N2"));
        final WrapperCIInstance<BusObject_mxJPO> o1 = new WrapperCIInstance<BusObject_mxJPO>(new BusObject_mxJPO(paramCache.getMapping().getTypeDef(CI.DM_OBJECTGENERATOR.updateType), "MXUPDATE_Test________O"));
        final WrapperCIInstance<BusObject_mxJPO> o2 = new WrapperCIInstance<BusObject_mxJPO>(new BusObject_mxJPO(paramCache.getMapping().getTypeDef(CI.DM_OBJECTGENERATOR.updateType), "MXUPDATE_Test________O"));
        final WrapperCIInstance<BusObject_mxJPO> o3 = new WrapperCIInstance<BusObject_mxJPO>(new BusObject_mxJPO(paramCache.getMapping().getTypeDef(CI.DM_OBJECTGENERATOR.updateType), "MXUPDATE_Test________O"));

        o1.create(paramCache);
        n1.create(paramCache);
        n2.create(paramCache);

        // assign number generator n1
        o1.parseUpdate("connection \"eService Number Generator\" to { type \"eService Number Generator\" name \"MXUPDATE_Test\" revision \"N1\" }");
        o1.store((File) null, paramCache);
        Assert.assertEquals(
                this.mql("print bus 'eService Object Generator' 'MXUPDATE_Test' 'O' select from[eService Number Generator] dump"),
                "True");

        // assign number generator n2
        o2.parseUpdate("connection \"eService Number Generator\" to { type \"eService Number Generator\" name \"MXUPDATE_Test\" revision \"N2\" }");
        o2.store((File) null, paramCache);
        Assert.assertEquals(
                this.mql("print bus 'eService Object Generator' 'MXUPDATE_Test' 'O' select from[eService Number Generator] dump"),
                "True");

        // remove all number generator
        o3.parseUpdate("");
        o3.store((File) null, paramCache);
        Assert.assertEquals(
                this.mql("print bus 'eService Object Generator' 'MXUPDATE_Test' 'O' select from[eService Number Generator] dump"),
                "False");
    }


    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_OBJECTGENERATOR);
        this.cleanup(AbstractTest.CI.DM_NUMBERGENERATOR);
    }

    @Override()
    protected BusObject_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new BusObject_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_OBJECTGENERATOR.updateType), _name);
    }
}

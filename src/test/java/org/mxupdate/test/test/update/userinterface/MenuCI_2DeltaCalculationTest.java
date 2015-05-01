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

package org.mxupdate.test.test.update.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.userinterface.MenuData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Menu_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Menu_mxJPO menu} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class MenuCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Menu_mxJPO,MenuData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
                {"simple test",
                    new MenuData(this, "Menu1"),
                    new MenuData(this, "Menu1").setValue("description", "test").setValue("label", "").setValue("href", "").setValue("alt", "")},
                {"tree menu",
                    new MenuData(this, "Menu1"),
                    new MenuData(this, "Menu1").setTreeMenu(true)},
                {"property name and value",
                    new MenuData(this, "Menu1"),
                    new MenuData(this, "Menu1").addProperty(new PropertyDef("property", "value"))},
       };
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_MENU);
    }

    @Override()
    protected Menu_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Menu_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_MENU.updateType), _name);
    }
}

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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.userinterface.FormData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Form_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Form_mxJPO form CI} delta calculator.
 *
 * @author The MxUpdate Team
 */
@Test
public class FormCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Form_mxJPO,FormData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) uuid",
                    new FormData(this, "Test"),
                    new FormData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"2a) symbolic name",
                    new FormData(this, "Test"),
                    new FormData(this, "Test")
                            .setValue("symbolicname", "expression_123")},
            {"2b) two symbolic name",
                    new FormData(this, "Test"),
                    new FormData(this, "Test")
                            .setValue("symbolicname", "expression_123")
                            .setValue("symbolicname", "expression_345")},

            // field add / remove
            {"3a) field add to empty",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field").setValue("label", "an \"label\"").getFormTable()},
            {"3b) two field add to empty",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field") .setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable()},
            {"3c) field remove all",
                    new FormData(this, "Form1")
                            .setValue("description", "description")
                            .newField("field") .setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                    new FormData(this, "Form1")
                            .setValue("description", "description")},
            {"3d) field remove one field",
                    new FormData(this, "Form1")
                            .setValue("description", "description")
                            .newField("field") .setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                    new FormData(this, "Form1")
                            .setValue("description", "description")
                            .newField("field2").setValue("label", "an \"label\"").getFormTable()},
            {"3e) field add",
                    new FormData(this, "Form1")
                            .setValue("description", "description")
                            .newField("field") .setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                    new FormData(this, "Form1")
                            .setValue("description", "description")
                            .newField("field") .setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable()
                            .newField("field3").setValue("label", "an \"label\"").getFormTable()},
            // field set values
            {"10a) set business object expression",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").setValue("businessobject", "value.new").getFormTable()},
            {"10b) set relationship expression",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").setValue("relationship", "value.new").getFormTable()},
            {"11) set label",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").setValue("label", "value.new").getFormTable()},
            {"12) new field with alt",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").setValue("alt", "value.new").getFormTable()},
            {"13) new field with href",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").setValue("href", "value.new").getFormTable()},
            {"14) new field with range href",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").setValue("range", "value.new").getFormTable()},
            {"17) set user",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").defData("user", new PersonAdminData(this, "Test.Old")).getFormTable()},
            {"18) new setting",
                    new FormData(this, "Form1"),
                    new FormData(this, "Form1")
                            .newField("field").setKeyValue("setting", "key", "value.new").getFormTable()},
            // field update values
            {"20a) update business object expression",
                    new FormData(this, "Form1")
                            .newField("field").setValue("businessobject", "label.old").getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").setValue("businessobject", "label.new").getFormTable()},
            {"20b) update relationship expression",
                    new FormData(this, "Form1")
                            .newField("field").setValue("relationship", "label.old").getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").setValue("relationship", "label.new").getFormTable()},
            {"21) update label",
                    new FormData(this, "Form1")
                            .newField("field").setValue("label", "label.old").getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").setValue("label", "label.new").getFormTable()},
            {"22) update alt",
                    new FormData(this, "Form1")
                            .newField("field").setValue("alt", "label.old").getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").setValue("alt", "label.new").getFormTable()},
            {"23) update hraf",
                    new FormData(this, "Form1")
                            .newField("field").setValue("href", "label.old").getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").setValue("href", "label.new").getFormTable()},
            {"24) update range href",
                    new FormData(this, "Form1")
                            .newField("field").setValue("range", "value.old").getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").setValue("range", "value.new").getFormTable()},
            {"27) update user",
                    new FormData(this, "Form1")
                            .newField("field").defData("user", new PersonAdminData(this, "Test.Old")).getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").defData("user", new PersonAdminData(this, "Test.New")).getFormTable()},
            {"28) update setting",
                    new FormData(this, "Form1")
                            .newField("field").setKeyValue("setting", "key", "value.old").getFormTable(),
                    new FormData(this, "Form1")
                            .newField("field").setKeyValue("setting", "key", "value.new").getFormTable()},
        };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSON);
        this.cleanup(AbstractTest.CI.UI_FORM);
    }

    @Override
    protected Form_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Form_mxJPO(_name);
    }
}

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

package org.mxupdate.test.test.update;

import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link BusObject_mxJPO business object CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class BusObjectCI_1ParserWithDerivedTest
    extends AbstractParserTest<BusObject_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"1) simple",
                    "",
                    "type \"IEF-EBOMSyncConfig\" description \"\" current \"\""},
            // type
            {"2a) type",
                    "",
                    "type \"Trigger\" description \"\" current \"\""},
            {"2b) type not defined",
                    "type \"IEF-EBOMSyncConfig\" description \"\" current \"\"",
                    "                            description \"\" current \"\""},
            // description
            {"3a) description",
                    "",
                    "type \"IEF-EBOMSyncConfig\" description \"abc def\" current \"\""},
            {"3b) description not defined",
                    "type \"IEF-EBOMSyncConfig\" description \"\" current \"\"",
                    "type \"IEF-EBOMSyncConfig\"                  current \"\""},
            {"3c) multi-line description",
                    "",
                    "type \"IEF-EBOMSyncConfig\" description \"abc\ndef\" current \"\""},

            // attribute
            {"4a) one attribute ",
                    "",
                    "type \"IEF-EBOMSyncConfig\" description \"\" current \"\" attribute \"ATTRNAME\" \"ATTRVALUE\""},
            // attribute
            {"4b) various attributes ",
                    "type \"IEF-EBOMSyncConfig\" description \"\" current \"\" attribute \"ATTRNAME\"  \"ATTRVALUE\" attribute \"ATTRNAME1\" \"ATTRVALUE\" attribute \"ATTRNAME2\" \"ATTRVALUE\"",
                    "type \"IEF-EBOMSyncConfig\" description \"\" current \"\" attribute \"ATTRNAME2\" \"ATTRVALUE\" attribute \"ATTRNAME1\" \"ATTRVALUE\" attribute \"ATTRNAME\"  \"ATTRVALUE\""},
            {"4c) mulitine attribute value",
                    "",
                    "type \"IEF-EBOMSyncConfig\" description \"\" current \"\" attribute \"ATTRNAME\" \"ATTR\n\nVALUE\""},

            // connection
            {"5a) one connection",
                    "",
                    "type \"Trigger\" description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\""},
            {"5b) connection with attribute",
                    "",
                    "type \"Trigger\" description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\" { attribute \"ATTRNAME\" \"ATTRVALUE\" }"},
            {"5c) connection with various attributes ",
                    "type \"Trigger\" description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\" { attribute \"ATTRNAME1\" \"ATTRVALUE\" attribute \"ATTRNAME2\" \"ATTRVALUE\" }",
                    "type \"Trigger\" description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\" { attribute \"ATTRNAME2\" \"ATTRVALUE\" attribute \"ATTRNAME1\" \"ATTRVALUE\" }"},
        };
    }

    @Override()
    protected BusObject_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new BusObject_mxJPO(_paramCache.getMapping().getTypeDef("IEFEBOMSyncConfig"), _name);
    }
}

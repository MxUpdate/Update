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

package org.mxupdate.test.ci.integration;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.integration.IEFGlobalConfigData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

/**
 * Test cases for the export and update of integration global configuration
 * objects.
 *
 * @author The MxUpdate Team
 */
public class IEFGlobalConfigTest
    extends AbstractIEFTest<IEFGlobalConfigData>
{
    /**
     * {@inheritDoc}
     * Returns new IEF global configuration instance.
     */
    @Override()
    protected IEFGlobalConfigData createNewData(final boolean _subType,
                                                final String _name)
    {
        return new IEFGlobalConfigData(
                this,
                _subType ? new TypeData(this, "GlobalConfig").setValue("derived", "MCADInteg-GlobalConfig") : null,
                _name,
                "1");
    }

    /**
     * Cleanup all test integration global configuration objects.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.IEF_GLOBAL_CONFIG);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }
}

/*
 * Copyright 2008-2011 The MxUpdate Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test.ci.integration;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.integration.IEFUnassignedRegistryData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

/**
 * Test cases for the export and update of integration unassigned registry
 * objects.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class IEFUnassignedRegistryTest
    extends AbstractIEFTest<IEFUnassignedRegistryData>
{
    /**
     * {@inheritDoc}
     * Returns new IEF unassigned registry instance.
     */
    @Override()
    protected IEFUnassignedRegistryData createNewData(final boolean _subType,
                                                      final String _name)
    {
        return new IEFUnassignedRegistryData(
                this,
                _subType ? new TypeData(this, "UnassignedRegistry").setValue("derived", "IEF-UnassignedIntegRegistry") : null,
                _name,
                "-");
    }

    /**
     * Cleanup all test integration unassigned registry objects.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.IEF_UNASSIGNED_REGISTRY);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }
}

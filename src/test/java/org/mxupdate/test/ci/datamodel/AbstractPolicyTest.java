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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.util.Version;
import org.testng.annotations.BeforeMethod;

/**
 * Common definitions for the policy tests.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractPolicyTest
    extends AbstractDataExportUpdate<PolicyData>
{
    /**
     * Deletes all test data model object.
     *
     * @throws Exception if clean up failed
     */
    @BeforeMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_POLICY);
        this.cleanup(AbstractTest.CI.DM_TYPE);
        this.cleanup(AbstractTest.CI.DM_FORMAT);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_ROLE);
   }

    /**
     * Creates for given <code>_name</code> a new policy instance.
     *
     * @param _name     name of the policy instance
     * @return policy instance
     */
    @Override()
    protected PolicyData createNewData(final String _name)
    {
        return new PolicyData(this, _name);
    }

    /**
     * Creates a clean data instance used to update an existing data instance.
     *
     * @param _original     original data instance
     * @return new data instance (where all original data is cleaned)
     */
    @Override()
    protected PolicyData createCleanNewData(final PolicyData _original)
    {
        final PolicyData ret = super.createCleanNewData(_original);

        // if delimiter is defined, must be also defined for new cleaned policy
        if (_original.getValue("delimiter") != null)  {
            ret.setValue("delimiter", _original.getValue("delimiter"));
            // if delimiter is defined, major / minor sequence must be defined
            // (the trick: they are changed vvs.)
            ret.setValue("minorsequence", _original.getValue("majorsequence"));
            ret.setValue("majorsequence", _original.getValue("minorsequence"));
        } else  {
            ret.setValue("sequence",      "");
        }

        // states can not be removed....
        for (final State state : _original.getStates())  {
            final State retState = new State().setName(state.getName());

            if (this.getVersion() != Version.V6R2011x)  {
                retState.setValue("published", "false");
            }

            ret.addState(retState);
        }

        return ret;
    }
}

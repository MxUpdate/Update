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

package org.mxupdate.test.test.update.datamodel.policyci;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.util.Version;
import org.testng.annotations.BeforeMethod;

/**
 * Common definitions for the policy tests.
 *
 * @author The MxUpdate Team
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
            ret.setFlag("delimiter", _original.getFlags().getValue("delimiter"));
            // if delimiter is defined, major / minor sequence must be defined
            // (the trick: they are changed vvs.)
            ret.setFlag("minorsequence", _original.getFlags().getValue("majorsequence"));
            ret.setFlag("majorsequence", _original.getFlags().getValue("minorsequence"));
        } else  {
            ret.setValue("sequence",      "");
        }

        // states can not be removed....
        for (final State state : _original.getStates())  {
            final State retState = new State().setName(state.getName());

            if (this.getVersion() != Version.V6R2011x)  {
                retState.setFlag("published", false, Create.ViaValue);
            }

            ret.addState(retState);
        }

        return ret;
    }
}

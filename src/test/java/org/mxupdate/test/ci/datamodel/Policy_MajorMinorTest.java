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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for policy exports of the major / minor information.
 *
 * @author The MxUpdate Team
 */
public class Policy_MajorMinorTest
    extends AbstractPolicyTest
{
    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"178"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return this.prepareData((String) null,
                // sequence
                new Object[]{
                        "policy with minor sequence (w/o delimiter)",
                        new PolicyData(this, "test")
                                .setValue("sequence", "1,2,3,A,B,C")},
                new Object[]{
                        "issue #178: policy with minor sequence ('minorsequence', but  w/o delimiter)",
                        new PolicyData(this, "test")
                                .setValue("minorsequence", "1,2,3,A,B,C"),
                        new PolicyData(this, "test")
                                .setValue("sequence", "1,2,3,A,B,C")},
                new Object[]{
                        "issue #178: policy with delimiter and required minor / major sequence",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setValue("delimiter", '.')
                                .setValue("minorsequence", "A,B,C")
                                .setValue("majorsequence", "1,2,3")},
                new Object[]{
                        "issue #178: policy with delimiter and required minor / major sequence ('sequence' key instead of 'minorsequence')",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setValue("delimiter", '.')
                                .setValue("sequence", "A,B,C")
                                .setValue("majorsequence", "1,2,3"),
                        new PolicyData(this, "test")
                                .setValue("delimiter", '.')
                                .setValue("minorsequence", "A,B,C")
                                .setValue("majorsequence", "1,2,3")},
                // revisionable (V6R2011x)
                new Object[]{
                        "issue #178 (V6R2011x): policy with state revisionable",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2013x, Version.V6R2014x, Version.V6R2015x)
                                .addState(new State()
                                            .setName("create")
                                            .setValue("revision", "true"))},
                new Object[]{
                        "issue #178 (V6R2011x): policy with state not revisionable",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2013x, Version.V6R2014x, Version.V6R2015x)
                                .addState(new State()
                                            .setName("create")
                                            .setValue("revision", "false"))},
                new Object[]{
                        "issue #178 (V6R2011x): policy with state revisionable (defined in new style)",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2013x, Version.V6R2014x, Version.V6R2015x)
                                .addState(new State()
                                            .setName("create")
                                            .setValue("minorrevision", "true")),
                        new PolicyData(this, "test")
                                .addState(new State()
                                            .setName("create")
                                            .setValue("revision", "true"))},
                // major / minor revisionable (V6R2013x)
                new Object[]{
                        "issue #178: policy state w/o defined revisionable flag",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                            .setName("create")),
                        new PolicyData(this, "test")
                                .addState(new State()
                                            .setName("create")
                                            .setValue("majorrevision", "false")
                                            .setValue("minorrevision", "false"))},
               new Object[]{
                        "issue #178: policy state with revisionable (defined in old style)",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                            .setName("create")
                                            .setValue("revision", "true")),
                        new PolicyData(this, "test")
                                .addState(new State()
                                            .setName("create")
                                            .setValue("majorrevision", "false")
                                            .setValue("minorrevision", "true"))},
                new Object[]{
                        "issue #178: policy state with revisionable",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                            .setName("create")
                                            .setValue("majorrevision", "false")
                                            .setValue("minorrevision", "true"))},
                new Object[]{
                        "issue #178: policy state with not minor revisionable",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                            .setName("create")
                                            .setValue("majorrevision", "false")
                                            .setValue("minorrevision", "false"))},
                new Object[]{
                        "issue #178: policy state with major revisionable",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                            .setName("create")
                                            .setValue("majorrevision", "true")
                                            .setValue("minorrevision", "false"))}
        );
    }

    /**
     * Negative test that update failed for modified delimiter
     *
     * @throws Exception
     */
    @IssueLink({"178"})
    @Test(description = "issue #178: negative test that update failed for modified delimiter")
    public void negativeTestUpdateDelimiter()
        throws Exception
    {
        if (this.getVersion() != Version.V6R2011x)  {
            new PolicyData(this, "test")
                    .setValue("delimiter", '.')
                    .setValue("minorsequence", "A,B,C")
                    .setValue("majorsequence", "1,2,3")
                    .update((String) null)
                    .checkExport()
                    .setValue("delimiter", '|')
                    .failureUpdate(ErrorKey.DM_POLICY_UPDATE_DELIMITER);
        }
    }
}

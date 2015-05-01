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

package org.mxupdate.test.test.update.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Policy_mxJPO policy CI} state export / update.
 *
 * @author The MxUpdate Team
 */
public class PolicyCI_5StateAccessUserTest
    extends AbstractPolicyTest
{
    /** Name of test policy. */
    private static final String POLICY_NAME = AbstractTest.PREFIX + "Test";

    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return new Object[][]{};
    }

    /**
     * Test create owner access with empty owner filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: create owner access with empty owner filter definition")
    public void testPositiveUpdateStateOwnerAccessWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n  nothidden")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test create owner access with empty owner filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: update owner access with empty owner filter definition for existing filter expression")
    public void testPositiveUpdateStateOwnerAccessWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);
        this.mql("add policy " + policy.getName()
                + " state create owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type!=Part"),
               "check that owner access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n  nothidden")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test create owner access with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: create owner access with filter expression")
    public void testPositiveUpdateStateOwnerAccessWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);
        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type==Part"),
                "check that owner access with filter expression defined");
    }

    /**
     * Test update owner access with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: update owner access with filter expression for existing filter expression")
    public void testPositiveUpdateStateOwnerAccessWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);
        this.mql("add policy " + policy.getName()
                + " state create owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type!=Part"),
                "check that owner access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type==Part"),
                "check that owner access with filter expression defined");
    }

    /**
     * Test create owner access without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: create owner access without filter expression for non existing state")
    public void testPositiveUpdateStateOwnerAccessWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n  nothidden")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test update owner access without filter expression for existing state
     * with existing owner filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: update owner access without filter expression for existing state with existing owner filter expression")
    public void testPositiveUpdateStateOwnerAccessWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create owner read,show filter \"type==Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    owner read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n     filter type==Part"),
                "check that owner access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n  nothidden")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test create owner revoke with empty owner filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: create owner revoke with empty owner filter definition")
    public void testPositiveUpdateStateOwnerRevokeWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n  nothidden")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test create owner revoke with empty owner filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: update owner revoke with empty owner filter definition for existing filter expression")
    public void testPositiveUpdateStateOwnerRevokeWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type!=Part"),
               "check that owner revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n  nothidden")
                        // old tyle
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test create owner revoke with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: create owner revoke with filter expression")
    public void testPositiveUpdateStateOwnerRevokeWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type==Part"),
                "check that owner revoke with filter expression defined");
    }

    /**
     * Test update owner revoke with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: update owner revoke with filter expression for existing filter expression")
    public void testPositiveUpdateStateOwnerRevokeWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type!=Part"),
               "check that owner revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type==Part"),
                "check that owner revoke with filter expression defined");
    }

    /**
     * Test create owner revoke without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: create owner revoke without filter expression for non existing state")
    public void testPositiveUpdateStateOwnerRevokeWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n  nothidden")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test update owner revoke without filter expression for existing state
     * with existing owner filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: update owner revoke without filter expression for existing state with existing owner filter expression")
    public void testPositiveUpdateStateOwnerRevokeWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke owner read,show filter \"type==Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type==Part"),
                "check that revoke owner revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: create public access with empty public filter definition")
    public void testPositiveUpdateStatePublicAccessWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n    owner")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: update public access with empty public filter definition for existing filter expression")
    public void testPositiveUpdateStatePublicAccessWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create public read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type!=Part"),
                "check that public access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n    owner")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test create public access with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: create public access with filter expression")
    public void testPositiveUpdateStatePublicAccessWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type==Part"),
                "check that public access with filter expression defined");
    }

    /**
     * Test update public access with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: update public access with filter expression for existing filter expression")
    public void testPositiveUpdateStatePublicAccessWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create public read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type!=Part"),
                "check that public access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        final String polPrint = this.mql("print pol " + policy.getName() + " !history");
        Assert.assertTrue(
                polPrint.matches("(\\n|.)*public read,show(\\n| )*filter type==Part(\\n|.)*"),
                "check that public access with filter expression defined (have print pol: " + polPrint + ")");
    }

    /**
     * Test create public access without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: create public access without filter expression for non existing state")
    public void testPositiveUpdateStatePublicAccessWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n    owner")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test update public access without filter expression for existing state
     * with existing public filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: update public access without filter expression for existing state with existing public filter expression")
    public void testPositiveUpdateStatePublicAccessWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create public read,show filter \"type==Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type==Part"),
                "check that public access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n    owner")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: create public access with empty public filter definition")
    public void testPositiveUpdateStatePublicRevokeWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n    public")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke  without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: update public access with empty public filter definition for existing filter expression")
    public void testPositiveUpdateStatePublicRevokeWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke public read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type!=Part"),
                "check that public revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n    public")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke without filter expression defined");
    }

    /**
     * Test create public access with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: create public access with filter expression")
    public void testPositiveUpdateStatePublicRevokeWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type==Part"),
                "check that public revoke with filter expression defined");
    }

    /**
     * Test update public access with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: update public access with filter expression for existing filter expression")
    public void testPositiveUpdateStatePublicRevokeWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke public read,show filter \"type!=Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show filter type!=Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type!=Part"),
                "check that public revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type==Part"),
                "check that public revoke with filter expression defined");
    }

    /**
     * Test create public access without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: create public access without filter expression for non existing state")
    public void testPositiveUpdateStatePublicRevokeWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n    public")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke without filter expression defined");
    }

    /**
     * Test update public access without filter expression for existing state
     * with existing public filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: update public access without filter expression for existing state with existing public filter expression")
    public void testPositiveUpdateStatePublicRevokeWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke public read,show filter \"type==Part\"");

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show filter type==Part")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type==Part"),
                "check that public revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode, (String) null);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n    public")
                        // old style
                        || this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke without filter expression defined");
    }

    /**
     * Test export with owner access for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: export with owner access for a state with filter expression")
    public void testPositiveExportStateOwnerAccessWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create owner read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    owner {read show} filter \"type==Part\"\n") >= 0,
                "check the for owner filter is exported");
    }

    /**
     * Test export with owner access for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: export with owner access for a state without filter expression")
    public void testPositiveExportStateOwnerAccessWithoutFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create owner read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    owner {read show}\n") >= 0,
                "check the for owner filter is exported");
    }

    /**
     * Test export with owner revoke for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: export with owner revoke for a state with filter expression")
    public void testPositiveExportStateOwnerRevokeWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create revoke owner read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    revoke owner {read show} filter \"type==Part\"\n") >= 0,
                "check the for owner filter is exported");
    }

    /**
     * Test export with none owner revoke.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: export with none owner revoke")
    public void testPositiveExportStateOwnerRevokeWithoutDefinition()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create revoke owner none filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("revoke owner") < 0,
                "check the for owner revoke is not exported");
    }

    /**
     * Test export with owner revoke for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: export with owner revoke for a state without filter expression")
    public void testPositiveExportStateOwnerRevokeWithoutFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create revoke owner read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();

        Assert.assertTrue(
                code.indexOf("\n    revoke owner {read show}\n") >= 0,
                "check the for owner revoke filter is not exported");
    }

    /**
     * Test export with public access for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: export with public access for a state with filter expression")
    public void testPositiveExportStatePublicAccessWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create public read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    public {read show} filter \"type==Part\"\n") >= 0,
                "check the for public access filter is exported");
    }

    /**
     * Test export with public access for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "issue #87: export with public access for a state without filter expression")
    public void testPositiveExportStatePublicAccessWithoutFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create public read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    public {read show}\n") >= 0,
                "check the for public access filter is not exported");
    }

    /**
     * Test export with public revoke for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: export with public revoke for a state with filter expression")
    public void testPositiveExportStatePublicRevokeWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create revoke public read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    revoke public {read show} filter \"type==Part\"\n") >= 0,
                "check the for public revoke filter is exported");
    }

    /**
     * Test export with none public revoke.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "export with none public revoke")
    public void testPositiveExportStatePublicRevokeWithoutDefinition()
        throws Exception
    {
        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create revoke public none filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();

        Assert.assertTrue(
                code.indexOf("\n    revoke public") < 0,
                "check the for public revoke is not exported");
    }

    /**
     * Test export with public revoke for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "issue #88: export with public revoke for a state without filter expression")
    public void testPositiveExportStatePublicRevokeWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyCI_5StateAccessUserTest.POLICY_NAME);
        policy.addState(
                new PolicyData.State()
                        .setName("create"));

        this.mql("add policy " + PolicyCI_5StateAccessUserTest.POLICY_NAME
                + " state create revoke public read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyCI_5StateAccessUserTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyCI_5StateAccessUserTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    revoke public {read show}\n") >= 0,
                "check the for public revoke filter is exported");
    }

}

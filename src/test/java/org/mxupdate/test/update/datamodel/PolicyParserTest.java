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

package org.mxupdate.test.update.datamodel;

import java.lang.reflect.Method;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the policy parser.
 *
 * @author The MxUpdate Team
 */
public class PolicyParserTest
    extends AbstractTest
{
    /** Name of the test dimension. */
    private static final String POLICY_NAME = AbstractTest.PREFIX + "_Test";

    /** Start of the command to update the dimension to extract the code. */
    private static final String START_INDEX = "mxUpdate policy \"${NAME}\" {";
    /** Length of the string of the command to update the dimension. */
    private static final int START_INDEX_LENGTH = PolicyParserTest.START_INDEX.length();

    /**
     * Returns data providers used for testing parses.
     *
     * @return test source code to parse
     */
    @DataProvider(name = "data")
    public Object[][] getCodes()
    {
        return new Object[][]{
                new Object[]{
                        "1) simple hidden policy",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\""},
                new Object[]{
                        "2a) simple hidden policy with description special characters",
                        "",
                        "description \"{}\\\"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\""},
                new Object[]{
                        "2b) simple hidden policy with type special characters",
                        "",
                        "description \"\"  type {\"{}\\\"\"} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\""},
                new Object[]{
                        "2c) simple hidden policy with format special characters",
                        "",
                        "description \"\"  type {} format {\"{}\\\"\"} defaultformat \"\" sequence \"\" store \"\" hidden \"true\""},
                new Object[]{
                        "2d) simple hidden policy with defaultformat special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"{}\\\"\" sequence \"\" store \"\" hidden \"true\""},
                new Object[]{
                        "2e) simple hidden policy with sequence special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"{}\\\"\" store \"\" hidden \"true\""},
                new Object[]{
                        "2f) simple hidden policy with store special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"{}\\\"\" hidden \"true\""},
                new Object[]{
                        "2g) simple hidden policy with delimeter / minor special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" delimiter . minorsequence \"{}\\\"\" majorsequence \"{}\\\"\" store \"\" hidden \"true\""},
                // policy property
                new Object[]{
                        "3a) policy with property special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" property \"{}\\\"\""},
                new Object[]{
                        "3b) policy with property and value special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" property \"{}\\\"\" value \"{}\\\"\""},
                new Object[]{
                        "3c) policy with property link special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" property \"{}\\\"\" to type \"{}\\\"\""},
                new Object[]{
                        "3d) policy with property link and value special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
                // policy state
                new Object[]{
                        "4a) policy with state symbolic name special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"{}\\\"\" { registeredName \"{}\\\"\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "4b) policy with state action special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"{}\\\"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "4c) policy with state action input special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"A\" input \"{}\\\"\" check \"\" input \"\" }"},
                new Object[]{
                        "4d) policy with state check special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"\" input \"\" check \"{}\\\"\" input \"\" }"},
                new Object[]{
                        "4e) policy with state check input special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"\" input \"\" check \"A\" input \"{}\\\"\" }"},
                // policy state property
                new Object[]{
                        "5a) policy with state property special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" }"},
                new Object[]{
                        "5b) policy with state property and value special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" value \"{}\\\"\" }"},
                new Object[]{
                        "5c) policy with state property link special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" to type \"{}\\\"\" }"},
                new Object[]{
                        "5d) policy with state property link and value special characters",
                        "",
                        "description \"\"  type {} format {} defaultformat \"\" sequence \"\" store \"\" hidden \"true\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision \"false\" minorrevision \"false\" version \"false\" promote \"false\" checkouthistory \"false\" published \"false\" action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},
        };
    }

    /**
     * Parsed the <code>_definition</code> code and compares the result with
     * <code>_toTest</code>.
     *
     * @param _description  description of the test
     * @param _toTest       expected result (if empty string
     *                      <code>_definition</code> is the expected result)
     * @param _definition   text of the definition to test
     * @throws Exception if <code>_definition</code> could not parsed
     */
    @Test(dataProvider = "data")
    public void positiveTestPolicy(final String _description,
                                   final String _toTest,
                                   final String _definition)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final Policy_mxJPO policy = new Policy_mxJPO(paramCache.getMapping().getTypeDef("Policy"), PolicyParserTest.POLICY_NAME);
        policy.parseUpdate(_definition);

        final StringBuilder generated = new StringBuilder();
        final Method write = policy.getClass()
                .getDeclaredMethod("write", ParameterCache_mxJPO.class, Appendable.class);
        write.setAccessible(true);
        write.invoke(policy, paramCache, generated);

        final StringBuilder oldDefBuilder = new StringBuilder();
        for (final String line : _toTest.isEmpty() ? _definition.split("\n") : _toTest.split("\n"))  {
            oldDefBuilder.append(line.trim()).append(' ');
        }
        int length = 0;
        String oldDef = oldDefBuilder.toString();
        while (length != oldDef.length())  {
            length = oldDef.length();
            oldDef = oldDef.replaceAll("  ", " ");
        }

        final String temp = generated.substring(generated.indexOf(PolicyParserTest.START_INDEX) + PolicyParserTest.START_INDEX_LENGTH + 1, generated.length() - 2).toString();
        final StringBuilder newDef = new StringBuilder();
        for (final String line : temp.split("\n"))  {
            newDef.append(line.trim()).append(' ');
        }

        Assert.assertEquals(newDef.toString().trim(), oldDef.trim());
    }
}

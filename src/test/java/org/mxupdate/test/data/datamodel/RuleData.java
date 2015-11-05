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

package org.mxupdate.test.data.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.ExportParser.Line;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.testng.Assert;

import matrix.util.MatrixException;

/**
 * Used to define a rule, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class RuleData
    extends AbstractAdminData<RuleData>
{
    /** Access definitions for this state. */
    private final List<Access> accessList = new ArrayList<>();

    /**
     * Initialize this rule with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this attribute is
     *                  defined)
     * @param _name     name of the rule
     */
    public RuleData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.DM_RULE, _name);
    }

    /**
     * Appends given {@code _accessList}.
     *
     * @param _accessList    access list to append
     * @return this rule data instance
     */
    public RuleData addAccess(final Access... _accessList)
    {
        this.accessList.addAll(Arrays.asList(_accessList));
        return this;
    }

    /**
     * Returns the TCL update file of this rule data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate rule \"${NAME}\" {\n");

        this.getFlags() .append4Update("    ", strg);
        this.getValues().append4Update("    ", strg);
        this.getDatas() .append4Update("    ", strg);

        for (final Access access : this.accessList)  {
            strg.append("   ");
            access.append4CIFile(strg);
        }

        this.getProperties().append4Update("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        strg.append("}");

        return strg.toString();
    }

    /**
     * Create the related rule in MX for this rule data instance.
     *
     * @return this rule data instance
     * @throws MatrixException if create failed
     */
    @Override()
    public RuleData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add rule \"").append(AbstractTest.convertMql(this.getName()))
               .append("\" owner none public none ");

            for (final Access access : this.accessList)  {
                cmd.append(' ').append(access.getMQLCreateString());
            }

            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * The defined users in the {@link #accessList access lists} are created.
     */
    @Override()
    public RuleData createDependings()
        throws MatrixException
    {
        for (final Access access : this.accessList)  {
            access.createDependings();
        }

        super.createDependings();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        // access filter
        final SortedSet<String> curAccess = new TreeSet<>();
        for (final Line subLine : _exportParser.getRootLines().get(0).getChildren())  {
            if (subLine.getTag().equals("public")
                    || subLine.getTag().equals("owner")
                    || subLine.getTag().equals("user")
                    || subLine.getTag().equals("login")
                    || subLine.getTag().equals("revoke"))  {

                curAccess.add("    " + subLine.getTag() + ' ' + subLine.getValue());
            }
        }
        final SortedSet<String> expAccess = new TreeSet<>();
        for (final Access access : this.accessList)  {
            final StringBuilder tmp = new StringBuilder();
            access.append4CIFile(tmp);
            expAccess.add("    " + tmp.toString().trim());
        }
        Assert.assertEquals(curAccess, expAccess, "check access definition");
    }
}

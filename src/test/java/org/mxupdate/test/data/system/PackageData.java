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

package org.mxupdate.test.data.system;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.util.DataList;

import matrix.util.MatrixException;

/**
 * Defines the data for package configuration items.
 *
 * @author The MxUpdate Team
 */
public class PackageData
    extends AbstractAdminData<PackageData>
{
    /** All used packages. */
    private final DataList<PackageData> usePackages = new DataList<>("uses", "uses", true);
    /** All members. */
    private final DataList<AbstractAdminData<?>> members = new DataList<>("member ", "member ", true);

    /**
     * Initialize this package with given {@code _name}.
     *
     * @param _test     related test implementation (where this package is
     *                  defined)
     * @param _name     name of the package
     */
    public PackageData(final AbstractTest _test,
                       final String _name)
    {
        super(_test, AbstractTest.CI.SYS_PACKAGE, _name);
    }

    /**
     * Assigns the {@code _package} to this data instance.
     *
     * @param _package      package to use
     * @return this data instance
     */
    public PackageData usePackage(final PackageData _package)
    {
        this.usePackages.add(_package);
        return this;
    }

    /**
     * Assigns the {@code _member} to this data instance.
     *
     * @param _member       member to append
     * @return this data instance
     */
    public PackageData addMember(final AbstractAdminData<?> _member)
    {
        this.members.add(_member);
        return this;
    }

    @Override
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate package \"${NAME}\" {\n");

        this.getFlags()     .append4Update("    ", strg);
        this.getValues()    .append4Update("    ", strg);
        this.usePackages    .append4Update("    ", strg);
        this.members        .append4Update("    ", strg);
        this.getProperties().append4Update("    ", strg);

        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        strg.append("}");

        return strg.toString();
    }

    @Override
    public PackageData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmdCreate = new StringBuilder();
            cmdCreate.append("escape add package \"").append(AbstractTest.convertMql(this.getName())).append("\" ");
            this.usePackages.append4Create(cmdCreate);
            this.append4Create(cmdCreate);
            this.getTest().mql(cmdCreate);

            final StringBuilder modCreate = new StringBuilder();
            modCreate.append("escape mod package \"").append(AbstractTest.convertMql(this.getName())).append('\"');
            this.members.append4CreateViaAdd(modCreate);
            this.getTest().mql(modCreate);
        }
        return this;
    }

    @Override
    public PackageData createDependings()
        throws MatrixException
    {
        super.createDependings();
        this.usePackages.createDependings();
        this.members    .createDependings();
        return this;
    }

    @Override
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        this.usePackages    .check4Export(_exportParser, "");
        this.members        .check4Export(_exportParser, "");
    }
}

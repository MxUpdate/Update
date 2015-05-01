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

import java.util.Arrays;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.mxupdate.test.data.util.DataList;
import org.testng.Assert;

/**
 * Used to define a type, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class TypeData
    extends AbstractDataWithTrigger<TypeData>
{
    /** All methods of this type. */
    private final DataList<AbstractProgramData<?>> methods = new DataList<AbstractProgramData<?>>("method ", "method ", false);
    /** All attributes of this data with attribute instances. */
    private final DataList<AbstractAttributeData<?>> attributes = new DataList<AbstractAttributeData<?>>();

    /**
     * Initialize this type data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this type is
     *                  defined)
     * @param _name     name of the type
     */
    public TypeData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.DM_TYPE, _name);
    }

    /**
     * Assigns the {@code _method} to this type.
     *
     * @param _method   method to assign
     * @return this type data instance
     */
    public TypeData addMethod(final AbstractProgramData<?> _method)
    {
        this.methods.add(_method);
        return this;
    }

    /**
     * Assigns the {@code attributes} to this data instance.
     *
     * @param _attributes       attribute to assign
     * @return this type data instance
     */
    public TypeData addAttribute(final AbstractAttributeData<?>... _attributes)
    {
        this.attributes.addAll(Arrays.asList(_attributes));
        return this;
    }

    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate type \"${NAME}\" {\n");

        this.getFlags()     .appendUpdate("    ", strg);
        this.getValues()    .appendUpdate("    ", strg);
        this.getSingles()   .appendUpdate("    ", strg);
        this.getTriggers()  .appendUpdate("    ", strg);
        this.methods        .appendUpdate("    ", strg);
        this.attributes     .appendUpdate("    ", strg);
        this.getProperties().appendUpdate("    ", strg);

        strg.append("}");

        return strg.toString();
    }

    @Override()
    public TypeData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add type \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            this.methods   .append4Create(cmd);
            this.attributes.append4Create(cmd);

            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }

        return this;
    }

    @Override()
    public TypeData createDependings()
        throws MatrixException
    {
        super.createDependings();

        this.methods   .createDependings();
        this.attributes.createDependings();

        return this;
    }

    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        // check symbolic name
        Assert.assertEquals(
                _exportParser.getSymbolicName(),
                this.getSymbolicName(),
                "check symbolic name");

        this.getFlags()     .checkExport(_exportParser, "");
        this.getValues()    .checkExport(_exportParser, "");
        this.getSingles()   .checkExport(_exportParser, "");
        this.getTriggers()  .checkExport(_exportParser, "");
        this.methods        .checkExport(_exportParser, "");
        this.attributes     .checkExport(_exportParser, "");
        this.getProperties().checkExport(_exportParser.getLines("/" + this.getCI().getUrlTag() + "/property/@value"));
    }
}

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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.helper.LocaleAttributeList;
import org.mxupdate.test.data.util.DataList;

import matrix.util.MatrixException;

/**
 * Used to define an interface, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class InterfaceData
    extends AbstractAdminData<InterfaceData>
{
    /** All assigned relationships / types for this interface. */
    private final DataList<AbstractDataWithTrigger<?>> fors = new DataList<>("for ", "", true);
    /** Local attributes. */
    private final LocaleAttributeList attributes = new LocaleAttributeList();

    /**
     * Initialize this interface data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this interface is
     *                  defined)
     * @param _name     name of the interface
     */
    public InterfaceData(final AbstractTest _test,
                         final String _name)
    {
        super(_test, AbstractTest.CI.DM_INTERFACE, _name);
    }

    /**
     * Assigns <code>_type</code> to the {@link #types list of assigned types}
     * for this interface.
     *
     * @param _type     type to assign
     * @return this interface data instance
     */
    public InterfaceData addType(final TypeData _type)
    {
        this.fors.add(_type);
        return this;
    }

    /**
     * Defines that the interface is assigned to all types.
     *
     * @return this interface data instance
     * @see #allTypes
     */
    public InterfaceData addAllTypes()
    {
        this.fors.addAll("type");
        return this;
    }

    /**
     * Assigns <code>_relationship</code> to the
     * {@link #relationships list of assigned relationships} for this
     * interface.
     *
     * @param _relationship     relationship to assign
     * @return this interface data instance
     * @see #relationships
     */
    public InterfaceData addRelationship(final RelationshipData _relationship)
    {
        this.fors.add(_relationship);
        return this;
    }

    /**
     * Defines that the interface is assigned to all relationships.
     *
     * @return this interface data instance
     * @see #allRelationships
     */
    public InterfaceData addAllRelationships()
    {
        this.fors.addAll("relationship");
        return this;
    }

    /**
     * Appends given {@code _attributes}.
     *
     * @param _attributes    attributes list to append
     * @return this interface data instance
     */
    public InterfaceData addLocalAttribute(final AttributeData... _attributes)
    {
        this.attributes.addAll(Arrays.asList(_attributes));
        return this;
    }

    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate interface \"${NAME}\" {\n");

        this.getFlags()     .append4Update("    ", strg);
        this.getValues()    .append4Update("    ", strg);
        this.getSingles()   .append4Update("    ", strg);
        this.getKeyValues() .append4Update("    ", strg);
        this.getDatas()     .append4Update("    ", strg);
        this.attributes     .append4Update("    ", strg);
        this.getProperties().append4Update("    ", strg);
        this.fors           .append4Update("    ", strg);

        strg.append("}");

        return strg.toString();
    }

    /**
     * Create the related interface in MX for this type data instance and
     * the {@link #types}.
     *
     * @return this interface data instance
     * @throws MatrixException if create failed
     * @see #types
     */
    @Override()
    public InterfaceData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add interface \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            this.fors      .append4Create(cmd);

            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * Creates depending {@link #parents parent interfaces}, {@link #types} and
     * {@link #relationships}.
     *
     * @see #parents
     * @see #types
     * @see #relationships
     */
    @Override()
    public InterfaceData createDependings()
        throws MatrixException
    {
        super.createDependings();

        this.fors      .createDependings();
        this.attributes.createDependings();

        return this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        this.fors        .check4Export(_exportParser, "");

        this.attributes.checkExport(_exportParser);
    }
}

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to define all administration objects which could have
 * attributes used to create / update and to export.
 *
 * @param <DATAWITHATTRIBUTE>     derived data class with attributes
 * @author The MxUpdate Team
 */
public abstract class AbstractDataWithAttribute<DATAWITHATTRIBUTE extends AbstractDataWithAttribute<?>>
    extends AbstractAdminData<DATAWITHATTRIBUTE>
{
    /**
     * All attributes of this data with attributes instance.
     */
    private final Set<AbstractAttributeData<?>> attributes = new HashSet<AbstractAttributeData<?>>();

    /**
     * All attributes to ignore of this data with attributes instance.
     */
    private final Set<AbstractAttributeData<?>> ignoreAttributes = new HashSet<AbstractAttributeData<?>>();

    /**
     * Flag to store information that the ignore attributes are appended.
     *
     * @see #appendIgnoredAttributes()
     */
    private boolean ignoreAttributesAppended;

    /**
     * All attributes to remove of this data with attributes instance.
     */
    private final Set<AbstractAttributeData<?>> removeAttributes = new HashSet<AbstractAttributeData<?>>();

    /**
     * Initialize the values for administration objects with attributes.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the administration object
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     * @param _requiredExportFlags  defines the required flags of the export
     *                              within the configuration item file
     */
    protected AbstractDataWithAttribute(final AbstractTest _test,
                                        final AbstractTest.CI _ci,
                                        final String _name,
                                        final Map<String,String> _requiredExportValues,
                                        final Map<String,Boolean> _requiredExportFlags)
    {
        super(_test, _ci, _name, _requiredExportValues, _requiredExportFlags);
    }

    /**
     * Assigns the <code>_attributes</code> to this data instance.
     *
     * @param _attributes       attribute to assign
     * @return this data instance
     * @see #attributes
     */
    @SuppressWarnings("unchecked")
    public DATAWITHATTRIBUTE addAttribute(final AbstractAttributeData<?>... _attributes)
    {
        this.attributes.addAll(Arrays.asList(_attributes));
        return (DATAWITHATTRIBUTE) this;
    }

    /**
     * Assigns the <code>_attributes</code> to ignore for this data instance.
     *
     * @param _attributes       attribute to ignore
     * @return this data instance
     * @see #ignoreAttributes
     */
    @SuppressWarnings("unchecked")
    public DATAWITHATTRIBUTE addIgnoreAttribute(final AbstractAttributeData<?>... _attributes)
    {
        this.ignoreAttributes.addAll(Arrays.asList(_attributes));
        return (DATAWITHATTRIBUTE) this;
    }

    /**
     * Assigns the <code>_attributes</code> to remove for this data instance.
     *
     * @param _attributes       attribute to remove
     * @return this data instance
     * @see #removeAttributes
     */
    @SuppressWarnings("unchecked")
    public DATAWITHATTRIBUTE addRemoveAttribute(final AbstractAttributeData<?>... _attributes)
    {
        this.removeAttributes.addAll(Arrays.asList(_attributes));
        return (DATAWITHATTRIBUTE) this;
    }

    /**
     * Returns all assigned {@link #attributes}.
     *
     * @return all assigned attributes
     * @see #attributes
     */
    public Set<AbstractAttributeData<?>> getAttributes()
    {
        return this.attributes;
    }

    /**
     * {@inheritDoc}
     * Creates all assigned {@link #attributes}.
     *
     * @see #attributes
     */
    @Override()
    @SuppressWarnings("unchecked")
    public DATAWITHATTRIBUTE createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create attributes
        for (final AbstractAttributeData<?> attr : this.attributes)  {
            attr.create();
        }

        return (DATAWITHATTRIBUTE) this;
    }

    /**
     * Appends the call to define the attributes for the configuration item.
     *
     * @param _cmd  string builder to append the attributes for the CI file
     */
    protected void append4CIAttributes(final StringBuilder _cmd)
    {
        _cmd.append("\n\ntestAttributes -").append(this.getCI().getMxType()).append(" \"${NAME}\"");
        // ignore attributes
        for (final AbstractAttributeData<?> attr : this.ignoreAttributes)  {
            _cmd.append(" -ignoreattr \"").append(AbstractTest.convertTcl(attr.getName())).append('\"');
        }
        // remove attributes
        for (final AbstractAttributeData<?> attr : this.removeAttributes)  {
            _cmd.append(" -removeattr \"").append(AbstractTest.convertTcl(attr.getName())).append('\"');
        }
        // assigned attributes
        _cmd.append(" -attributes [list \\\n");
        for (final AbstractAttributeData<?> attr : this.attributes)  {
            _cmd.append("    \"").append(AbstractTest.convertTcl(attr.getName())).append("\" \\\n");
        }
        _cmd.append("]\n");
    }

    /**
     * Appends the MQL commands to define all {@link #attributes} within a
     * create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if programs could not be created or thrown from
     *                         called method in super class
     * @see #attributes
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // ignore attributes
        for (final AbstractAttributeData<?> attribute : this.ignoreAttributes)  {
            attribute.create();
        }
        // remove attributes
        for (final AbstractAttributeData<?> attribute : this.removeAttributes)  {
            attribute.create();
        }
        // assigned attributes
        for (final AbstractAttributeData<?> attribute : this.attributes)  {
            attribute.create();
            _cmd.append(" attribute \"").append(AbstractTest.convertMql(attribute.getName())).append("\"");
        }
    }

    /**
     * Appends on the given data object in MX the related
     * {@link #ignoreAttributes ignored attributes}.
     *
     * @return this data instance
     * @throws MatrixException if append failed
     */
    @SuppressWarnings("unchecked")
    public DATAWITHATTRIBUTE appendIgnoredAttributes()
        throws MatrixException
    {
        for (final AbstractAttributeData<?> attribute : this.ignoreAttributes)  {
            attribute.create();
            this.getTest().mql(new StringBuilder()
                    .append("escape mod ").append(this.getCI().getMxType()).append(" \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                    .append("\" add attribute \"").append(AbstractTest.convertMql(attribute.getName())).append('\"'));
        }
        this.ignoreAttributesAppended = true;
        return (DATAWITHATTRIBUTE) this;
    }

    /**
     * Appends on the given data object in MX the related
     * {@link #removeAttributes remove attributes}.
     *
     * @return this data instance
     * @throws MatrixException if append failed
     */
    @SuppressWarnings("unchecked")
    public DATAWITHATTRIBUTE appendRemoveAttributes()
        throws MatrixException
    {
        for (final AbstractAttributeData<?> attribute : this.removeAttributes)  {
            attribute.create();
            this.getTest().mql(new StringBuilder()
                    .append("escape mod ").append(this.getCI().getMxType()).append(" \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                    .append("\" add attribute \"").append(AbstractTest.convertMql(attribute.getName())).append('\"'));
        }
        return (DATAWITHATTRIBUTE) this;
    }
}

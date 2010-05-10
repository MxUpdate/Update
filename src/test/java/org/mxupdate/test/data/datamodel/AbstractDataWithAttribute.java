/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.test.data.datamodel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * The class is used to define all administration objects which could have
 * attributes used to create / update and to export.
 *
 * @param <DATAWITHATTRIBUTE>     derived data class with attributes
 * @author The MxUpdate Team
 * @version $Id$
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
     * @see #checkExport(ExportParser)
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
     */
    protected AbstractDataWithAttribute(final AbstractTest _test,
                                        final AbstractTest.CI _ci,
                                        final String _name,
                                        final Set<String> _requiredExportValues)
    {
        super(_test, _ci, _name, _requiredExportValues);
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

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check attributes
        final Set<String> attrs = new HashSet<String>(_exportParser.getLines("/testAttributes/"));
        for (final AbstractAttributeData<?> attribute : this.attributes)  {
            final String attrName = "\"" + AbstractTest.convertTcl(attribute.getName()) + "\" \\";
            Assert.assertTrue(attrs.contains(attrName),
                              "check that attribute '" + attribute.getName() + "' is defined");
        }
        Assert.assertEquals(
                attrs.size(),
                this.attributes.size() + ((this.ignoreAttributesAppended) ? this.ignoreAttributes.size() : 0),
                "check all attributes are defined");
    }
}

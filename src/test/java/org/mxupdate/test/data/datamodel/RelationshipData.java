/*
 * Copyright 2008-2009 The MxUpdate Team
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

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * Used to define a relationship, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class RelationshipData
    extends AbstractDataWithTrigger<RelationshipData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(1);
    static  {
        RelationshipData.REQUIRED_EXPORT_VALUES.add("description");
    }

    /**
     * All attributes of this relationship.
     */
    private final Set<AbstractAttribute<?>> attributes = new HashSet<AbstractAttribute<?>>();

    /**
     * Initialize this relationship data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this relationship is
     *                  defined)
     * @param _name     name of the relationship
     */
    public RelationshipData(final AbstractTest _test,
                            final String _name)
    {
        super(_test, AbstractTest.CI.RELATIONSHIP, _name,
              "RELATIONSHIP_", "datamodel/relationship",
              RelationshipData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Assigns the <code>_attribute</code> to this relationship.
     *
     * @param _attribute        attribute to assign
     * @return this relationship data instance
     * @see #attributes
     */
    public RelationshipData addAttribute(final AbstractAttribute<?> _attribute)
    {
        this.attributes.add(_attribute);
        return this;
    }

    /**
     * Returns the TCL update file of this relationship data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod relationship \"${NAME}\"");
        this.append4CIFileValues(cmd);

        // append attributes
        cmd.append("\n\ntestAttributes -relationship \"${NAME}\" -attributes [list \\\n");
        for (final AbstractAttribute<?> attribute : this.attributes)  {
            cmd.append("    \"").append(AbstractTest.convertTcl(attribute.getName())).append("\" \\\n");
        }
        cmd.append("]\n");

        return cmd.toString();
    }

    /**
     * Create the related relationship in MX for this relationship data
     * instance and appends the {@link #attributes}.
     *
     * @return this relationship data instance
     * @throws MatrixException if create failed
     * @see #methods
     * @see #attributes
     */
    @Override()
    public RelationshipData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add relationship \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            // append attributes
            for (final AbstractAttribute<?> attribute : this.attributes)  {
                attribute.create();
                cmd.append(" attribute \"").append(AbstractTest.convertMql(attribute.getName())).append("\"");
            }

            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }

        return this;
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
        for (final AbstractAttribute<?> attribute : this.attributes)  {
            final String attrName = "\"" + AbstractTest.convertTcl(attribute.getName()) + "\" \\";
            Assert.assertTrue(attrs.contains(attrName),
                              "check that attribute '" + attribute.getName() + "' is defined");
        }
        Assert.assertEquals(attrs.size(),
                            this.attributes.size(),
                            "check all attributes are defined");
    }
}

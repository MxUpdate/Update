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

package org.mxupdate.test.data.user;

import java.util.ArrayList;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * The class is used to define all person (which have related person business
 * object) objects used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PersonData
    extends AbstractPersonAdminData<PersonData>
{
    /**
     * Name of the state of the person business object.
     *
     * @see #setState(String)
     */
    private String state;

    /**
     * Constructor to initialize this person.
     *
     * @param _test     related test implementation (where administration
     *                  person is defined)
     * @param _name     name of the person
     */
    public PersonData(final AbstractTest _test,
                      final String _name)
    {
        super(_test, AbstractTest.CI.USR_PERSON, _name);
    }

    /**
     * Defines the new {@link #state} for this person.
     *
     * @param _state        new state
     * @return this person instance
     * @see #state
     */
    public PersonData setState(final String _state)
    {
        this.state = _state;
        return this;
    }

    /**
     * {@inheritDoc}
     * Overwrites the original method to set the correct {@link #state}
     * information.
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder ciFile = new StringBuilder().append(super.ciFile())
                .append("\nsetState \"").append((this.state != null) ? this.state : "Inactive").append("\"\n");
        return ciFile.toString();
    }

    /**
     * Creates the person business object (after the administration person was
     * created).
     *
     * @return this administration person instance
     * @throws MatrixException if create failed
     * @see #products
     */
    @Override()
    public PersonData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            super.create();
            final StringBuilder cmd = new StringBuilder()
                    .append("escape add bus \"").append(AbstractTest.convertMql(this.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(this.getName()))
                    .append("\" \"-")
                    .append("\" description \"").append(AbstractTest.convertMql((this.getValue("description") != null) ? this.getValue("description") : ""))
                    .append("\" policy \"").append(AbstractTest.convertMql(this.getCI().getBusPolicy()))
                    .append("\" vault \"").append(AbstractTest.convertMql(this.getCI().getBusVault()))
                    .append('\"');

            // append state (if defined
            if (this.state != null)  {
                cmd.append(" current \"").append(AbstractTest.convertMql(this.state)).append("\"");
            }

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // differ export for admin / bus update
        final List<ExportParser.Line> adminLines = new ArrayList<ExportParser.Line>();
        final List<ExportParser.Line> busLines = new ArrayList<ExportParser.Line>();
        for (final ExportParser.Line rootLine : _exportParser.getRootLines())  {
            if (rootLine.getValue().startsWith("mod person ")
                    || rootLine.getValue().startsWith("escape mod person ")
                    || rootLine.getValue().startsWith("escape add cue ")
                    || rootLine.getValue().startsWith("escape add filter ")
                    || rootLine.getValue().startsWith("escape add query ")
                    || rootLine.getValue().startsWith("escape add table ")
                    || rootLine.getValue().startsWith("escape add tip ")
                    || rootLine.getValue().startsWith("escape add toolset ")
                    || rootLine.getValue().startsWith("escape add view ")
                    || rootLine.getValue().startsWith("escape add property ")
                    || rootLine.getTag().equals("setProducts"))  {
                adminLines.add(rootLine);
            } else  {
                busLines.add(rootLine);
            }
        }

        // check admin export
        final ExportParser adminExportParser = new ExportParser(
                this.getName(),
                _exportParser.getSymbolicName(),
                _exportParser.getLog(),
                adminLines.toArray(new ExportParser.Line[adminLines.size()]));
        super.checkExport(adminExportParser);

        // check bus export
        final ExportParser busExportParser = new ExportParser(
                this.getName(),
                _exportParser.getSymbolicName(),
                _exportParser.getLog(),
                busLines.toArray(new ExportParser.Line[busLines.size()]));
        final List<String> lineStates = busExportParser.getLines("/setState/@value");
        Assert.assertEquals(
                lineStates.size(),
                1,
                "check that one state line is defined");
        Assert.assertEquals(
                lineStates.get(0),
                (this.state == null) ? "\"Inactive\"" : "\"" + this.state + "\"",
                "check correct state");
    }
}

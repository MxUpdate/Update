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

package org.mxupdate.test.data.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.organization.AbstractOrganizationalData;
import org.testng.Assert;

/**
 * The class is used to define all person (which have related person business
 * object) objects used to create / update and to export.
 *
 * @author The MxUpdate Team
 */
public class PersonData
    extends AbstractPersonAdminData<PersonData>
{
    /**
     * Name of the state of the person business object.
     *
     * @see #setState(String)
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private String state;

    /**
     * The person is employee of given organization.
     *
     * @see #addEmployeeOf(AbstractOrganizationalData...)
     * @see #ciFile()
     */
    private final Set<AbstractOrganizationalData<?>> employeeOf = new HashSet<AbstractOrganizationalData<?>>();

    /**
     * The person is representative of given organization.
     *
     * @see #addRepresentativeOf(AbstractOrganizationalData...)
     * @see #ciFile()
     */
    private final Set<AbstractOrganizationalData<?>> representativeOf = new HashSet<AbstractOrganizationalData<?>>();

    /**
     * The person is member of given organization.
     *
     * @see #addMemberOf(AbstractOrganizationalData,String,String)
     * @see #ciFile()
     */
    private final Set<AbstractOrganizationalData<?>> memberOf = new HashSet<AbstractOrganizationalData<?>>();

    /**
     * Defined access for given organization.
     *
     * @see #addMemberOf(AbstractOrganizationalData,String,String)
     * @see #ciFile()
     * @see #checkExport(ExportParser)
     */
    private final Map<AbstractOrganizationalData<?>,String> memberOfAccess = new HashMap<AbstractOrganizationalData<?>,String>();

    /**
     * Defined roles for given organization.
     *
     * @see #addMemberOf(AbstractOrganizationalData,String,String)
     * @see #ciFile()
     */
    private final Map<AbstractOrganizationalData<?>,String> memberOfRoles = new HashMap<AbstractOrganizationalData<?>,String>();

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
     * Defines that this person is employee of given organizational units
     * <code>_org</code>.
     *
     * @param _orgs     organizational units
     * @return this person instance
     * @see #employeeOf
     */
    public PersonData addEmployeeOf(final AbstractOrganizationalData<?>... _orgs)
    {
        this.employeeOf.addAll(Arrays.asList(_orgs));
        return this;
    }

    /**
     * Remove from this person employee of given organizational units
     * <code>_org</code>.
     *
     * @param _orgs     organizational units
     * @return this person instance
     * @see #employeeOf
     */
    public PersonData removeEmployeeOf(final AbstractOrganizationalData<?>... _orgs)
    {
        this.employeeOf.removeAll(Arrays.asList(_orgs));
        return this;
    }

    /**
     * Defines that this person is representative of given organizational units
     * <code>_org</code>.
     *
     * @param _orgs     organizational units
     * @return this person instance
     * @see #representativeOf
     */
    public PersonData addRepresentativeOf(final AbstractOrganizationalData<?>... _orgs)
    {
        this.representativeOf.addAll(Arrays.asList(_orgs));
        return this;
    }

    /**
     * Remove from this person representative of given organizational units
     * <code>_org</code>.
     *
     * @param _orgs     organizational units
     * @return this person instance
     * @see #representativeOf
     */
    public PersonData removeRepresentativeOf(final AbstractOrganizationalData<?>... _orgs)
    {
        this.representativeOf.removeAll(Arrays.asList(_orgs));
        return this;
    }

    /**
     * Defines that this person is representative of given organizational units
     * <code>_org</code>.
     *
     * @param _org      organizational unit
     * @param _access   access string
     * @param _roles    list of roles
     * @return this person instance
     * @see #memberOf
     */
    public PersonData addMemberOf(final AbstractOrganizationalData<?> _org,
                                  final String _access,
                                  final String _roles)
    {
        this.memberOf.add(_org);
        this.memberOfAccess.put(_org, _access);
        this.memberOfRoles.put(_org, _roles);
        return this;
    }

    /**
     * Remove from this person member of given organizational units
     * <code>_org</code>.
     *
     * @param _orgs     organizational units
     * @return this person instance
     * @see #memberOf
     */
    public PersonData removeMemberOf(final AbstractOrganizationalData<?>... _orgs)
    {
        this.memberOf.removeAll(Arrays.asList(_orgs));
        return this;
    }

    /**
     * {@inheritDoc}
     * Overwrites the original method to set the correct {@link #state}
     * information.
     *
     * @see #state
     * @see #employeeOf
     * @see #representativeOf
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder ciFile = new StringBuilder().append(super.ciFile())
                .append("\nsetState \"").append((this.state != null) ? this.state : "Inactive").append("\"\n");
        // employee of
        ciFile.append("\nsetEmployeeOf");
        for (final AbstractOrganizationalData<?> org : this.employeeOf)  {
            ciFile.append(" \\\n    ").append(this.makeLine(org));
        }
        // representative of
        ciFile.append("\nsetRepresentativeOf");
        for (final AbstractOrganizationalData<?> org : this.representativeOf)  {
            ciFile.append(" \\\n    ").append(this.makeLine(org));
        }
        // member of
        ciFile.append("\nsetMemberOf");
        for (final AbstractOrganizationalData<?> org : this.memberOf)  {
            ciFile.append(" \\\n    ")
                  .append(this.makeLine(
                          org,
                          "Project Access", this.memberOfAccess.get(org),
                          "Project Role", this.memberOfRoles.get(org)));
        }
        return ciFile.toString();
    }

    /**
     * {@inheritDoc}
     * Creates depending {@link #employeeOf} organizational object.
     *
     * @see #employeeOf
     */
    @Override()
    public PersonData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create all employee of organizational objects
        for (final AbstractOrganizationalData<?> org : this.employeeOf)  {
            org.create();
        }
        // create all representative of organizational objects
        for (final AbstractOrganizationalData<?> org : this.representativeOf)  {
            org.create();
        }
        // create all member of organizational objects
        for (final AbstractOrganizationalData<?> org : this.memberOf)  {
            org.create();
        }

        return this;
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
                    .append("\" description \"").append(AbstractTest.convertMql((this.getValue("description") != null) ? this.getValue("description").toString() : ""))
                    .append("\" policy \"").append(AbstractTest.convertMql(this.getCI().getBusPolicy()))
                    .append("\" vault \"").append(AbstractTest.convertMql(this.getCI().getBusVault()))
                    .append('\"');

            // append state (if defined)
            if (this.state != null)  {
                cmd.append(" current \"").append(AbstractTest.convertMql(this.state)).append("\"");
            }

            cmd.append(';');

            // append employee of
            for (final AbstractOrganizationalData<?> org : this.employeeOf)  {
                cmd.append("\nescape connect bus \"")
                    .append(AbstractTest.convertMql(this.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(this.getName()))
                    .append("\" \"-")
                    .append("\" relationship Employee from \"")
                    .append(AbstractTest.convertMql(org.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(org.getBusName()))
                    .append("\" \"").append(AbstractTest.convertMql(org.getBusRevision()))
                    .append("\";");
            }
            // append representative of
            for (final AbstractOrganizationalData<?> org : this.representativeOf)  {
                cmd.append("\nescape connect bus \"")
                    .append(AbstractTest.convertMql(this.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(this.getName()))
                    .append("\" \"-")
                    .append("\" relationship \"Organization Representative\" from \"")
                    .append(AbstractTest.convertMql(org.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(org.getBusName()))
                    .append("\" \"").append(AbstractTest.convertMql(org.getBusRevision()))
                    .append("\";");
            }
            // append member of
            for (final AbstractOrganizationalData<?> org : this.memberOf)  {
                cmd.append("\nescape connect bus \"")
                    .append(AbstractTest.convertMql(this.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(this.getName()))
                    .append("\" \"-")
                    .append("\" relationship \"Member\" from \"")
                    .append(AbstractTest.convertMql(org.getCI().getBusType()))
                    .append("\" \"").append(AbstractTest.convertMql(org.getBusName()))
                    .append("\" \"").append(AbstractTest.convertMql(org.getBusRevision()))
                    .append("\" \"Project Access\" \"").append(AbstractTest.convertMql(this.memberOfAccess.get(org)))
                    .append("\" \"Project Role\" \"").append(AbstractTest.convertMql(this.memberOfRoles.get(org)))
                    .append("\";");
            }

            try  {
                this.getTest().mql("trigger off");
                this.getTest().mql(cmd);
            } finally  {
                this.getTest().mql("trigger on");
            }
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
        // state
        final List<String> lineStates = busExportParser.getLines("/setState/@value");
        Assert.assertEquals(
                lineStates.size(),
                1,
                "check that one state line is defined");
        Assert.assertEquals(
                lineStates.get(0),
                (this.state == null) ? "\"Inactive\"" : "\"" + this.state + "\"",
                "check correct state");
        // employee of
        this.checkExportConnection(busExportParser, "employee", "setEmployeeOf", this.employeeOf, null, null);
        // representative of
        this.checkExportConnection(busExportParser, "representative", "setRepresentativeOf", this.representativeOf, null, null);
        // member of
        final List<Map<AbstractOrganizationalData<?>,String>> attrValuesMap = new ArrayList<Map<AbstractOrganizationalData<?>,String>>();
        attrValuesMap.add(this.memberOfAccess);
        attrValuesMap.add(this.memberOfRoles);
        this.checkExportConnection(busExportParser, "member", "setMemberOf", this.memberOf, new String[]{"Project Access", "Project Role"}, attrValuesMap);
    }

    /**
     * Checks the export of a organization connection.
     *
     * @param _exportParser     export parser
     * @param _log              logging text
     * @param _tag              tag
     * @param _orgs             expected organization objects
     * @param _attrNames        attribute name
     * @param _attrValuesMaps   values
     */
    protected void checkExportConnection(final ExportParser _exportParser,
                                         final String _log,
                                         final String _tag,
                                         final Set<AbstractOrganizationalData<?>> _orgs,
                                         final String[] _attrNames,
                                         final List<Map<AbstractOrganizationalData<?>,String>> _attrValuesMaps)
    {
        Assert.assertEquals(
                _exportParser.getLines("/" + _tag + "/@value").size(),
                1,
                "check that one " + _log + " of is defined");
        final Set<String> expOrgs = new HashSet<String>(_exportParser.getLines("/" + _tag + "/"));
        Assert.assertEquals(
                expOrgs.size(),
                _orgs.size(),
                "check that all " + _log + " definitions exists");
        for (final AbstractOrganizationalData<?> org :_orgs)  {
            final List<String> attrValues = new ArrayList<String>();
            if (_attrValuesMaps != null)  {
                int idx = 0;
                for (final Map<AbstractOrganizationalData<?>,String> attrValuesMap : _attrValuesMaps)  {
                    attrValues.add(_attrNames[idx++]);
                    attrValues.add(attrValuesMap.get(org));
                }
            }

            final String line = this.makeLine(org, attrValues.toArray(new String[attrValues.size()]));
            Assert.assertTrue(
                    expOrgs.contains(line) || expOrgs.contains(line + " \\"),
                    "check that " + _log + " of for '" + line + "' is defined (have : " + expOrgs + ")");
        }
    }

    /**
     * Prepares one line used within the export to define a organization
     * object.
     *
     * @param _org          organization object
     * @param _attrValues   related attribute names and values on the
     *                      connection
     * @return string
     */
    protected String makeLine(final AbstractOrganizationalData<?> _org,
                              final String... _attrValues)
    {
        String ret = "{\"" + _org.getCI().getBusType()
                + "\" \"" + AbstractTest.convertTcl(_org.getBusName())
                + "\" \"" + AbstractTest.convertTcl(_org.getBusRevision())
                + "\"";
        for (final String attrValue : _attrValues)  {
            ret += " \"" + AbstractTest.convertTcl(attrValue) + "\"";
        }
        return ret + "}";
    }
}

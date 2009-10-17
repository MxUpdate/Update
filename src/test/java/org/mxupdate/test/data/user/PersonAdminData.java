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

package org.mxupdate.test.data.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * The class is used to define all administration person (which have no related
 * person business object) objects used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PersonAdminData
    extends AbstractUserData<PersonAdminData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
        PersonAdminData.REQUIRED_EXPORT_VALUES.add("description");
    }

    /**
     * Access of this person.
     *
     * @see #addAccess(String...)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private final Set<String> access = new TreeSet<String>();

    /**
     * Administration access of this person.
     *
     * @see #addAdminAccess(String...)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private final Set<String> adminAccess = new TreeSet<String>();

    /**
     * Type of this person.
     *
     * @see #addType(String...)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     */
    private final Set<String> types = new TreeSet<String>();

    /**
     * Assigned roles for this person.
     *
     * @see #addRole(RoleData...)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private final Set<RoleData> roles = new HashSet<RoleData>();

    /**
     * Assigned groups for this person.
     *
     * @see #addGroup(GroupData...)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private final Set<GroupData> groups = new HashSet<GroupData>();

    /**
     * Flag to indicate that the password never expires. If <code>null</code>
     * the password will expire (same value as <i>false</i>; see
     * {@link #checkExport(ExportParser)}).
     *
     * @see #setPasswordNeverExpires(Boolean)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private Boolean passwordNeverExpires;

    /**
     * Constructor to initialize this administration person.
     *
     * @param _test     related test implementation (where this administration
     *                  person is defined)
     * @param _name     name of the administration person
     */
    public PersonAdminData(final AbstractTest _test,
                           final String _name)
    {
        super(_test, AbstractTest.CI.PERSONADMIN, _name, PersonAdminData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Assigns given <code>_access</code> to this person.
     *
     * @param _access   access to assign
     * @return this person instance
     * @see #access
     */
    public PersonAdminData addAccess(final String... _access)
    {
        this.access.addAll(Arrays.asList(_access));
        return this;
    }

    /**
     * Assigns given <code>_adminAccess</code> to this person.
     *
     * @param _adminAccess  administration access to assign
     * @return this person instance
     * @see #adminAccess
     */
    public PersonAdminData addAdminAccess(final String... _adminAccess)
    {
        this.adminAccess.addAll(Arrays.asList(_adminAccess));
        return this;
    }

    /**
     * Assigns given <code>_type</code> to this person.
     *
     * @param _type     type to assign
     * @return this person instance
     * @see #types
     */
    public PersonAdminData addType(final String... _type)
    {
        this.types.addAll(Arrays.asList(_type));
        return this;
    }

    /**
     * Assigns given <code>_role</code> to this person.
     *
     * @param _role     roles to assign
     * @return this person instance
     * @see #roles
     */
    public PersonAdminData addRole(final RoleData... _role)
    {
        this.roles.addAll(Arrays.asList(_role));
        return this;
    }

    /**
     * Returns all assigned {@link #roles}.
     *
     * @return all roles
     * @see #roles
     */
    public Set<RoleData> getRoles()
    {
        return this.roles;
    }

    /**
     * Assigns given <code>_group</code> to this person.
     *
     * @param _group        groups to assign
     * @return this person instance
     * @see #groups
     */
    public PersonAdminData addGroup(final GroupData... _group)
    {
        this.groups.addAll(Arrays.asList(_group));
        return this;
    }

    /**
     * Returns all assigned {@link #groups}.
     *
     * @return all group
     * @see #groups
     */
    public Set<GroupData> getGroups()
    {
        return this.groups;
    }

    /**
     * Defines if for this administration person the password never expires.
     *
     * @param _passwordNeverExpires     <i>true</i> to define that the password
     *                                  never expires
     * @return this person administration data instance
     * @see #passwordNeverExpires
     */
    public PersonAdminData setPasswordNeverExpires(final Boolean _passwordNeverExpires)
    {
        this.passwordNeverExpires = _passwordNeverExpires;
        return this;
    }

    /**
     * Appends the person specific values to the TCL update code
     * <code>_cmd</code> of the configuration item file. This includes:
     * <ul>
     * <li>{@link #access}</li>
     * <li>{@link #adminAccess administration access}</li>
     * <li>{@link #types}</li>
     * <li>{@link #groups}<li>
     * <li>{@link #roles}</li>
     * <li>{@link #passwordNeverExpires password never expires flag}</li>
     * </ul>
     *
     * @param _cmd  string builder with the TCL commands of the configuration
     *              item file
     * @see #values
     */
    @Override
    protected void append4CIFileValues(final StringBuilder _cmd)
    {
        super.append4CIFileValues(_cmd);
        _cmd.append(" access ").append(StringUtil_mxJPO.joinTcl(',', false, this.access, "none"))
            .append(" admin ").append(StringUtil_mxJPO.joinTcl(',', false, this.adminAccess, "none"));
        if (!this.types.isEmpty())  {
            _cmd.append(" type ").append(StringUtil_mxJPO.joinTcl(',', false, this.types, null)).append(";\n");
        }
        // groups
        for (final GroupData group : this.groups)  {
            _cmd.append(" assign group \"").append(AbstractTest.convertTcl(group.getName())).append("\"");
        }
        // roles
        for (final RoleData role : this.roles)  {
            _cmd.append(" assign role \"").append(AbstractTest.convertTcl(role.getName())).append("\"");
        }
        // password never expires flag
        if (this.passwordNeverExpires != null)  {
            _cmd.append(' ');
            if (!this.passwordNeverExpires)  {
                _cmd.append('!');
            }
            _cmd.append("neverexpires");
        }
    }

    /**
     * Appends the MQL commands related to a person within a create. This
     * includes:
     * <ul>
     * <li>{@link #access}</li>
     * <li>{@link #adminAccess administration access}</li>
     * <li>{@link #types}</li>
     * <li>{@link #groups}<li>
     * <li>{@link #roles}</li>
     * <li>{@link #passwordNeverExpires password never expires flag}</li>
     * </ul>
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #values
     */
    @Override
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // assign access
        _cmd.append(" access ").append(StringUtil_mxJPO.joinMql(',', false, this.access, "none"));
        // assign administration access
        if (!this.adminAccess.isEmpty())  {
            _cmd.append(" admin ").append(StringUtil_mxJPO.joinMql(',', false, this.adminAccess, null));
        }
        // assign access
        if (!this.types.isEmpty())  {
            _cmd.append(" type ").append(StringUtil_mxJPO.joinMql(',', false, this.types, null));
        }
        // groups
        for (final GroupData group : this.groups)  {
            group.create();
            _cmd.append(" assign group \"").append(AbstractTest.convertMql(group.getName())).append("\"");
        }
        // roles
        for (final RoleData role : this.roles)  {
            role.create();
            _cmd.append(" assign role \"").append(AbstractTest.convertMql(role.getName())).append("\"");
        }
        // password never expires flag
        if (this.passwordNeverExpires != null)  {
            _cmd.append(' ');
            if (!this.passwordNeverExpires)  {
                _cmd.append('!');
            }
            _cmd.append("neverexpires");
        }
    }

    /**
     * Checks the export of this person if all values are correct defined. It
     * is checked that
     * <ul>
     * <li>all {@link #access} is correct defined</li>
     * <li>all {@link #adminAccess administration access} is correct defined
     *     </li>
     * <li>all {@link #groups} are correct defined</li>
     * <li>all {@link #roles} are correct defined</li>
     * <li>{@link #passwordNeverExpires password never expires flag} is defined
     *     if set to true</li>
     * </ul>
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check access
        this.checkSingleValue(_exportParser,
                              "person",
                              "access",
                              "\"" + StringUtil_mxJPO.joinTcl(',', false, this.access, "none") + "\"");

        // check administration access
        this.checkSingleValue(_exportParser,
                              "person",
                              "admin",
                              "\"" + StringUtil_mxJPO.joinTcl(',', false, this.adminAccess, "none") + "\"");

        // prepare list of all assignments
        final Set<String> assigns = new HashSet<String>();
        for (final GroupData group : this.groups)  {
            assigns.add("group \"" + AbstractTest.convertTcl(group.getName()) + "\"");
        }
        for (final RoleData role : this.roles)  {
            assigns.add("role \"" + AbstractTest.convertTcl(role.getName()) + "\"");
        }
        // remove all assigned groups / roles in the TCL update file
        for (final String assign : _exportParser.getLines("/mql/assign/@value"))  {
            if (assigns.contains(assign))  {
                assigns.remove(assign);
            } else  {
                Assert.assertNotNull(assign, "assign " + assign + "not known");
            }
        }
        Assert.assertEquals(assigns.size(), 0, "check that all assignments for groups / roles are defined");
        // password never expires flag
        this.checkValueExists(_exportParser, "person", "neverexpires", (this.passwordNeverExpires != null) && this.passwordNeverExpires);
        this.checkValueExists(_exportParser, "person", "!neverexpires", (this.passwordNeverExpires == null) || !this.passwordNeverExpires);
    }
}

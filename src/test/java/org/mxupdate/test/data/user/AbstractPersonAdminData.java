/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.data.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * @param <PERSONDATA> class derived from abstract administration person
 */
public class AbstractPersonAdminData<PERSONDATA extends AbstractPersonAdminData<?>>
    extends AbstractUserData<PERSONDATA>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
    static  {
        AbstractPersonAdminData.REQUIRED_EXPORT_VALUES.put("description", "");
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
     * @see #containsType(String)
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
     * Assigned products for this person.
     *
     * @see #addProduct(String...)
     * @see #ciFile()
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<String> products = new TreeSet<String>();

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
     * Person wants email?
     *
     * @see #setWantsEmail(Boolean)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private Boolean wantsEmail;

    /**
     * Person wants (internal) icon mail.
     *
     * @see #setWantsIconMail(Boolean)
     * @see #append4CIFileValues(StringBuilder)
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private Boolean wantsIconMail;

    /**
     * Constructor to initialize this administration person.
     *
     * @param _test     related test implementation (where this administration
     *                  person is defined)
     * @param _ci       related configuration type
     * @param _name     name of the administration person
     */
    public AbstractPersonAdminData(final AbstractTest _test,
                                   final AbstractTest.CI _ci,
                                   final String _name)
    {
        super(_test, _ci, _name, AbstractPersonAdminData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Assigns given <code>_access</code> to this person.
     *
     * @param _access   access to assign
     * @return this person instance
     * @see #access
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA addAccess(final String... _access)
    {
        this.access.addAll(Arrays.asList(_access));
        return (PERSONDATA) this;
    }

    /**
     * Assigns given <code>_adminAccess</code> to this person.
     *
     * @param _adminAccess  administration access to assign
     * @return this person instance
     * @see #adminAccess
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA addAdminAccess(final String... _adminAccess)
    {
        this.adminAccess.addAll(Arrays.asList(_adminAccess));
        return (PERSONDATA) this;
    }

    /**
     * Assigns given <code>_type</code> to this person.
     *
     * @param _type     type to assign
     * @return this person instance
     * @see #types
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA addType(final String... _type)
    {
        this.types.addAll(Arrays.asList(_type));
        return (PERSONDATA) this;
    }

    /**
     * Checks if <code>_type</code> is contained in the list of {@link #types}.
     *
     * @param _type     type to check
     * @return <i>true</i> if <code>_type</code> is contained in {@link #types}
     * @see #types
     */
    public boolean containsType(final String _type)
    {
        return this.types.contains(_type);
    }

    /**
     * Assigns given <code>_role</code> to this person.
     *
     * @param _role     roles to assign
     * @return this person instance
     * @see #roles
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA addRole(final RoleData... _role)
    {
        this.roles.addAll(Arrays.asList(_role));
        return (PERSONDATA) this;
    }

    /**
     * Assigns given <code>_group</code> to this person.
     *
     * @param _group        groups to assign
     * @return this person instance
     * @see #groups
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA addGroup(final GroupData... _group)
    {
        this.groups.addAll(Arrays.asList(_group));
        return (PERSONDATA) this;
    }

    /**
     * Assigns given <code>_group</code> to this person.
     *
     * @param _product      products to assign
     * @return this person instance
     * @see #products
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA addProduct(final String... _product)
    {
        this.products.addAll(Arrays.asList(_product));
        return (PERSONDATA) this;
    }

    /**
     * Defines if for this administration person the password never expires.
     *
     * @param _passwordNeverExpires     <i>true</i> to define that the password
     *                                  never expires
     * @return this person administration data instance
     * @see #passwordNeverExpires
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA setPasswordNeverExpires(final Boolean _passwordNeverExpires)
    {
        this.passwordNeverExpires = _passwordNeverExpires;
        return (PERSONDATA) this;
    }

    /**
     * Defines that the person wants email.
     *
     * @param _wantsEmail   <i>true</i> to define that the person wants email
     * @return this person administration data instance
     * @see #wantsEmail
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA setWantsEmail(final Boolean _wantsEmail)
    {
        this.wantsEmail = _wantsEmail;
        return (PERSONDATA) this;
    }

    /**
     * Defines that the person wants email.
     *
     * @param _wantsIconMail    <i>true</i> to define that the person wants
     *                          icon mail
     * @return this person administration data instance
     * @see #wantsIconMail
     */
    @SuppressWarnings("unchecked")
    public PERSONDATA setWantsIconMail(final Boolean _wantsIconMail)
    {
        this.wantsIconMail = _wantsIconMail;
        return (PERSONDATA) this;
    }

    /**
     * {@inheritDoc}
     * Overwrites the original method to append the definition of
     * {@link #products}.
     */
    @Override()
    public String ciFile()
    {
        return new StringBuilder()
                .append(super.ciFile())
                .append("\nsetProducts ")
                        .append(StringUtil_mxJPO.joinTcl(' ', true, this.products, ""))
                .toString();
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
     * <li>{@link #wantsEmail wants email flag}</li>
     * <li>{@link #wantsIconMail wants icon mail flag}</li>
     * </ul>
     *
     * @param _cmd  string builder with the TCL commands of the configuration
     *              item file
     * @see #values
     */
    @Override()
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
        // wants email
        if (this.wantsEmail != null)  {
            _cmd.append(' ')
                .append(this.wantsEmail ? "enable" : "disable")
                .append(" email");
        }
        // wants icon mail
        if (this.wantsIconMail != null)  {
            _cmd.append(' ')
                .append(this.wantsIconMail ? "enable" : "disable")
                .append(" iconmail");
        }
    }

    /**
     * {@inheritDoc}
     * Creates depending {@link #groups} and {@link #roles}.
     *
     * @see #groups
     * @see #roles
     */
    @Override()
    @SuppressWarnings("unchecked")
    public PERSONDATA createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create depending roles
        for (final RoleData role : this.roles)  {
            role.create();
        }

        // create depending groups
        for (final GroupData group : this.groups)  {
            group.create();
        }

        return (PERSONDATA) this;
    }

    /**
     * Appends the {@link #products} to this person instance.
     *
     * @return this administration person instance
     * @throws MatrixException if create failed
     * @see #products
     */
    @Override()
    @SuppressWarnings("unchecked")
    public PERSONDATA create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            super.create();
            for (final String product : this.products)  {
                this.getTest().mql("escape mod product \"" + AbstractTest.convertMql(product)
                                    + "\" add person \"" + AbstractTest.convertMql(this.getName()) + "\"");
            }
        }
        return (PERSONDATA) this;
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
     * <li>{@link #wantsEmail wants email flag}</li>
     * <li>{@link #wantsIconMail wants icon mail flag}</li>
     * </ul>
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #values
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        this.setCreated(true);

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
        // wants email
        if (this.wantsEmail != null)  {
            _cmd.append(' ')
                .append(this.wantsEmail ? "enable" : "disable")
                .append(" email");
        }
        // wants icon mail
        if (this.wantsIconMail != null)  {
            _cmd.append(' ')
                .append(this.wantsIconMail ? "enable" : "disable")
                .append(" iconmail");
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
     * <li>{@link #wantsEmail wants email flag} is correct defined</li>
     * <li>{@link #wantsIconMail wants icon mail flag} is correct defined</li>
     * <li>all {@link #products} are correct defined</li>
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

        // email / icon mail flags
        final Set<String> enabled = new HashSet<String>(_exportParser.getLines("/mql/enable/@value"));
        final Set<String> disabled = new HashSet<String>(_exportParser.getLines("/mql/disable/@value"));
        if ((this.wantsEmail != null) && this.wantsEmail)  {
            Assert.assertTrue(enabled.contains("email"), "check email enabled");
            Assert.assertFalse(disabled.contains("email"), "check email not disabled");
        } else  {
            Assert.assertFalse(enabled.contains("email"), "check email not enabled");
            Assert.assertTrue(disabled.contains("email"), "check email disabled");
        }
        if ((this.wantsIconMail == null) || this.wantsIconMail)  {
            Assert.assertTrue(enabled.contains("iconmail"), "check icon mail enabled");
            Assert.assertFalse(disabled.contains("iconmail"), "check icon mail not disabled");
        } else  {
            Assert.assertFalse(enabled.contains("iconmail"), "check icon mail not enabled");
            Assert.assertTrue(disabled.contains("iconmail"), "check icon mail disabled");
        }

        // check products
        final List<String> tclProducts = _exportParser.getLines("/setProducts/@value");
        Assert.assertEquals(
                tclProducts.size(),
                1,
                "check that exact one line with definition for products exists");
        final String tclProduct = tclProducts.get(0);
        Assert.assertEquals(
                tclProduct,
                StringUtil_mxJPO.joinTcl(' ', true, this.products, ""),
                "check that all products are correct defined");
    }
}

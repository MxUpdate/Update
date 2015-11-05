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

package org.mxupdate.update.user;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

import matrix.util.MatrixException;

/**
 * The class is used to handle administration persons.
 * Following properties are supported:
 * <ul>
 * <li>package</li>
 * <li>uuid</li>
 * <li>symbolic names</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #fullName full name}</li>
 * <li>{@link #emailAddress email adress</li>
 * <li>{@link #address}</li>
 * <li>{@link #fax}</li>
 * <li>{@link #phone}</li>
 * <li>{@link #active} flag</li>
 * <li>{@link #trusted} flag</li>
 * <li>{@link #email} flag</li>
 * <li>{@link #iconmail} flag</li>
 * <li>{@link #access} settings</li>
 * <li>{@link #admin} access settings</li>
 * <li>{@link #application}</li>
 * <li>{@link #products}</li>
 * <li>{@link #type type access}</li>
 * </li>
 *
 * @author The MxUpdate Team
 */
public class PersonCI_mxJPO
    extends AbstractUser_mxJPO<PersonCI_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for persons. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        PersonCI_mxJPO.IGNORED_URLS.add("/assignmentList");
        PersonCI_mxJPO.IGNORED_URLS.add("/assignmentList/assignment");
        PersonCI_mxJPO.IGNORED_URLS.add("/access");
        PersonCI_mxJPO.IGNORED_URLS.add("/adminAccess");
        PersonCI_mxJPO.IGNORED_URLS.add("/defaultApplication");
        PersonCI_mxJPO.IGNORED_URLS.add("/homeVault");
        // password settings are completely ignored!
        PersonCI_mxJPO.IGNORED_URLS.add("/password");
        PersonCI_mxJPO.IGNORED_URLS.add("/passwordChangeRequired");
        PersonCI_mxJPO.IGNORED_URLS.add("/passwordModification");
        PersonCI_mxJPO.IGNORED_URLS.add("/passwordModification/datetime");
        PersonCI_mxJPO.IGNORED_URLS.add("/productList");
        PersonCI_mxJPO.IGNORED_URLS.add("/passwordNeverExpires");
    }

    /** Holds all group assignments of this person. */
    private final SortedSet<String> groups = new TreeSet<>();

    /** Holds all role assignments of this person. */
    private final SortedSet<String> roles = new TreeSet<>();

    /** Full name of the person. */
    private String fullName = "";

    /** Email address of the person. */
    private String emailAddress = "";

    /** Address of the person. */
    private String address = "";

    /** Fax number of the person. */
    private String fax = "";

    /** Phone number of the person.*/
    private String phone = "";

    /** Default vault of the person.*/
    private String vault = "";

    /** Is the person not active? */
    private boolean active = true;

    /** Is the person a trusted user? */
    private boolean trusted = false;

    /** Person wants email? */
    private boolean email = false;

    /** Person wants (internal) icon mail. */
    private boolean iconmail = true;

    /** Set of access for this person. */
    private final SortedSet<String> access = new TreeSet<>();

    /** Set of administration access for this person. */
    private final SortedSet<String> admin = new TreeSet<>();

    /**  Defines the name of the assigned default application.  */
    private String application = "";

    /** All assigned products of the person. */
    private final SortedSet<String> products = new TreeSet<>();

    /** All assigned types of the person. */
    private final SortedSet<TypeItem> types = new TreeSet<>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _mxName   MX name of the administration object
     */
    public PersonCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Person, _mxName);
        this.access.add("all");
        this.types.add(TypeItem.APPLICATION);
        this.types.add(TypeItem.FULL);
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        // define dummy admin access (to be checked after parsing..)
        this.admin.clear();
        this.admin.add("dummy");

        new PersonCIParser_mxJPO(new StringReader(_code)).parse(this);

        // fix none admin access
        if ((this.admin.size() == 1) && this.admin.iterator().next().equals("none")) {
            this.admin.clear();
        }
        // fix dummy admin access
        if ((this.admin.size() == 1) && this.admin.iterator().next().equals("dummy")) {
            this.admin.clear();
            if (this.types.contains(TypeItem.BUSINESS) || this.types.contains(TypeItem.SYSTEM))  {
                this.admin.add("all");
            }
        }
        // fix none access
        if ((this.access.size() == 1) && this.access.iterator().next().equals("none")) {
            this.access.clear();
        }

        this.prepare();
    }

    @Override
    public void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, ParseException
    {
        // to ensure that the value is the same default as in the DB
        this.access.clear();
        this.types.clear();
        this.iconmail = false;
        this.active = true;
        super.parse(_paramCache);
    }

    /**
     * <p>Parses all person specific URL values. This includes:
     * <ul>
     * <li>{@link #groups assigned groups}</li>
     * <li>{@link #roles assigned roles}</li>
     * <li>{@link #access}</li>
     * <li>{@link #adminAccess business administration access}</li>
     * <li>is person {@link #isInactive inactive}</li>
     * <li>flag if the person is an {@link #isApplicationUser application user}
     *     </li>
     * <li>flag if the person is a {@link #isFullUser full user}</li>
     * <li>flag if the person is a
     *     {@link #isBusinessAdministrator business administrator}</li>
     * <li>flag if the person is a
     *     {@link #isSystemAdministrator system administrator}</li>
     * <li>flag if the person is a {@link #isTrusted trusted} person</li>
     * <li>person wants {@link #wantsEmail email}</li>
     * <li>person wants {@link #wantsIconMail icon mail}</li>
     * <li>{@link #address}</li>
     * <li>{@link #email email address}</li>
     * <li>{@link #fax fax number}</li>
     * <li>{@link #fullName full name}</li>
     * <li>{@link #vault default vault}</li>
     * <li>{@link #phone phone number}</li>
     * <li>{@link #passwordNeverExpires password never expires flag}</li>
     * <li>{@link #products assigned products}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content depending on the URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (PersonCI_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/assignmentList/assignment/groupRef".equals(_url))  {
            this.groups.add(_content);
            parsed = true;
        } else if ("/assignmentList/assignment/roleRef".equals(_url))  {
            this.roles.add(_content);
            parsed = true;
        } else if (_url.startsWith("/access"))  {
            final String value = _url.substring(8).replaceAll("Access$", "");
            if (!"none".equals(value)) {
                this.access.add(value);
            }
            parsed = true;
        } else if (_url.startsWith("/adminAccess")) {
            final String value = _url.substring(13).replaceAll("Access$", "");
            if (!"none".equals(value)) {
                this.admin.add(value);
            }
            parsed = true;
        } else if ("/defaultApplication/applicationRef".equals(_url))  {
            this.application = _content;
            parsed = true;

        } else if ("/inactive".equals(_url))  {
            this.active = false;
            parsed = true;
        } else if ("/applicationsOnly".equals(_url))  {
            this.types.add(TypeItem.APPLICATION);
            parsed = true;
        } else if ("/fullUser".equals(_url))  {
            this.types.add(TypeItem.FULL);
            parsed = true;
        } else if ("/businessAdministrator".equals(_url))  {
            this.types.add(TypeItem.BUSINESS);
            parsed = true;
        } else if ("/systemAdministrator".equals(_url))  {
            this.types.add(TypeItem.SYSTEM);
            parsed = true;
        } else if ("/trusted".equals(_url))  {
            this.trusted = true;
            parsed = true;
        } else if ("/wantsEmail".equals(_url))  {
            this.email = true;
            parsed = true;
        } else if ("/wantsIconMail".equals(_url))  {
            this.iconmail = true;
            parsed = true;
        } else if ("/address".equals(_url))  {
            this.address = _content;
            parsed = true;
        } else if ("/email".equals(_url))  {
            this.emailAddress = _content;
            parsed = true;
        } else if ("/fax".equals(_url))  {
            this.fax = _content;
            parsed = true;
        } else if ("/fullName".equals(_url))  {
            this.fullName = _content;
            parsed = true;
        } else if ("/homeVault/vaultRef".equals(_url))  {
            this.vault = _content;
            parsed = true;
        } else if ("/phone".equals(_url))  {
            this.phone = _content;
            parsed = true;
        } else if ("/productList/productRef".equals(_url))  {
            this.products.add(_content);
            parsed = true;
        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * The original export MQL command from the super class could not be used,
     * because otherwise all sets and mails of the person to export are also
     * exported (but the sets and mails of the person is not needed....).
     *
     * @return XML export of the person
     */
    @Override
    protected String execXMLExport(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        return MqlBuilderUtil_mxJPO.mql().cmd("escape export person ").arg(this.getName()).cmd(" !mail !set xml").exec(_paramCache.getContext());
    }

    /**
     * <p>Calculates if workspace objects for this person are not handled. This
     * is done by checking if the name of this person matches one of the match
     * lists defined with parameter {@link #PARAM_IGNORE_WSO_PERSONS}. In this
     * case the the workspace objects are ignored for this person.</p>
     *
     * @param _paramCache       parameter cache
     * @return <i>true</i> if the handling of workspace objects for this person
     *         is ignored
     * @see #PARAM_IGNORE_WSO_PERSONS
     */
    @Override
    protected boolean ignoreWorkspaceObjects(final ParameterCache_mxJPO _paramCache)
    {
        boolean ignore = super.ignoreWorkspaceObjects(_paramCache);
        if (!ignore)
        {
            final Collection<String> ignoreMatches = _paramCache.getValueList(ParameterCache_mxJPO.ValueKeys.UserIgnoreWSO4Persons);
            if (ignoreMatches != null) {
                ignore = StringUtil_mxJPO.match(this.getName(), ignoreMatches);
            }
        }
        return ignore;
    }

    @Override
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //                  tag              | default | value                     | write?
                .stringNotNull(          "package",              this.getPackageRef())
                .stringNotNull(          "uuid",                 this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(                   "symbolicname",         this.getSymbolicNames())
                .string(                 "comment",              this.getDescription())
                .flag(                   "active",        false, this.active)
                .flag(                   "trusted",       false, this.trusted)
                .flag(                   "hidden",        false, this.isHidden())
                .singleIfTrue(           "access",               "all",                    this.access.size() == 1 && this.access.iterator().next().equals("all"))
                .listOneLineSingleIfTrue("access",               this.access,              !(this.access.size() == 1 && this.access.iterator().next().equals("all")))
                .singleIfTrue(           "admin",                "all",                    this.admin.size() == 1 && this.admin.iterator().next().equals("all"))
                .listOneLineSingleIfTrue("admin",                this.admin,               !(this.admin.size() == 1 && this.admin.iterator().next().equals("all")))
                .flag(                   "email",         false, this.email)
                .flag(                   "iconmail",      false, this.iconmail)
                .string(                 "address",              this.address)
                .string(                 "emailaddress",         this.emailAddress)
                .string(                 "fax",                  this.fax)
                .string(                 "fullname",             this.fullName)
                .string(                 "phone",                this.phone)
                .listOneLineSingle(      "product",              this.products)
                .listOneLineSingle(      "type",                 this.getTypes())
                .stringIfTrue(           "vault",                this.vault,               this.vault != null && !this.vault.isEmpty())
                .stringIfTrue(           "application",          this.application,         this.application != null && !this.application.isEmpty())
                .stringIfTrue(           "site",                 this.getSite(),           this.getSite() != null && !this.getSite().isEmpty())
                .listIfTrue(             "group",                this.groups,              this.groups != null && !this.groups.isEmpty())
                .listIfTrue(             "role",                 this.roles,               this.roles != null && !this.roles.isEmpty())
                .properties(this.getProperties());
    }

    /**
     * @return sorted string set
     */
    private SortedSet<String> getTypes()
    {
        final SortedSet<String> ret = new TreeSet<>();
        for (final TypeItem item : this.types) {
            ret.add(item.name().toLowerCase());
        }
        return ret;
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final PersonCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcPackage(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);

        // type (application, full, business, system, inactive, trusted)
        if ((CompareToUtil_mxJPO.compare(0, this.types, _current.types) != 0) || (this.active != _current.active) || (this.trusted != _current.trusted))  {
            _mql.newLine().cmd("type ");
            boolean first = true;
            for (final TypeItem typeItem : TypeItem.values())  {
                if (first)  {
                    first = false;
                } else {
                    _mql.cmd(",");
                }
                if (this.types.contains(typeItem))  {
                    _mql.arg(typeItem.name().toLowerCase());
                } else {
                    _mql.arg("not" + typeItem.name().toLowerCase());
                }
            }
            _mql.cmd(",");
            if (this.active) {
                _mql.arg("notinactive");
            } else {
                _mql.arg("inactive");
            }
            _mql.cmd(",");
            if (this.trusted) {
                _mql.arg("trusted");
            } else {
                _mql.arg("nottrusted");
            }
        }

        if (CompareToUtil_mxJPO.compare(0, this.access, _current.access) != 0)  {
            if (this.access.isEmpty()) {
                _mql.newLine().cmd("access none");
            } else if (this.access.size() == 1 && this.access.iterator().next().equals("all")) {
                _mql.newLine().cmd("access all");
            } else {
                DeltaUtil_mxJPO.calcLstOneCallDelta(  _mql,      "access",       this.access,            _current.access);
            }
        }
        if (CompareToUtil_mxJPO.compare(0, this.admin, _current.admin) != 0)  {
            if (this.admin.isEmpty())  {
                _mql.newLine().cmd("admin none");
            } else if (this.admin.size() == 1 && this.admin.iterator().next().equals("all"))  {
                _mql.newLine().cmd("admin all");
            } else {
                DeltaUtil_mxJPO.calcLstOneCallDelta(  _mql,      "admin",       this.admin,            _current.admin);
            }
        }

        final Collection<String> ignoreEmailMatches = _paramCache.getValueList(ParameterCache_mxJPO.ValueKeys.UserPersonIgnoreWantsEmail);
        final boolean ignoreEmail = ignoreEmailMatches != null &&  StringUtil_mxJPO.match(this.getName(), ignoreEmailMatches);
        if (!ignoreEmail && (this.email != _current.email))  {
            if (this.email)  {
                _mql.newLine().cmd("enable email");
            } else {
                _mql.newLine().cmd("disable email");
            }
        }

        final Collection<String> ignoreIconMailMatches = _paramCache.getValueList(ParameterCache_mxJPO.ValueKeys.UserPersonIgnoreWantsIconMail);
        final boolean ignoreIconMail = ignoreIconMailMatches != null &&  StringUtil_mxJPO.match(this.getName(), ignoreIconMailMatches);
        if (!ignoreIconMail && (this.iconmail != _current.iconmail))  {
            if (this.iconmail)  {
                _mql.newLine().cmd("enable iconmail");
            } else {
                _mql.newLine().cmd("disable iconmail");
            }
        }

        DeltaUtil_mxJPO.calcValueDelta( _mql,   "comment",          this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(  _mql,   "hidden",    false, this.isHidden(),        _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "address",          this.address,           _current.address);
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "email",            this.emailAddress,      _current.emailAddress);
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "fax",              this.fax,               _current.fax);
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "fullname",         this.fullName,          _current.fullName);
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "phone",            this.phone,             _current.phone);
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "vault",            this.vault,             _current.vault);
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "application",      this.application,       _current.application);
        DeltaUtil_mxJPO.calcValueDelta( _mql,   "site",             this.getSite(),         _current.getSite());

        // groups (specific syntax...)
        for (final String curValue : _current.groups)  {
            if (!this.groups.contains(curValue))  {
                _mql.newLine()
                    .cmd("remove assign group ").arg(curValue);
            }
        }
        for (final String newValue : this.groups)  {
            if (!_current.groups.contains(newValue))  {
                _mql.newLine()
                    .cmd("assign group ").arg(newValue);
            }
        }

        // roles (specific syntax...)
        for (final String curValue : _current.roles)  {
            if (!this.roles.contains(curValue))  {
                _mql.newLine()
                    .cmd("remove assign role ").arg(curValue);
            }
        }
        for (final String newValue : this.roles)  {
            if (!_current.roles.contains(newValue))  {
                _mql.newLine()
                    .cmd("assign role ").arg(newValue);
            }
        }

        this.getProperties().calcDelta(_mql, "", _current.getProperties());

        // products
        final Collection<String> ignoreProdMatches = _paramCache.getValueList(ParameterCache_mxJPO.ValueKeys.UserPersonIgnoreProducts);
        final boolean ignoreProd = ignoreProdMatches != null &&  StringUtil_mxJPO.match(this.getName(), ignoreProdMatches);
        if (!ignoreProd && CompareToUtil_mxJPO.compare(0, this.products, _current.products) != 0 ) {
            _mql.pushPrefix("");
            for (final String curValue : _current.products)  {
                if (!this.products.contains(curValue))  {
                    _mql.newLine()
                        .cmd("escape mod product ").arg(curValue).cmd(" remove person ").arg(this.getName());
                }
            }
            for (final String newValue : this.products)  {
                if (!_current.products.contains(newValue))  {
                    _mql.newLine()
                        .cmd("escape mod product ").arg(newValue).cmd(" add person ").arg(this.getName());
                }
            }
            _mql.popPrefix();
        }
    }


    public enum TypeItem
    {
        /** Application User. */
        APPLICATION,
        /** Full User. */
        FULL,
        /** Business User. */
        BUSINESS,
        /** System Admin User. */
        SYSTEM
    }
}

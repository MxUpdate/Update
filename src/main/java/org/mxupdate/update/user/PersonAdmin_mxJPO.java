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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to handle administration persons.
 *
 * @author The MxUpdate Team
 */
public class PersonAdmin_mxJPO
    extends AbstractUser_mxJPO<PersonAdmin_mxJPO>
{
    /**
     * Called TCL procedure within the TCL update to assign current person to
     * required products. Already assigned products are removed.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_SET_PRODUCTS
            = "proc setProducts {args}  {\n"
                + "global NAME\n"
                + "set lsCurrent [split [mql print person \"${NAME}\" select product dump '\\n'] '\\n']\n"
                + "foreach sOneProduct ${lsCurrent}  {\n"
                    + "if {([string length \"${sOneProduct}\"] > 0) && ([lsearch ${args} \"${sOneProduct}\"] < 0)}  {\n"
                        + "logDebug \"    - remove product '${sOneProduct}'\"\n"
                        + "mql mod product \"${sOneProduct}\" remove person \"${NAME}\"\n"
                    + "}\n"
                + "}\n"
                + "foreach sOneProduct ${args}  {\n"
                    + "if {[lsearch ${lsCurrent} \"${sOneProduct}\"] < 0}  {\n"
                      + "logDebug \"    - assign product '${sOneProduct}'\"\n"
                      + "mql mod product \"${sOneProduct}\" add person \"${NAME}\"\n"
                    + "}\n"
                + "}\n"
            + "}\n";

    /**
     * Dummy procedure with logging information that the definition of products
     * is ignored.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_SET_PRODUCTS_DUMMY
            = "proc setProducts {args}  {\n"
                + "logDebug \"    - ignoring definition of products ${args}\""
            + "}\n";

    /**
     * Set of all ignored URLs from the XML definition for persons.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        PersonAdmin_mxJPO.IGNORED_URLS.add("/assignmentList");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/assignmentList/assignment");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/access");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/adminAccess");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/defaultApplication");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/homeVault");
        // password settings are completely ignored!
        PersonAdmin_mxJPO.IGNORED_URLS.add("/password");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/passwordChangeRequired");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/passwordModification");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/passwordModification/datetime");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/productList");
    }

    /**
     * Defines the parameter for the match of persons for which workspace
     * objects are not handled (neither exported nor updated).
     *
     * @see #ignoreWorkspaceObjects(ParameterCache_mxJPO)
     */
    private static final String PARAM_IGNORE_WSO_PERSONS = "UserIgnoreWSO4Persons";

    /**
     * If the parameter is set the 'password never expires' - flag for persons
     * is ignored. This means that the flag is not managed anymore from the
     * MxUpdate Update tool.
     *
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_IGNORE_PSWD_NEVER_EXPIRES = "UserPersonIgnorePswdNeverExpires";

    /**
     * If the parameter is set the 'wants email' - flag for persons is ignored.
     * This means that the flag is not managed anymore from the MxUpdate Update
     * tool.
     *
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_IGNORE_WANTS_EMAIL = "UserPersonIgnoreWantsEmail";

    /**
     * If the parameter is set the 'wants icon mail' - flag for persons
     * matching given string is ignored. This means that the flag is not
     * managed anymore from the MxUpdate Update tool.
     *
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_IGNORE_WANTS_ICON_MAIL = "UserPersonIgnoreWantsIconMail";

    /**
     * If the parameter is set, products for persons matching given string are
     * not updated.
     *
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_IGNORE_PRODUCTS = "UserPersonIgnoreProducts";

    /**
     * Holds all group assignments of this person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> groups = new TreeSet<String>();

    /**
     * Holds all role assignments of this person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> roles = new TreeSet<String>();

    /**
     * Full name of the person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String fullName;

    /**
     * Email address of the person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String email;

    /**
     * Address of the person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String address;

    /**
     * Fax number of the person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String fax;

    /**
     * Phone number of the person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String phone;

    /**
     * Default vault of the person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String vault;

    /**
     * Is the person not active?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isInactive = false;

    /**
     * Is the person an application user?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)s
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isApplicationUser = false;

    /**
     * Is the person a full user?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isFullUser = false;

    /**
     * Is the person a business administrator?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isBusinessAdministrator = false;

    /**
     * Is the person a system administrator?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isSystemAdministrator = false;

    /**
     * Is the person a trusted user?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isTrusted = false;

    /**
     * Person wants email?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean wantsEmail = false;

    /**
     * Person wants (internal) icon mail.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean wantsIconMail = false;

    /**
     * Set of access for this person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> access = new TreeSet<String>();

    /**
     * Set of administration access for this person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> adminAccess = new TreeSet<String>();

    /**
     * Defines the name of the assigned default application.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String defaultApplication;

    /**
     * The password of the person never expires.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean passwordNeverExpires = false;

    /**
     * All assigned products of the person.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> products = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  person type definition
     * @param _mxName   MX name of the administration object
     */
    public PersonAdmin_mxJPO(final TypeDef_mxJPO _typeDef,
                             final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Returns a set of found person names. First for all administration
     * persons are searched. Then, all persons are removed from the matching
     * set, for whom a related business person object exists. This new set is
     * returned.
     *
     * @param _paramCache   parameter cache
     * @return set of person names for which no person business object exists
     * @throws MatrixException if the query for person objects failed
     */
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final Set<String> persons = super.getMxNames(_paramCache);

        // evaluate for all business person object names
        final StringList selects = new StringList();
        selects.addElement("name");
        final Query query = new Query();
        query.open(_paramCache.getContext());
        query.setBusinessObjectType(this.getTypeDef().getMxBusType());
        final BusinessObjectWithSelectList list = query.select(_paramCache.getContext(), selects);
        query.close(_paramCache.getContext());

        // remove the found persons which related business object
        for (final Object mapObj : list)  {
            final BusinessObjectWithSelect map = (BusinessObjectWithSelect) mapObj;
            final String busName = (String) map.getSelectDataList("name").get(0);
            persons.remove(busName);
        }

        return persons;
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
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
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (PersonAdmin_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/assignmentList/assignment/groupRef".equals(_url))  {
            this.groups.add(_content);
            parsed = true;
        } else if ("/assignmentList/assignment/roleRef".equals(_url))  {
            this.roles.add(_content);
            parsed = true;

        } else if (_url.startsWith("/access"))  {
            this.access.add(_url.substring(8).replaceAll("Access$", ""));
            parsed = true;

        } else if (_url.startsWith("/adminAccess"))  {
            this.adminAccess.add(_url.substring(13).replaceAll("Access$", ""));
            parsed = true;

        } else if ("/defaultApplication/applicationRef".equals(_url))  {
            this.defaultApplication = _content;
            parsed = true;

        } else if ("/inactive".equals(_url))  {
            this.isInactive = true;
            parsed = true;
        } else if ("/applicationsOnly".equals(_url))  {
            this.isApplicationUser = true;
            parsed = true;
        } else if ("/fullUser".equals(_url))  {
            this.isFullUser = true;
            parsed = true;
        } else if ("/businessAdministrator".equals(_url))  {
            this.isBusinessAdministrator = true;
            parsed = true;
        } else if ("/systemAdministrator".equals(_url))  {
            this.isSystemAdministrator = true;
            parsed = true;
        } else if ("/trusted".equals(_url))  {
            this.isTrusted = true;
            parsed = true;

        } else if ("/wantsEmail".equals(_url))  {
            this.wantsEmail = true;
            parsed = true;
        } else if ("/wantsIconMail".equals(_url))  {
            this.wantsIconMail = true;
            parsed = true;

        } else if ("/address".equals(_url))  {
            this.address = _content;
            parsed = true;
        } else if ("/email".equals(_url))  {
            this.email = _content;
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

        } else if ("/passwordNeverExpires".equals(_url))  {
            this.passwordNeverExpires = true;
            parsed = true;

        } else if ("/productList/productRef".equals(_url))  {
            this.products.add(_content);
            parsed = true;

        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * The original export MQL command from the super class could not be used,
     * because otherwise all sets and mails of the person to export are also
     * exported (but the sets and mails of the person is not needed....).
     *
     * @return MQL command to make an XML export of the person
     */
    @Override()
    protected String getExportMQL()
    {
        return new StringBuilder()
                .append("escape export person \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                .append("\" !mail !set xml")
                .toString();
    }

    /**
     * Writes specific information about the cached administration person to
     * the given TCL update file <code>_out</code>. The included information is
     * <ul>
     * <li>{@link #passwordNeverExpires password never expires flag} (if
     *     parameter {@link #PARAM_IGNORE_PSWD_NEVER_EXPIRES} is not set)</li>
     * <li>{@link #access}</li>
     * <li>{@link #adminAccess business administration access}</li>
     * <li>person wants {@link #wantsEmail email} (if parameter
     *     {@link #PARAM_IGNORE_WANTS_EMAIL} is not set)</li>
     * <li>person wants {@link #wantsIconMail icon mail} (if parameter
     *     {@link #PARAM_IGNORE_WANTS_ICON_MAIL} is not set)</li>
     * <li>{@link #address}</li>
     * <li>{@link #email email address}</li>
     * <li>{@link #fax fax number}</li>
     * <li>{@link #fullName full name}</li>
     * <li>{@link #phone phone number}</li>
     * <li>{@link #vault default vault}</li>
     * <li>{@link #defaultApplication default application} (if defined)</li>
     * <li>assigned {@link #groups}</li>
     * <li>assigned {@link #roles}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     * @see #PARAM_IGNORE_PSWD_NEVER_EXPIRES
     * @see #PARAM_IGNORE_WANTS_EMAIL
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        super.writeObject(_paramCache, _out);

        // password never expires flag if not matched
        final Collection<String> ignorePswdNeverExpires = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_PSWD_NEVER_EXPIRES);
        if ((ignorePswdNeverExpires == null) || !StringUtil_mxJPO.match(this.getName(), ignorePswdNeverExpires))  {
            _out.append(" \\\n    ").append(this.passwordNeverExpires ? "" : "!").append("neverexpires");
        }
        _out.append(" \\\n    access \"").append(StringUtil_mxJPO.joinTcl(',', false, this.access, "none")).append("\"")
            .append(" \\\n    admin \"").append(StringUtil_mxJPO.joinTcl(',', false, this.adminAccess, "none")).append("\"");
        // wants email only if not matched
        final Collection<String> ignoreWantsEmail = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_WANTS_EMAIL);
        if ((ignoreWantsEmail == null) || !StringUtil_mxJPO.match(this.getName(), ignoreWantsEmail))  {
            _out.append(" \\\n    ").append(this.wantsEmail ? "enable" : "disable").append(" email");
        }
        // wants icon mail only if not matched
        final Collection<String> ignoreWantsIconMail = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_WANTS_ICON_MAIL);
        if ((ignoreWantsIconMail == null) || !StringUtil_mxJPO.match(this.getName(), ignoreWantsIconMail))  {
            _out.append(" \\\n    ").append(this.wantsIconMail ? "enable" : "disable").append(" iconmail");
        }
        _out.append(" \\\n    address \"").append(StringUtil_mxJPO.convertTcl(this.address)).append("\"")
            .append(" \\\n    email \"").append(StringUtil_mxJPO.convertTcl(this.email)).append("\"")
            .append(" \\\n    fax \"").append(StringUtil_mxJPO.convertTcl(this.fax)).append("\"")
            .append(" \\\n    fullname \"").append(StringUtil_mxJPO.convertTcl(this.fullName)).append("\"")
            .append(" \\\n    phone \"").append(StringUtil_mxJPO.convertTcl(this.phone)).append("\"")
            .append(" \\\n    vault \"").append(StringUtil_mxJPO.convertTcl(this.vault)).append("\"");
        if (this.defaultApplication != null)  {
            _out.append(" \\\n    application \"")
                .append(StringUtil_mxJPO.convertTcl(this.defaultApplication)).append("\"");
        }
        for (final String group : this.groups)  {
            _out.append(" \\\n    assign group \"")
                .append(StringUtil_mxJPO.convertTcl(group))
                .append("\"");
        }
        for (final String role : this.roles)  {
            _out.append(" \\\n    assign role \"")
                .append(StringUtil_mxJPO.convertTcl(role))
                .append("\"");
        }
    }

    /**
     * Appends at the end of the TCL update file the change of the person
     * &quot;type&quot; and others which could not be executed within same
     * statement of the person properties.
     * <ul>
     * <li>type flag if person is {@link #isApplicationUser application user}</li>
     * <li>type flag if person is {@link #isFullUser full user}</li>
     * <li>type flag if person is
     *     {@link #isBusinessAdministrator business administration}</li>
     * <li>type flag if person is {@link #isInactive inactive}</li>
     * <li>type flag if person is {@link #isTrusted trusted}</li>
     * <li>type flag if person is
     *     {@link #isSystemAdministrator system administrator}</li>
     * <li>assigned {@link #products}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     */
    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
            throws IOException
    {
        super.writeEnd(_paramCache, _out);
        _out.append("\nmql mod person \"${NAME}\" type ");
        if (!this.isApplicationUser)  {
            _out.append("not");
        }
        _out.append("application,");
        if (!this.isFullUser)  {
            _out.append("not");
        }
        _out.append("full,");
        if (!this.isBusinessAdministrator)  {
            _out.append("not");
        }
        _out.append("business,");
        if (!this.isInactive)  {
            _out.append("not");
        }
        _out.append("inactive,");
        if (!this.isTrusted)  {
            _out.append("not");
        }
        _out.append("trusted,");
        if (!this.isSystemAdministrator)  {
            _out.append("not");
        }
        _out.append("system");

        // define products
        final Collection<String> matchIgnoreProducts = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_PRODUCTS);
        if ((matchIgnoreProducts == null) || !StringUtil_mxJPO.match(this.getName(), matchIgnoreProducts))  {
            _out.append("\nsetProducts")
                .append((this.products.isEmpty() ? "" : " "))
                .append(StringUtil_mxJPO.joinTcl(' ', true, this.products, ""));
        }
    }

    /**
     * The method overwrites the original method to
     * <ul>
     * <li>reset the comment (description)</li>
     * <li>set the version and author attribute</li>
     * <li>reset all not ignored attributes</li>
     * <li>define the TCL variable &quot;OBJECTID&quot; with the object id of
     *     the represented business object</li>
     * <li>sets the {@link #passwordNeverExpires password never expires flag}
     *     to <i>false</i> (means that the password expires); depends on
     *     parameter {@link #PARAM_IGNORE_PSWD_NEVER_EXPIRES}</li>
     * <li>disables that the person wants email; depends on
     *     parameter {@link #PARAM_IGNORE_WANTS_EMAIL}</li>
     * <li>enables that the person wants icon mail; depends on
     *     parameter {@link #PARAM_IGNORE_WANTS_ICON_MAIL}</li>
     * </ul>
     * The original method of the super class if called surrounded with a
     * history off, because if the update itself is done the modified basic
     * attribute and the version attribute of the business object is updated.
     * <br/>
     * The new generated MQL code is set in the front of the already defined
     * MQL code in <code>_preMQLCode</code> and appended to the MQL statements
     * in <code>_postMQLCode</code>.
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     * @see #PARAM_IGNORE_PSWD_NEVER_EXPIRES
     * @see #PARAM_IGNORE_WANTS_EMAIL
     * @see #PARAM_IGNORE_WANTS_ICON_MAIL
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        // append TCL procedures
        final StringBuilder preTCLCode = new StringBuilder()
                .append(_preTCLCode)
                .append('\n');

        // append TCL set products if not ignored (otherwise dummy TCL proc)
        final Collection<String> matchIgnoreProducts = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_PRODUCTS);
        if ((matchIgnoreProducts == null) || !StringUtil_mxJPO.match(this.getName(), matchIgnoreProducts))  {
            preTCLCode.append(PersonAdmin_mxJPO.TCL_SET_PRODUCTS);
        } else  {
            preTCLCode.append(PersonAdmin_mxJPO.TCL_SET_PRODUCTS_DUMMY);
        }

        // append other pre MQL code
        final StringBuilder preMQLCode = new StringBuilder()
                .append(_preMQLCode)
                .append("escape mod person \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                        .append("comment '' ")
                        .append("access none ")
                        .append("admin none ")
                        .append("address '' ")
                        .append("email '' ")
                        .append("fax '' ")
                        .append("fullname '' ")
                        .append("phone '' ")
                        .append("remove assign all");
        // reset wants email if not ignored
        final Collection<String> ignoreWantsEmail = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_WANTS_EMAIL);
        if ((ignoreWantsEmail == null) || !StringUtil_mxJPO.match(this.getName(), ignoreWantsEmail))  {
            preMQLCode.append(" disable email");
        }
        // reset wants icon mail if not ignored
        final Collection<String> ignoreWantsIconMail = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_WANTS_ICON_MAIL);
        if ((ignoreWantsIconMail == null) || !StringUtil_mxJPO.match(this.getName(), ignoreWantsIconMail))  {
            preMQLCode.append(" enable iconmail");
        }
        // reset password never expires if not ignored
        final Collection<String> ignorePswdNeverExpires = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_PSWD_NEVER_EXPIRES);
        if ((ignorePswdNeverExpires == null) || !StringUtil_mxJPO.match(this.getName(), ignorePswdNeverExpires))  {
            preMQLCode.append(" !neverexpires");
        }
        if (this.defaultApplication != null)  {
            preMQLCode.append(" application \"\"");
        }
        preMQLCode.append(";\n");

        // remove hidden flag
        if (this.isHidden())  {
            preMQLCode.append("escape mod ").append(this.getTypeDef().getMxAdminName())
                      .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                      .append(" !hidden;\n");
        }

        // remove site...
        if (this.getSite() != null)  {
            preMQLCode.append("escape mod ").append(this.getTypeDef().getMxAdminName())
                      .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                      .append(" site \"\";\n");
        }

        super.update(_paramCache, preMQLCode, _postMQLCode, preTCLCode, _tclVariables, _sourceFile);
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
    @Override()
    protected boolean ignoreWorkspaceObjects(final ParameterCache_mxJPO _paramCache)
    {
        boolean ignore = super.ignoreWorkspaceObjects(_paramCache);
        if (!ignore)  {
            final Collection<String> ignoreMatches = _paramCache.getValueList(PersonAdmin_mxJPO.PARAM_IGNORE_WSO_PERSONS);
            if (ignoreMatches != null)  {
                ignore = StringUtil_mxJPO.match(this.getName(), ignoreMatches);
            }
        }
        return ignore;
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final PersonAdmin_mxJPO _current)
        throws UpdateException_mxJPO
    {
    }
}

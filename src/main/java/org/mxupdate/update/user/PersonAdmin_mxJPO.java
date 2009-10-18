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

package org.mxupdate.update.user;

import java.io.File;
import java.io.IOException;
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
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to handle administration persons.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PersonAdmin_mxJPO
        extends AbstractUser_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -8458972437612839433L;

    /**
     * Set of all ignored URLs from the XML definition for persons.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        PersonAdmin_mxJPO.IGNORED_URLS.add("/assignmentList");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/assignmentList/assignment");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/access");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/adminAccess");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/defaultApplication");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/homeVault");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/password");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/passwordModification");
        PersonAdmin_mxJPO.IGNORED_URLS.add("/passwordModification/datetime");
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
     * If the parameter is set the 'wants icon mail' - flag for persons is
     * ignored. This means that the flag is not managed anymore from the
     * MxUpdate Update tool.
     *
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_IGNORE_WANTS_ICON_MAIL = "UserPersonIgnoreWantsIconMail";

    /**
     * Holds all group assignments of this person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> groups = new TreeSet<String>();

    /**
     * Holds all role assignments of this person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> roles = new TreeSet<String>();

    /**
     * Full name of the person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String fullName;

    /**
     * Email address of the person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String email;

    /**
     * Address of the person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String address;

    /**
     * Fax number of the person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String fax;

    /**
     * Phone number of the person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String phone;

    /**
     * Default vault of the person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String vault;

    /**
     * Is the person not active?
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isInactive = false;

    /**
     * Is the person an application user?
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isApplicationUser = false;

    /**
     * Is the person a full user?
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isFullUser = false;

    /**
     * Is the person a business administrator?
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isBusinessAdministrator = false;

    /**
     * Is the person a system administrator?
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isSystemAdministrator = false;

    /**
     * Is the person a trusted user?
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private boolean isTrusted = false;

    /**
     * Person wants email?
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean wantsEmail = false;

    /**
     * Person wants (internal) icon mail.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean wantsIconMail = false;

    /**
     * Set of access for this person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> access = new TreeSet<String>();

    /**
     * Set of administration access for this person.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> adminAccess = new TreeSet<String>();

    /**
     * Defines the name of the assigned default application.
     *
     * @see #parse(String, String)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String defaultApplication;

    /**
     * The password of the person never expires.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean passwordNeverExpires = false;

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
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _url      URL to parse
     * @param _content  content depending on the URL
     * @see #IGNORED_URLS
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if (!PersonAdmin_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/assignmentList/assignment/groupRef".equals(_url))  {
                this.groups.add(_content);
            } else if ("/assignmentList/assignment/roleRef".equals(_url))  {
                this.roles.add(_content);

            } else if (_url.startsWith("/access"))  {
                this.access.add(_url.substring(8).replaceAll("Access$", ""));

            } else if (_url.startsWith("/adminAccess"))  {
                this.adminAccess.add(_url.substring(13).replaceAll("Access$", ""));

            } else if ("/defaultApplication/applicationRef".equals(_url))  {
                this.defaultApplication = _content;

            } else if ("/inactive".equals(_url))  {
                this.isInactive = true;
            } else if ("/applicationsOnly".equals(_url))  {
                this.isApplicationUser = true;
            } else if ("/fullUser".equals(_url))  {
                this.isFullUser = true;
            } else if ("/businessAdministrator".equals(_url))  {
                this.isBusinessAdministrator = true;
            } else if ("/systemAdministrator".equals(_url))  {
                this.isSystemAdministrator = true;
            } else if ("/trusted".equals(_url))  {
                this.isTrusted = true;

            } else if ("/wantsEmail".equals(_url))  {
                this.wantsEmail = true;
            } else if ("/wantsIconMail".equals(_url))  {
                this.wantsIconMail = true;

            } else if ("/address".equals(_url))  {
                this.address = _content;
            } else if ("/email".equals(_url))  {
                this.email = _content;
            } else if ("/fax".equals(_url))  {
                this.fax = _content;
            } else if ("/fullName".equals(_url))  {
                this.fullName = _content;
            } else if ("/homeVault/vaultRef".equals(_url))  {
                this.vault = _content;
            } else if ("/phone".equals(_url))  {
                this.phone = _content;

            } else if ("/passwordNeverExpires".equals(_url))  {
                this.passwordNeverExpires = true;

            } else  {
                super.parse(_url, _content);
            }
        }
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
     * &quot;type&quot;. The person &quot;type&quot; defines, if the person is
     * e.g. active, or trusted etc..
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

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
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
}

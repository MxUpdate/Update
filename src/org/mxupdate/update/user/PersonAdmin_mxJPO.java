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
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.update.util.StringUtil_mxJPO.join;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOff;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOn;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class PersonAdmin_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -8458972437612839433L;

    /**
     * Holds all group assignments of this person.
     */
    private final Set<String> groups = new TreeSet<String>();

    /**
     * Holds all role assignments of this person.
     */
    private final Set<String> roles = new TreeSet<String>();

    /**
     * Full name of the person.
     */
    private String fullName;

    /**
     * Email address of the person.
     */
    private String email;

    /**
     * Address of the person.
     */
    private String address;

    /**
     * Fax of the person.
     */
    private String fax;

    /**
     * Phone of the person.
     */
    private String phone;

    /**
     * Default vault of the person.
     */
    private String vault;

    /**
     * Is the person not active?
     */
    private boolean isInactive = false;

    /**
     * Is the person an application user?
     */
    private boolean isApplicationUser = false;

    /**
     * Is the person a full user?
     */
    private boolean isFullUser = false;

    /**
     * Is the person a business administrator?
     */
    private boolean isBusinessAdministrator = false;

    /**
     * Is the person a system administrator?
     */
    private boolean isSystemAdministrator = false;

    /**
     * Is the person a trusted user?
     */
    private boolean isTrusted = false;

    /**
     * Person wants email?
     */
    private boolean wantsEmail = false;

    /**
     * Person wants (internal) icon mail.
     */
    private boolean wantsIconMail = false;

    /**
     * Set of access for this person.
     */
    private final Set<String> access = new TreeSet<String>();

    /**
     * Set of administration access for this person.
     */
    private final Set<String> adminAccess = new TreeSet<String>();

    /**
     * Defines the site of the person.
     */
    private String site;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param person_mxJPO TODO
     */
    public PersonAdmin_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Returns a set of found person names. First for all matching
     * administration persons is searched. Then, all persons are removed from
     * the matching set, for whom a related business person object exists. This
     * new set is returned.
     *
     * @param _contex       context for this request
     * @param _matched      collection of match strings for which the persons
     *                      are searched
     * @return set of matching person names
     */
    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final Set<String> persons = super.getMatchingNames(_context, _matches);

        // evaluate for all business person object names
        final StringList selects = new StringList();
        selects.addElement("name");
        final Query query = new Query();
        query.open(_context);
        query.setBusinessObjectType(this.getTypeDef().getMxBusType());
        final BusinessObjectWithSelectList list = query.select(_context, selects);
        query.close(_context);

        // remove the found persons which related business object
        for (final Object mapObj : list)  {
            final BusinessObjectWithSelect map = (BusinessObjectWithSelect) mapObj;
            final String busName = (String) map.getSelectDataList("name").get(0);
            persons.remove(busName);
        }

        return persons;
    }

    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/assignmentList".equals(_url))  {
            // to be ignored ...
        } else if ("/assignmentList/assignment".equals(_url))  {
            // to be ignored ...
        } else if ("/assignmentList/assignment/groupRef".equals(_url))  {
            this.groups.add(_content);
        } else if ("/assignmentList/assignment/roleRef".equals(_url))  {
            this.roles.add(_content);

        } else if ("/access".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/access"))  {
            this.access.add(_url.substring(8).replaceAll("Access$", ""));

        } else if ("/adminAccess".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/adminAccess"))  {
            this.adminAccess.add(_url.substring(13).replaceAll("Access$", ""));

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
        } else if ("/homeVault".equals(_url))  {
            // to be ignored ...
        } else if ("/homeVault/vaultRef".equals(_url))  {
            this.vault = _content;
        } else if ("/phone".equals(_url))  {
            this.phone = _content;

        } else if ("/homeSite".equals(_url))  {
        // to be ignored
        } else if ("/homeSite/siteRef".equals(_url))  {
            this.site = _content;

        // password must be ignored...
        } else if (_url.startsWith("/password"))  {

        // to be ignored ...
        } else if (_url.startsWith("/cueList"))  {
        } else if (_url.startsWith("/filterList"))  {
        } else if (_url.startsWith("/queryList"))  {
        } else if (_url.startsWith("/tableList"))  {
        } else if (_url.startsWith("/tipList"))  {
        } else if (_url.startsWith("/toolsetList"))  {
        } else if (_url.startsWith("/viewList"))  {

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * The original export MQL command from the super class could not be used,
     * because otherwise all sets and mails of the person to export are also
     * exported (but the sets and mails of the person is not needed....).
     *
     * @return MQL command to make an XML export of the person
     */
    @Override
    protected String getExportMQL()
    {
        return new StringBuilder()
                .append("export person \"").append(this.getName()).append("\" !mail !set xml")
                .toString();
    }

    @Override
    protected void writeObject(Writer _out)
            throws IOException
    {
        _out.append(" \\\n    access \"").append(join(',', this.access, "none")).append("\"")
            .append(" \\\n    admin \"").append(join(',', this.adminAccess, "none")).append("\"")
            .append(" \\\n    address \"").append(convertTcl(this.address)).append("\"")
            .append(" \\\n    email \"").append(convertTcl(this.email)).append("\"")
            .append(" \\\n    fax \"").append(convertTcl(this.fax)).append("\"")
            .append(" \\\n    fullname \"").append(convertTcl(this.fullName)).append("\"")
            .append(" \\\n    phone \"").append(convertTcl(this.phone)).append("\"")
            .append(" \\\n    vault \"").append(convertTcl(this.vault)).append("\"");
        if (this.site != null)  {
            _out.append(" \\\n    site \"").append(convertTcl(this.site)).append("\"");
        }
        for (final String group : this.groups)  {
            _out.append(" \\\n    assign group \"")
                .append(convertTcl(group))
                .append("\"");
        }
        for (final String role : this.roles)  {
            _out.append(" \\\n    assign role \"")
                .append(convertTcl(role))
                .append("\"");
        }
    }

    /**
     * Appends at the end of the TCL update file the change of the person
     * &quot;type&quot;. The person &quot;type&quot; defines, if the person is
     * e.g. active, or trusted etc..
     *
     * @param _out      appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     */
    @Override
    protected void writeEnd(final Appendable _out)
            throws IOException
    {
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
     * <li>reset the description</li>
     * <li>set the version and author attribute</li>
     * <li>reset all not ignored attributes</li>
     * <li>define the TCL variable &quot;OBJECTID&quot; with the object id of
     *     the represented business object</li>
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
     */
    @Override
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
                .append("mod person \"").append(this.getName()).append("\" ")
                        .append("access none ")
                        .append("admin none ")
                        .append("address '' ")
                        .append("email '' ")
                        .append("fax '' ")
                        .append("fullname '' ")
                        .append("phone '' ")
                        .append("remove assign all;\n");

        // update must be done with history off (because not required...)
        try  {
            setHistoryOff(_paramCache.getContext());
            super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
        } finally  {
            setHistoryOn(_paramCache.getContext());
        }
    }
}
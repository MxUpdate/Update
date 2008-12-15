/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.user;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO;
import net.sourceforge.mxupdate.update.AbstractBusObject_mxJPO;
import net.sourceforge.mxupdate.update.AbstractObject_mxJPO;
import net.sourceforge.mxupdate.update.AbstractPropertyObject_mxJPO;
import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.AdminTypeDef;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.BusTypeDef;

import org.xml.sax.SAXException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.join;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.setHistoryOff;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.setHistoryOn;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@InfoAnno_mxJPO(adminType = AdminTypeDef.Person,
                busType = BusTypeDef.Person)
public class Person_mxJPO
        extends AbstractObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 213737109331352452L;

    /**
     * Administration person instance (used to parse the admin part of a
     * person).
     */
    private final PersonAdmin personAdmin = new PersonAdmin();

    /**
     * Business object person instance (used to parse the business object part
     * of a person).
     */
    private final PersonBus personBus = new PersonBus();

    /**
     * Holds all group assignments of this person.
     */
    private final Set<String> groups = new TreeSet<String>();

    /**
     * Holds all role assignments of this person.
     */
    private final Set<String> roles = new TreeSet<String>();

    /**
     * The matching names are evaluated with the help of the business
     * administration person objects.
     *
     * @param _contex       context for this request
     * @param _matched      collection of match strings for which the persons
     *                      are searched
     * @return set of matching person names
     * @see #personAdmin
     */
    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        return this.personAdmin.getMatchingNames(_context, _matches);
    }

    @Override
    public void export(final Context _context,
                       final File _path,
                       final String _name)
            throws MatrixException, SAXException, IOException
    {
        this.personAdmin.parse(_context, _name);
        this.personBus.parse(_context, _name);
        final File file = new File(_path, this.personAdmin.getFileName());
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        this.personAdmin.write(out);
        out.append('\n');
        this.personBus.write(out);
        this.personAdmin.writeEnd(out);
        out.flush();
        out.close();
    }


    @Override
    public void update(final Context _context,
                       final String _name,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        this.personBus.parse(_context, _name);
        this.personAdmin.update(_context, _name, _file, _newVersion);
    }

    @InfoAnno_mxJPO(adminType = AdminTypeDef.Person)
    private class PersonAdmin
            extends AbstractAdminObject_mxJPO
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = -8458972437612839433L;

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
         * Because the original method
         * {@link AbstractPropertyObject_mxJPO#parse(Context,String)} is
         * protected, but called from
         * {@link Person_mxJPO#export(Context, File, String)}, the original
         * method must be overwritten and only called. So the original method
         * could be used to parse the business administration part of the
         * person.
         *
         * @param _context  context for this request
         * @param _name     name of the person to parse
         */
        @Override
        protected void parse(final Context _context,
                             final String _name)
                throws MatrixException, SAXException, IOException
        {
            super.parse(_context, _name);
        }

        @Override
        protected String getFileName()
        {
            return super.getFileName();
        }

        @Override
        protected void write(final Writer _out)
                throws IOException
        {
            super.write(_out);
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
                Person_mxJPO.this.groups.add(_content);
            } else if ("/assignmentList/assignment/roleRef".equals(_url))  {
                Person_mxJPO.this.roles.add(_content);

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

        @Override
        protected String getExportMQL(final String _name)
        {
            return new StringBuilder()
                    .append("export person \"").append(_name).append("\" !mail !set xml")
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
            for (final String group : Person_mxJPO.this.groups)  {
                _out.append(" \\\n    assign group \"")
                    .append(convertTcl(group))
                    .append("\"");
            }
            for (final String role : Person_mxJPO.this.roles)  {
                _out.append(" \\\n    assign role \"")
                    .append(convertTcl(role))
                    .append("\"");
            }
        }

        /**
         *
         * @param _out
         * @throws IOException
         */
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
         * @param _context          context for this request
         * @param _preMQLCode       MQL statements which must be called before the
         *                          TCL code is executed
         * @param _postMQLCode      MQL statements which must be called after the
         *                          TCL code is executed
         * @param _tclCode          TCL code from the file used to update
         * @param _tclVariables     map of all TCL variables where the key is the
         *                          name and the value is value of the TCL variable
         *                          (the value is automatically converted to TCL
         *                          syntax!)
         */
        @Override
        protected void update(final Context _context,
                              final CharSequence _preMQLCode,
                              final CharSequence _postMQLCode,
                              final CharSequence _tclCode,
                              final Map<String,String> _tclVariables)
                throws Exception
        {
            final StringBuilder preMQLCode = new StringBuilder();

            // post update MQL statements
            final StringBuilder postMQLCode = new StringBuilder()
                    .append(_postMQLCode);

            final Map<String,String> tclVariables = new HashMap<String,String>();
            Person_mxJPO.this.personBus.prepareUpdate(_context, preMQLCode, postMQLCode, tclVariables);
            tclVariables.putAll(_tclVariables);

            // append other pre MQL code
            preMQLCode.append(_preMQLCode)
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
                setHistoryOff(_context);
                super.update(_context, preMQLCode, postMQLCode, _tclCode, tclVariables);
            } finally  {
                setHistoryOn(_context);
            }
        }
    }

    @InfoAnno_mxJPO(busType = BusTypeDef.Person)
    private class PersonBus
            extends AbstractBusObject_mxJPO
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = 5091746765762271832L;

        /**
         * Current state of the business object person.
         */
        private String status;

        /**
         * Person is employee of company.
         */
        private String employeeOf;

        /**
         * Person is member of company.
         */
        private String memberOf;

        /**
         * Person has member access of company.
         */
        private String memberAccess;

        /**
         * Set of all member roles.
         */
        private final Set<String> memberRoles = new TreeSet<String>();

        /**
         * Set of all companies for which the person is company representative.
         */
        private final Set<String> representativeOf = new TreeSet<String>();

        /**
         * Parsed the business object of the person.
         *
         * @param _context  context for this request
         * @param _name     name of the person which must be parsed
         */
        @Override
        protected void parse(final Context _context,
                             final String _name)
                throws MatrixException, SAXException, IOException
        {
            super.parse(_context, _name + "________" + "-");
            this.prepare(_context);

            // exists a business object?
            if (this.getBusName() != null)  {
                final BusinessObject bus = new BusinessObject(this.getBusType(),
                                                              this.getBusName(),
                                                              this.getBusRevision(),
                                                              this.getBusVault());

// TODO: data model check needed... only one company connected? etc.
                // read current state, employee of, member of information
                final StringList selects = new StringList(1);
                selects.addElement("current");
                selects.addElement("to[Employee].from.name");
                selects.addElement("to[Member|from.type==Company].from.name");
                selects.addElement("to[Member|from.type==Company].attribute[Project Access]");
                selects.addElement("to[Member|from.type==Company].attribute[Project Role]");
                selects.addElement("to[Organization Representative|from.type==Company].from.name");

                final BusinessObjectWithSelect s = bus.select(_context, selects);

                // current state
                this.status = s.getSelectData("current");

                // employee
                this.employeeOf = s.getSelectData("to[Employee].from.name");

                // member
                this.memberOf = s.getSelectData("to[Member].from.name");
                this.memberAccess = s.getSelectData("to[Member].attribute[Project Access]");
                for (final String role : s.getSelectData("to[Member].attribute[Project Role]").split("~"))  {
                    if ((role != null) && !"".equals(role))  {
                        this.memberRoles.add(role);
                    }
                }

                // company representatives of
                final StringList companies = s.getSelectDataList("to[Organization Representative].from.name");
                if (companies != null)  {
                    for (final Object company : companies)  {
                        this.representativeOf.add((String) company);
                    }
                }
            }
        }

        /**
         * Appends the part for the business object to the TCL update code.
         *
         * @param _out      appendable instance where the TCL update code for
         *                  the business object part must be written
         */
        @Override
        protected void write(final Writer _out)
                throws IOException
        {
            _out.append("mql mod bus \"${OBJECTID}\"")
                .append(" \\\n    description \"").append(convertTcl(this.getBusDescription())).append("\"");
            for (final Attribute attr : this.getAttrValuesSorted())  {
                _out.append(" \\\n    \"").append(convertTcl(attr.name))
                    .append("\" \"").append(convertTcl(attr.value)).append("\"");
              }
            // member of
            _out.append("\n# member of")
                .append("\nset lsCur [mql print bus \"${OBJECTID}\" select \"to\\[Member|from.type==Company\\].from.name\" dump \"\\n\"]")
                .append("\nforeach sCur ${lsCur}  {")
                .append("\n  puts \"    - remove as member from '${sCur}'\"")
                .append("\n  mql disconnect bus \"${OBJECTID}\" \\")
                .append("\n      relationship \"Member\" \\")
                .append("\n      from Company \"${sCur}\" -")
                .append("\n}");
            if (this.memberOf != null)  {
                _out.append("\nputs \"    - assign as member from '").append(convertTcl(this.memberOf)).append("'\"")
                    .append("\nmql connect bus \"${OBJECTID}\" \\")
                    .append("\n    relationship \"Member\" \\")
                    .append("\n    from Company \"").append(convertTcl(this.memberOf)).append("\" - \\")
                    .append("\n    \"Project Access\" \"").append(convertTcl(this.memberAccess)).append("\" \\")
                    .append("\n    \"Project Role\" \"").append(convertTcl(join('~', this.memberRoles, null))).append("\"");
            }
            // employees
            final List<String> employees = new ArrayList<String>();
            if (this.employeeOf != null)  {
                employees.add(this.employeeOf);
            }
            _out.append(writeCons("employee",
                                  employees,
                                  "Employee"))
            // organization representatives
                .append(writeCons("organization representative",
                                  this.representativeOf,
                                  "Organization Representative"));
            // state
            if ("Active".equals(this.status))  {
                _out.append("\nmql promote bus \"${OBJECTID}\"");
            } else if ("Create".equals(this.status))  {
                _out.append("\nmql demote bus \"${OBJECTID}\"");
            }
        }

        protected StringBuilder writeCons(final String _title,
                                          final Collection<String> _values,
                                          final String _relationship)
        {
            final StringBuilder ret = new StringBuilder()
                    .append("\n# ").append(_title)
                    .append("\nset lsCur [mql print bus \"${OBJECTID}\" select \"to\\[").append(_relationship).append("\\].from.name\" dump \"\\n\"]")
                    .append("\nset lsNew [list");
            for (final String repr : _values)  {
                ret.append(" \"").append(convertTcl(repr)).append('\"');
            }
            ret.append("]")
               .append("\nforeach sCur ${lsCur}  {")
               .append("\n  if {[lsearch ${lsNew} \"${sCur}\"] < 0}  {")
               .append("\n    puts \"    - remove as ").append(_title).append(" from '${sCur}'\"")
               .append("\n    mql disconnect bus \"${OBJECTID}\" \\")
               .append("\n        relationship \"").append(_relationship).append("\" \\")
               .append("\n        from Company \"${sCur}\" -")
               .append("\n  }")
               .append("\n}")
               .append("\nforeach sNew ${lsNew}  {")
               .append("\n  if {[lsearch ${lsCur} \"${sNew}\"] < 0}  {")
               .append("\n    puts \"    - assign as ").append(_title).append(" from '${sNew}'\"")
               .append("\n    mql connect bus \"${OBJECTID}\" \\")
               .append("\n        relationship \"").append(_relationship).append("\" \\")
               .append("\n        from Company \"${sNew}\" -")
               .append("\n  }")
               .append("\n}");

            return ret;
        }

        protected void prepareUpdate(final Context _context,
                                     final StringBuilder _preMQLCode,
                                     final StringBuilder _postMQLCode,
                                     final Map<String,String> _tclVariables)
                throws MatrixException
        {
            // found the business object
            final BusinessObject bus = new BusinessObject(this.getBusType(),
                                                          this.getBusName(),
                                                          this.getBusRevision(),
                                                          this.getBusVault());
            final String objectId = bus.getObjectId(_context);

            // state
            if ("Active".equals(this.status))  {
                _preMQLCode.append("demote bus ").append(objectId).append(";\n");
            } else if ("Create".equals(this.status))  {
                _preMQLCode.append("promote bus ").append(objectId).append(";\n");
            }

            // prepare map of all TCL variables incl. id of business object
            _tclVariables.put("OBJECTID", objectId);
        }
    }
}

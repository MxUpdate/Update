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

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.AbstractPropertyObject_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.xml.sax.SAXException;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.update.util.StringUtil_mxJPO.join;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOff;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOn;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 */
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
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public Person_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * The matching names are evaluated with the help of the business
     * person objects. From the return values of the query for all business
     * person objects, only the business object name is returned in the set
     * (because the revision of the person business object is always a
     * &quot;-&quot;).
     *
     * @param _contex       context for this request
     * @param _matched      collection of match strings for which the persons
     *                      are searched
     * @return set of matching person names
     * @see #personBus
     */
    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final Set<String> persons = this.personBus.getMatchingNames(_context, _matches);
        final Set<String> ret = new TreeSet<String>();
        for (final String busName : persons)  {
            ret.add(busName.split(BusObject_mxJPO.SPLIT_NAME)[0]);
        }
        return ret;
    }

    @Override
    public void export(final ParameterCache_mxJPO _paramCache,
                       final File _path,
                       final String _name)
            throws MatrixException, SAXException, IOException
    {
        this.personAdmin.parse(_paramCache, _name);
        this.personBus.parse(_paramCache, _name);
        final File file = new File(_path, this.personAdmin.getFileName());
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        this.personAdmin.write(_paramCache, out);
        out.append('\n');
        this.personBus.write(_paramCache, out);
        this.personAdmin.writeEnd(out);
        out.flush();
        out.close();
    }

    @Override
    protected void parse(final ParameterCache_mxJPO _paramCache,
                         final String _name)
            throws MatrixException, SAXException, IOException
    {
        // TODO Auto-generated method stub

    }

    /**
     * Deletes the person business object and person administration object.
     *
     * @param _context      context for this request
     * @param _name         name of person to delete
     * @throws Exception if delete of person failed
     */
    @Override
    public void delete(final Context _context,
                       final String _name)
            throws Exception
    {
        this.personBus.delete(_context,
                              new StringBuilder().append(_name).append(BusObject_mxJPO.SPLIT_NAME)
                                      .append('-').toString());
        this.personAdmin.delete(_context, _name);
    }

    /**
     * Creates the person administration object and person business object.
     *
     * @param _context      context for this request
     * @param _file         TCL update file
     * @param _name         name of person to create
     * @throws Exception if create of person failed
     */
    @Override
    public void create(final Context _context,
                       final File _file,
                       final String _name)
            throws Exception
    {
        this.personAdmin.create(_context, _file, _name);
        this.personBus.create(_context,
                              _file,
                              new StringBuilder().append(_name).append(BusObject_mxJPO.SPLIT_NAME)
                                      .append('-').toString());
    }

    @Override
    public void update(final ParameterCache_mxJPO _paramCache,
                       final String _name,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        this.personBus.parse(_paramCache, _name);
        this.personAdmin.update(_paramCache, _name, _file, _newVersion);
    }

    protected void write(final ParameterCache_mxJPO _paramCache, final Writer _out) throws IOException,
            MatrixException {
    }

    class PersonAdmin
            extends PersonAdmin_mxJPO
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = -3816908902276144444L;

        private PersonAdmin()
        {
            super(Person_mxJPO.this.getTypeDef());
        }

        /**
         * Because the original method
         * {@link AbstractPropertyObject_mxJPO#parse(Context,String)} is
         * protected, but called from
         * {@link Person_mxJPO#export(Context, File, String)}, the original
         * method must be overwritten and only called. So the original method
         * could be used to parse the business administration part of the
         * person.
         *
         * @param _paramCache   parameter cache
         * @param _name         name of the person to parse
         */
        @Override
        protected void parse(final ParameterCache_mxJPO _paramCache,
                             final String _name)
                throws MatrixException, SAXException, IOException
        {
            super.parse(_paramCache, _name);
        }

        /**
         * Because the original method
         * {@link AbstractPropertyObject_mxJPO#getFileName()} is
         * protected, but called from
         * {@link Person_mxJPO#export(Context, File, String)}, the original
         * method must be overwritten and only called.
         *
         * @return file name
         */
        @Override
        protected String getFileName()
        {
            return super.getFileName();
        }

        /**
         * Because the original method
         * {@link AbstractAdminObject_mxJPO#write(ParameterCache_mxJPO,Writer)}
         * is protected, but called from
         * {@link Person_mxJPO#export(Context, File, String)}, the
         * original method must be overwritten and only called.
         *
         * @param _paramCache   parameter cache
         * @param _out          writer instance
         */
        @Override
        protected void write(final ParameterCache_mxJPO _paramCache,
                             final Writer _out)
                throws IOException
        {
            super.write(_paramCache, _out);
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
            final StringBuilder preMQLCode = new StringBuilder();

            // post update MQL statements
            final StringBuilder postMQLCode = new StringBuilder()
                    .append(_postMQLCode);

            final Map<String,String> tclVariables = new HashMap<String,String>();
            Person_mxJPO.this.personBus.prepareUpdate(_paramCache.getContext(), preMQLCode, postMQLCode, tclVariables);
            tclVariables.putAll(_tclVariables);

            // update must be done with history off (because not required...)
            try  {
                setHistoryOff(_paramCache.getContext());
                super.update(_paramCache, preMQLCode, postMQLCode, _preTCLCode, tclVariables, _sourceFile);
            } finally  {
                setHistoryOn(_paramCache.getContext());
            }
        }
    }

    class PersonBus
            extends BusObject_mxJPO
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
         * Constructor used to initialize the type definition enumeration.
         */
        private PersonBus()
        {
            super(Person_mxJPO.this.getTypeDef());
        }

        /**
         * Parsed the business object of the person.
         *
         * @param _paramCache   parameter cache
         * @param _name         name of the person which must be parsed
         */
        @Override
        protected void parse(final ParameterCache_mxJPO _paramCache,
                             final String _name)
                throws MatrixException
        {
            super.parse(_paramCache,
                        new StringBuilder().append(_name).append(BusObject_mxJPO.SPLIT_NAME).append('-').toString());

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

                final BusinessObjectWithSelect s = bus.select(_paramCache.getContext(), selects);

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
         * @param _paramCache   parameter cache
         * @param _out          appendable instance where the TCL update code
         *                      for the business object part must be written
         */
        @Override
        protected void write(final ParameterCache_mxJPO _paramCache,
                             final Writer _out)
                throws IOException
        {
            _out.append("mql mod bus \"${OBJECTID}\"")
                .append(" \\\n    description \"").append(convertTcl(this.getBusDescription())).append("\"");
            for (final AttributeValue attr : this.getAttrValuesSorted())  {
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

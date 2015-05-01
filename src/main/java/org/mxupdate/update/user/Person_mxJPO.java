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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.Context;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.xml.sax.SAXException;

/**
 *
 * @author The MxUpdate Team
 */
public class Person_mxJPO
    extends AbstractObject_mxJPO
{
    /**
     * TCL procedure to update the state of a person business object.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_SET_STATE
            = "proc setState {_newState}  {\n"
                + "global OBJECTID\n"
                + "set sCurrent [mql print bus ${OBJECTID} select current dump]\n"
                + "if {\"${_newState}\" != \"${sCurrent}\"}  {\n"
                    + "set lsStates [split [mql print bus ${OBJECTID} select policy.state dump '\\n'] '\\n']\n"
                    + "if {[lsearch ${lsStates} \"${sCurrent}\"] < [lsearch ${lsStates} \"${_newState}\"]}  {\n"
                        + "mql promote bus ${OBJECTID}\n"
                        + "logTrace \"    - activate business object\"\n"
                    + "} else  {\n"
                        + "mql demote bus ${OBJECTID}\n"
                        + "logTrace \"    - deactivate business object\"\n"
                    + "}\n"
                + "}\n"
            + "}\n";

    /**
     * Dummy procedure with logging information that the state update is
     * ignored.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_SET_STATE_DUMMY
            = "proc setState {_newState}  {\n"
                + "logDebug \"    - ignoring update of state '${_newState}'\""
            + "}\n";

    /**
     * Name of the TCL variable holding the name of the relationship
     * 'Employee'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_EMPLOYEE_OF
     */
    private static final String TCL_VAR_EMPLOYEE_RELATION = "_ATTR_EMPLOYEE_RELATION";

    /**
     * Name of the TCL variable holding the allowed organizational types of the
     * relationship 'Employee'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_EMPLOYEE_OF
     */
    private static final String TCL_VAR_EMPLOYEE_TYPES = "_ATTR_EMPLOYEE_TYPES";

    /**
     * Procedure which handles the update of the employee connection.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #TCL_VAR_EMPLOYEE_RELATION
     * @see #TCL_VAR_EMPLOYEE_TYPES
     */
    private static final String TCL_SET_EMPLOYEE_OF
            = "proc setEmployeeOf {args}  {\n"
                + "global " + Person_mxJPO.TCL_VAR_EMPLOYEE_RELATION + "\n"
                + "global " + Person_mxJPO.TCL_VAR_EMPLOYEE_TYPES + "\n"
                + "_updateConnection \"employee\" \"$" + Person_mxJPO.TCL_VAR_EMPLOYEE_RELATION
                        + "\" \"$" + Person_mxJPO.TCL_VAR_EMPLOYEE_TYPES
                        + "\" ${args} [list]\n"
            + "}\n";

    /**
     * Dummy procedure with logging information that the employee update
     * is ignored.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_SET_EMPLOYEE_OF_DUMMY
            = "proc setEmployeeOf {args}  {\n"
                + "logDebug \"    - ignoring employee update\""
            + "}\n";

    /**
     * Name of the TCL variable holding the name of the relationship
     * 'Organization Representative'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_REPRESENTATIVE_OF
     */
    private static final String TCL_VAR_REPRESENTATIVE_RELATION = "_ATTR_REPRESENTATIVE_RELATION";

    /**
     * Name of the TCL variable holding the allowed organizational types of the
     * relationship 'Organization Representative'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_REPRESENTATIVE_OF
     */
    private static final String TCL_VAR_REPRESENTATIVE_TYPES = "_ATTR_REPRESENTATIVE_TYPES";

    /**
     * Procedure which handles the update of the representative connection.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #TCL_VAR_REPRESENTATIVE_RELATION
     * @see #TCL_VAR_REPRESENTATIVE_TYPES
     */
    private static final String TCL_SET_REPRESENTATIVE_OF
            = "proc setRepresentativeOf {args}  {\n"
                + "global " + Person_mxJPO.TCL_VAR_REPRESENTATIVE_RELATION + "\n"
                + "global " + Person_mxJPO.TCL_VAR_REPRESENTATIVE_TYPES + "\n"
                + "_updateConnection \"representative\" \"$" + Person_mxJPO.TCL_VAR_REPRESENTATIVE_RELATION
                        + "\" \"$" + Person_mxJPO.TCL_VAR_REPRESENTATIVE_TYPES
                        + "\" ${args} [list]\n"
            + "}\n";

    /**
     * Dummy procedure with logging information that the representative update
     * is ignored.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_SET_REPRESENTATIVE_OF_DUMMY
            = "proc setRepresentativeOf {args}  {\n"
                + "logDebug \"    - ignoring representative update\""
            + "}\n";

    /**
     * Name of the TCL variable holding the name of the attribute
     * 'Project Access'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String TCL_VAR_MEMBER_ATTR_PROJECT_ACCESS = "_ATTR_MEMBER_ATTR_PROJECT_ACCESS";

    /**
     * Name of the TCL variable holding the name of the attribute
     * 'Project Role'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String TCL_VAR_MEMBER_ATTR_PROJECT_ROLE = "_ATTR_MEMBER_ATTR_PROJECT_ROLE";

    /**
     * Name of the TCL variable holding the name of the relationship
     * 'Member'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String TCL_VAR_MEMBER_RELATION = "_ATTR_MEMBER_RELATION";

    /**
     * Name of the TCL variable holding the allowed organizational types of the
     * relationship 'Member'.
     *
     * @see PersonBus#prepareUpdate(ParameterCache_mxJPO, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String TCL_VAR_MEMBER_TYPES = "_ATTR_MEMBER_TYPES";

    /**
     * Procedure which handles the update of the member connection.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #TCL_VAR_MEMBER_ATTR_PROJECT_ACCESS
     * @see #TCL_VAR_MEMBER_ATTR_PROJECT_ROLE
     * @see #TCL_VAR_MEMBER_RELATION
     * @see #TCL_VAR_MEMBER_TYPES
     */
    private static final String TCL_SET_MEMBER_OF
            = "proc setMemberOf {args}  {\n"
                + "global " + Person_mxJPO.TCL_VAR_MEMBER_ATTR_PROJECT_ACCESS + "\n"
                + "global " + Person_mxJPO.TCL_VAR_MEMBER_ATTR_PROJECT_ROLE + "\n"
                + "global " + Person_mxJPO.TCL_VAR_MEMBER_RELATION + "\n"
                + "global " + Person_mxJPO.TCL_VAR_MEMBER_TYPES + "\n"
                + "_updateConnection \"member\" \"$" + Person_mxJPO.TCL_VAR_MEMBER_RELATION
                        + "\" \"$" + Person_mxJPO.TCL_VAR_MEMBER_TYPES
                        + "\" ${args} [list \"$" + Person_mxJPO.TCL_VAR_MEMBER_ATTR_PROJECT_ACCESS
                        + "\" \"$" + Person_mxJPO.TCL_VAR_MEMBER_ATTR_PROJECT_ROLE + "\"]\n"
            + "}\n";

    /**
     * Dummy procedure with logging information that the member update
     * is ignored.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_SET_MEMBER_OF_DUMMY
            = "proc setMemberOf {args}  {\n"
                + "logDebug \"    - ignoring member update\""
            + "}\n";

    /**
     * TCL procedure to update connection.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TCL_UPDATE_CONNECTIONS
            = "proc _updateConnection {_sLog _sRelation _sTypes _llsNews _lsAttrs} {\n"
                + "global OBJECTID\n"
                + "set llsNew [list]\n"
                + "foreach lsOne ${_llsNews}  {\n"
                    + "set lsLine [list [lindex ${lsOne} 0] [lindex ${lsOne} 1] [lindex ${lsOne} 2]]\n"
                    + "foreach sOneAttr ${_lsAttrs}  {\n"
                        + "set iIdx [lsearch ${lsOne} ${sOneAttr}]\n"
                        + "if {${iIdx} >=0}  {\n"
                            + "lappend lsLine [lindex ${lsOne} [expr ${iIdx} + 1]]\n"
                        + "} else  {\n"
                            + "lappend lsLine \"\"\n"
                        + "}\n"
                    + "}\n"
                    + "lappend llsNew ${lsLine}\n"
                + "}\n"
                + "set llsCmd [list mql expand bus ${OBJECTID} "
                        + "to "
                        + "relationship \"${_sRelation}\" "
                        + "type \"${_sTypes}\" "
                        + "select bus "
                        + "select rel id]\n"
                + "foreach sOneAttr ${_lsAttrs}  {\n"
                    + "lappend llsCmd \"attribute\\[${sOneAttr}\\]\"\n"
                + "}\n"
                + "lappend llsCmd dump '' tcl\n"
                + "set llsExpand [eval ${llsCmd}]\n"
                + "set llsCur [list]\n"
                // disconnect obsolete
                + "foreach lsOne ${llsExpand}  {\n"
                    + "set lsLine [list [lindex ${lsOne} 3] [lindex ${lsOne} 4] [lindex ${lsOne} 5]]\n"
                    + "set iIdx 7\n"
                    + "foreach sOneAttr ${_lsAttrs}  {\n"
                        + "lappend lsLine [lindex [lindex ${lsOne} ${iIdx}] 0]\n"
                        + "incr iIdx\n"
                    + "}\n"
                    + "if {[lsearch ${llsNew} ${lsLine}] < 0}  {\n"
                        + "logTrace \"    - remove as ${_sLog} from [string tolower [lindex ${lsOne} 3]] '[lindex ${lsOne} 4]'\"\n"
                        + "mql disconnect connection [lindex [lindex ${lsOne} 6] 0]\n"
                    + "}\n"
                    + "lappend llsCur ${lsLine}\n"
                + "}\n"
                // connect new ones
                + "foreach lsOne ${llsNew}  {\n"
                    + "set lsLine [list]\n"
                    + "foreach sOne ${lsOne}  {\n"
                        + "lappend lsLine ${sOne}\n"
                    + "}\n"
                    + "if {[lsearch ${llsCur} ${lsLine}] < 0}  {\n"
                        + "logTrace \"    - define as ${_sLog} of [string tolower [lindex ${lsOne} 0]] '[lindex ${lsOne} 1]'\"\n"
                        + "set lsCmd [list mql connect bus ${OBJECTID} "
                                + "relationship \"${_sRelation}\" "
                                + "from [lindex ${lsOne} 0] [lindex ${lsOne} 1] [lindex ${lsOne} 2]]\n"
                        + "set iIdx 3\n"
                        + "foreach sOneAttr ${_lsAttrs}  {\n"
                            + "lappend lsCmd ${sOneAttr} [lindex ${lsOne} ${iIdx}] \n"
                            + "incr iIdx\n"
                        + "}\n"
                        + "eval ${lsCmd}\n"
                    + "}\n"
                + "}\n"
            + "}\n";

    /**
     * Defines the parameter for the match of persons for which states are not
     * handled (neither exported nor updated).
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see PersonBus#write(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_IGNORE_STATE = "UserPersonIgnoreState";

    /**
     * Defines the parameter that employee connections are not handled.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#write(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_EMPLOYEE_IGNORE  = "UserPersonEmployeeIgnore";

    /**
     * Defines the parameter for the relationship name of 'Employee'.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_EMPLOYEE_RELATION  = "UserPersonEmployeeRelation";

    /**
     * Defines the parameter for the allowed organizational types for the
     * employee relationship.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_EMPLOYEE_TYPES  = "UserPersonEmployeeTypes";

    /**
     * Defines the parameter for the attribute name of 'Project Access'.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_MEMBER_ATTR_PROJECT_ACCESS  = "UserPersonMemberAttrProjAccess";

    /**
     * Defines the parameter for the attribute name of 'Project Role'.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_MEMBER_ATTR_PROJECT_ROLE  = "UserPersonMemberAttrProjRole";

    /**
     * Defines the parameter that member connections are not handled.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#write(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_MEMBER_IGNORE  = "UserPersonMemberIgnore";

    /**
     * Defines the parameter for the relationship name of 'Member'.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_MEMBER_RELATION  = "UserPersonMemberRelation";

    /**
     * Defines the parameter for the allowed organizational types for the
     * member relationship.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_MEMBER_TYPES  = "UserPersonMemberTypes";

    /**
     * Defines the parameter that representative connections are not handled.
     *
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#write(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_REPRESENTATIVE_IGNORE  = "UserPersonRepresentativeIgnore";

    /**
     * Defines the parameter for the relationship name of
     * 'Organization Representative'.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_REPRESENTATIVE_RELATION  = "UserPersonRepresentativeRelation";

    /**
     * Defines the parameter for the allowed organizational types for the
     * representative relationship.
     *
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonBus#prepareUpdate(Context, StringBuilder, StringBuilder, Map)
     * @see #TCL_SET_MEMBER_OF
     */
    private static final String PARAM_REPRESENTATIVE_TYPES  = "UserPersonRepresentativeTypes";

    /**
     * Administration person instance (used to parse the admin part of a
     * person).
     */
    private final PersonAdmin personAdmin;

    /**
     * Business object person instance (used to parse the business object part
     * of a person).
     */
    private final PersonBus personBus;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the person object
     */
    public Person_mxJPO(final TypeDef_mxJPO _typeDef,
                        final String _mxName)
    {
        super(_typeDef, _mxName);
        this.personAdmin = new PersonAdmin(_typeDef, _mxName);
        this.personBus = new PersonBus(_typeDef, _mxName);
    }

    /**
     * Symbolic names from the {@link #personAdmin} are returned.
     *
     * @return symbolic names from the administration part of the person
     */
    protected Set<String> getSymbolicNames()
    {
        return this.personAdmin.getSymbolicNames();
    }

    /**
     * The list of all person names are evaluated with the help of the business
     * person objects. From the return values of the query for all business
     * person objects, only the business object name is returned in the set
     * (because the revision of the person business object is always a
     * &quot;-&quot;).
     *
     * @param _paramCache   parameter cache
     * @return set of person names
     * @throws MatrixException if the query for persons failed
     * @see #personBus
     */
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final Set<String> persons = this.personBus.getMxNames(_paramCache);
        final Set<String> ret = new TreeSet<String>();
        for (final String busName : persons)  {
            ret.add(busName.split(BusObject_mxJPO.SPLIT_NAME)[0]);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>The properties are handled on the administration part of the person.
     * So the property values are evaluated from {@link #personAdmin}.</p>
     */
    @Override()
    public String getPropValue(final ParameterCache_mxJPO _paramCache,
                               final PropertyDef_mxJPO _prop)
        throws MatrixException
    {
        return this.personAdmin.getPropValue(_paramCache, _prop);
    }

    /**
     * If the person must be parsed, first the admin object of the person is
     * parsed and then the related person business object.
     *
     * @param _paramCache   parameter cache
     * @see #personAdmin
     * @see #personBus
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException
    {
        this.personAdmin.parse(_paramCache);
        this.personBus.parse(_paramCache);
    }

    /**
     * Deletes the person business object and person administration object.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if delete of person failed
     */
    @Override()
    public void delete(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        this.personBus.delete(_paramCache);
        this.personAdmin.delete(_paramCache);
    }

    /**
     * Creates the person administration object and person business object.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if create of person failed
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        this.personAdmin.create(_paramCache);
        this.personBus.create(_paramCache);
    }

    /**
     * Parses first the business object representation and then updates the
     * person using the administration representation.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _file         file used for update
     * @throws Exception if parse or update failed
     * @see PersonBus#parse(ParameterCache_mxJPO)
     * @see PersonAdmin#update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final boolean _create,
                       final File _file)
        throws Exception
    {
        this.personBus.parse(_paramCache);
        this.personAdmin.update(_paramCache, _create, _file);
    }

    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.personAdmin.write(_paramCache, _out);
        _out.append('\n');
        this.personBus.write(_paramCache, _out);
    }

    /**
     * Handles the administration part of the person. The class is used that
     * some methods could be called from this person class.
     */
    private final class PersonAdmin
        extends PersonAdmin_mxJPO
    {
        /**
         * Constructor used to initialize the administration object instance
         * for persons.
         *
         * @param _typeDef  related type definition for the person
         * @param _mxName   MX name of the person object
         */
        private PersonAdmin(final TypeDef_mxJPO _typeDef,
                            final String _mxName)
        {
            super(_typeDef, _mxName);
        }

        /**
         * Because the original method
         * {@link PersonAdmin_mxJPO#getSymbolicNames()}
         * is protected, but called from
         * {@link Person_mxJPO#getSymbolicNames()}, the original
         * method must be overwritten and only called.
         *
         * @return set of symbolic names for the administration part of the
         *         person
         */
        @Override()
        protected SortedSet<String> getSymbolicNames()
        {
            return super.getSymbolicNames();
        }

        /**
         * Because the original method
         * {@link PersonAdmin_mxJPO#parse(ParameterCache_mxJPO)}
         * is protected, but called from
         * {@link Person_mxJPO#parse(ParameterCache_mxJPO)}, the original
         * method must be overwritten and only called. So the original method
         * could be used to parse the business administration part of the
         * person.
         * {@inheritDoc}
         */
        @Override()
        protected void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException, SAXException, IOException
        {
            super.parse(_paramCache);
        }

        /**
         *
         * @param _paramCache   parameter cache
         * @param _out          writer instance
         * @throws IOException if the TCL update code for the person could not
         *                     be written
         */
        @Override()
        public void write(final ParameterCache_mxJPO _paramCache,
                          final Appendable _out)
            throws IOException
        {
            _out.append("mql mod ")
                .append(this.getTypeDef().getMxAdminName())
                .append(" \"${NAME}\"");
            if (!"".equals(this.getTypeDef().getMxAdminSuffix()))  {
                _out.append(" ").append(this.getTypeDef().getMxAdminSuffix());
            }
            _out.append(" \\\n    description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append("\"");
//            this.writeObject(_paramCache, _out);
            this.getProperties().writeAddFormat(_paramCache, _out, this.getTypeDef());
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
         * @throws Exception if the update from derived class failed
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
            final StringBuilder preTCLCode = new StringBuilder()
                    .append(_preTCLCode)
                    .append('\n')
                    .append(Person_mxJPO.TCL_UPDATE_CONNECTIONS);

            // append TCL set state if not ignored (otherwise dummy TCL proc)
            final Collection<String> matchStates = _paramCache.getValueList(Person_mxJPO.PARAM_IGNORE_STATE);
            if ((matchStates == null) || !StringUtil_mxJPO.match(this.getName(), matchStates))  {
                preTCLCode.append(Person_mxJPO.TCL_SET_STATE);
            } else  {
                preTCLCode.append(Person_mxJPO.TCL_SET_STATE_DUMMY);
            }

            // append TCL update representative if not ignored
            // (otherwise dummy TCL proc)
            final Collection<String> matchEmpl = _paramCache.getValueList(Person_mxJPO.PARAM_EMPLOYEE_IGNORE);
            if ((matchEmpl == null) || !StringUtil_mxJPO.match(this.getName(), matchEmpl))  {
                preTCLCode.append(Person_mxJPO.TCL_SET_EMPLOYEE_OF);
            } else  {
                preTCLCode.append(Person_mxJPO.TCL_SET_EMPLOYEE_OF_DUMMY);
            }

            // append TCL update representative if not ignored
            // (otherwise dummy TCL proc)
            final Collection<String> matchMemb = _paramCache.getValueList(Person_mxJPO.PARAM_MEMBER_IGNORE);
            if ((matchMemb == null) || !StringUtil_mxJPO.match(this.getName(), matchMemb))  {
                preTCLCode.append(Person_mxJPO.TCL_SET_MEMBER_OF);
            } else  {
                preTCLCode.append(Person_mxJPO.TCL_SET_MEMBER_OF_DUMMY);
            }

            // append TCL update representative if not ignored
            // (otherwise dummy TCL proc)
            final Collection<String> matchRepr = _paramCache.getValueList(Person_mxJPO.PARAM_REPRESENTATIVE_IGNORE);
            if ((matchRepr == null) || !StringUtil_mxJPO.match(this.getName(), matchRepr))  {
                preTCLCode.append(Person_mxJPO.TCL_SET_REPRESENTATIVE_OF);
            } else  {
                preTCLCode.append(Person_mxJPO.TCL_SET_REPRESENTATIVE_OF_DUMMY);
            }


            final StringBuilder preMQLCode = new StringBuilder();

            // post update MQL statements
            final StringBuilder postMQLCode = new StringBuilder()
                    .append(_postMQLCode);

            final Map<String,String> tclVariables = new HashMap<String,String>();
            Person_mxJPO.this.personBus.prepareUpdate(_paramCache, preMQLCode, postMQLCode, tclVariables);
            tclVariables.putAll(_tclVariables);

            // update must be done with history off (because not required...)
            try  {
                MqlUtil_mxJPO.setHistoryOff(_paramCache);
                super.update(_paramCache, preMQLCode, postMQLCode, preTCLCode, tclVariables, _sourceFile);
            } finally  {
                MqlUtil_mxJPO.setHistoryOn(_paramCache);
            }
        }
    }

    /**
     * Handles the business object part of a person. Class is also needed so
     * that protected methods could be called from this class.
     */
    private static final class PersonBus
        extends BusObject_mxJPO
    {
        /**
         * Current state of the business object person.
         */
        private String status;

        /**
         * Person is employee of organizational objects.
         *
         * @see #parse(ParameterCache_mxJPO)
         */
        private final Set<Person_mxJPO.ReferencedOrganization> employeeOf = new TreeSet<Person_mxJPO.ReferencedOrganization>();

        /**
         * Person is member of company.
         */
        private final Set<Person_mxJPO.ReferencedOrganization> memberOf = new TreeSet<Person_mxJPO.ReferencedOrganization>();

        /**
         * Set of all companies for which the person is company representative.
         *
         * @see #parse(ParameterCache_mxJPO)
         */
        private final Set<Person_mxJPO.ReferencedOrganization> representativeOf = new TreeSet<Person_mxJPO.ReferencedOrganization>();

        /**
         * Constructor used to initialize the business object instance for
         * persons.
         *
         * @param _typeDef  related type definition for the person
         * @param _mxName   MX name of the person object
         */
        private PersonBus(final TypeDef_mxJPO _typeDef,
                          final String _mxName)
        {
            super(_typeDef,
                 new StringBuilder().append(_mxName)
                                    .append(BusObject_mxJPO.SPLIT_NAME)
                                    .append('-').toString());
        }

        /**
         * Parsed the business object of the person.
         *
         * @param _paramCache   parameter cache
         */
        @Override()
        protected void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            super.parse(_paramCache);

            // exists a business object?
            if (this.getBusName() != null)  {
                final BusinessObject bus = new BusinessObject(this.getBusType(),
                                                              this.getBusName(),
                                                              this.getBusRevision(),
                                                              this.getBusVault());

                try  {
                    bus.open(_paramCache.getContext());

                    // read current state, employee of, member of information
                    final StringList selects = new StringList(1);
                    selects.addElement("current");

                    final BusinessObjectWithSelect s = bus.select(_paramCache.getContext(), selects);

                    // current state
                    this.status = s.getSelectData("current");

                    // employee
                    final Collection<String> matchEmpl = _paramCache.getValueList(Person_mxJPO.PARAM_EMPLOYEE_IGNORE);
                    if ((matchEmpl == null) || !StringUtil_mxJPO.match(this.getName(), matchEmpl))  {
                        this.parseConnection(_paramCache, bus, this.employeeOf,
                                _paramCache.getValueString(Person_mxJPO.PARAM_EMPLOYEE_RELATION),
                                StringUtil_mxJPO.join(',', false, _paramCache.getValueList(Person_mxJPO.PARAM_EMPLOYEE_TYPES), "*"));
                    }

                    // member
                    final Collection<String> matchMemb = _paramCache.getValueList(Person_mxJPO.PARAM_MEMBER_IGNORE);
                    if ((matchMemb == null) || !StringUtil_mxJPO.match(this.getName(), matchMemb))  {
                        this.parseConnection(_paramCache, bus, this.memberOf,
                                _paramCache.getValueString(Person_mxJPO.PARAM_MEMBER_RELATION),
                                StringUtil_mxJPO.join(',', false, _paramCache.getValueList(Person_mxJPO.PARAM_MEMBER_TYPES), "*"),
                                _paramCache.getValueString(Person_mxJPO.PARAM_MEMBER_ATTR_PROJECT_ACCESS),
                                _paramCache.getValueString(Person_mxJPO.PARAM_MEMBER_ATTR_PROJECT_ROLE));
                    }

                    // company representatives of
                    final Collection<String> matchRepr = _paramCache.getValueList(Person_mxJPO.PARAM_REPRESENTATIVE_IGNORE);
                    if ((matchRepr == null) || !StringUtil_mxJPO.match(this.getName(), matchRepr))  {
                        this.parseConnection(_paramCache, bus, this.representativeOf,
                                _paramCache.getValueString(Person_mxJPO.PARAM_REPRESENTATIVE_RELATION),
                                StringUtil_mxJPO.join(',', false, _paramCache.getValueList(Person_mxJPO.PARAM_REPRESENTATIVE_TYPES), "*"));
                    }
                } finally  {
                    bus.close(_paramCache.getContext());
                }
            }
        }

        protected void parseConnection(final ParameterCache_mxJPO _paramCache,
                                       final BusinessObject _bus,
                                       final Set<Person_mxJPO.ReferencedOrganization> _refOrgs,
                                       final String _relationship,
                                       final String _types,
                                       final String... _relAttrs)
            throws MatrixException
        {
            final StringList objectSelects = new StringList();
            objectSelects.addElement("type");
            objectSelects.addElement("name");
            objectSelects.addElement("revision");

            final List<String> relAttrSels = new ArrayList<String>();
            final StringList relationshipSelects = new StringList();
            if (_relAttrs != null)  {
                for (final String relAttr : _relAttrs)  {
                    final String relAttrSel = new StringBuilder().append("attribute[").append(relAttr).append("]").toString();
                    relAttrSels.add(relAttrSel);
                    relationshipSelects.addElement(relAttrSel);
                }
            }

            final RelationshipWithSelectList relSelList = _bus.expandSelect(
                            _paramCache.getContext(),
                            _relationship,
                            _types,
                            objectSelects,
                            relationshipSelects,
                            true,
                            false,
                            (short) 1,
                            "",
                            "",
                            (short) 0,
                            true,
                            false)
                    .getRelationships();

            for (final Object mapObj : relSelList)
            {
                final RelationshipWithSelect   relMap = (RelationshipWithSelect) mapObj;
                final BusinessObjectWithSelect busMap = relMap.getTarget();
                // get attribute values from connection
                final List<String> attrValues = new ArrayList<String>(relAttrSels.size());
                int idx = 0;
                for (final String relAttrSel : relAttrSels)  {
                    attrValues.add(_relAttrs[idx++]);
                    attrValues.add(relMap.getSelectData(relAttrSel));
                }

                _refOrgs.add(
                        new ReferencedOrganization(
                                busMap.getSelectData("type"),
                                busMap.getSelectData("name"),
                                busMap.getSelectData("revision"),
                                attrValues));
            }
        }

        /**
         * Appends the part for the business object to the TCL update code.
         *
         * @param _paramCache   parameter cache
         * @param _out          appendable instance where the TCL update code
         *                      for the business object part must be written
         * @throws IOException if the TCL update code could not written
         */
        @Override()
        public void write(final ParameterCache_mxJPO _paramCache,
                          final Appendable _out)
            throws IOException
        {
            _out.append("mql mod bus \"${OBJECTID}\"")
                .append(" \\\n    description \"").append(StringUtil_mxJPO.convertTcl(this.getBusDescription())).append("\"");
            for (final AttributeValue attr : this.getAttrValuesSorted())  {
                _out.append(" \\\n    \"").append(StringUtil_mxJPO.convertTcl(attr.name))
                    .append("\" \"").append(StringUtil_mxJPO.convertTcl(attr.value)).append("\"");
              }
            // employee of
            final Collection<String> matchEmpl = _paramCache.getValueList(Person_mxJPO.PARAM_EMPLOYEE_IGNORE);
            if ((matchEmpl == null) || !StringUtil_mxJPO.match(this.getName(), matchEmpl))  {
                _out.append("\nsetEmployeeOf");
                for (final ReferencedOrganization refOrg : this.employeeOf)  {
                    refOrg.write(_out);
                }
            }
            // member of
            final Collection<String> matchMemb = _paramCache.getValueList(Person_mxJPO.PARAM_MEMBER_IGNORE);
            if ((matchMemb == null) || !StringUtil_mxJPO.match(this.getName(), matchMemb))  {
                _out.append("\nsetMemberOf");
                for (final ReferencedOrganization refOrg : this.memberOf)  {
                    refOrg.write(_out);
                }
            }
            // representative of (if not ignored)
            final Collection<String> matchRepr = _paramCache.getValueList(Person_mxJPO.PARAM_REPRESENTATIVE_IGNORE);
            if ((matchRepr == null) || !StringUtil_mxJPO.match(this.getName(), matchRepr))  {
                _out.append("\nsetRepresentativeOf");
                for (final ReferencedOrganization refOrg : this.representativeOf)  {
                    refOrg.write(_out);
                }
            }
            // state (if not ignored)
            final Collection<String> matchStates = _paramCache.getValueList(Person_mxJPO.PARAM_IGNORE_STATE);
            if ((matchStates == null) || !StringUtil_mxJPO.match(this.getName(), matchStates))  {
                _out.append("\nsetState \"").append(this.status).append('\"');
            }
        }

        /**
         * Sets the TCL variable <code>OBJECTID</code> to current business
         * object.
         *
         * @param _paramCache       parameter cache
         * @param _preMQLCode       pre MQL code
         * @param _postMQLCode      post MQL code
         * @param _tclVariables     map with all TCL variables
         * @throws MatrixException if update failed
         */
        protected void prepareUpdate(final ParameterCache_mxJPO _paramCache,
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
            final String objectId = bus.getObjectId(_paramCache.getContext());

            // prepare map of all TCL variables incl. id of business object
            _tclVariables.put("OBJECTID", objectId);

            _tclVariables.put(
                    Person_mxJPO.TCL_VAR_EMPLOYEE_RELATION,
                    _paramCache.getValueString(Person_mxJPO.PARAM_EMPLOYEE_RELATION));
            _tclVariables.put(
                    Person_mxJPO.TCL_VAR_EMPLOYEE_TYPES,
                    StringUtil_mxJPO.join(',', false, _paramCache.getValueList(Person_mxJPO.PARAM_EMPLOYEE_TYPES), "*"));

            _tclVariables.put(Person_mxJPO.TCL_VAR_MEMBER_ATTR_PROJECT_ACCESS,  _paramCache.getValueString(Person_mxJPO.PARAM_MEMBER_ATTR_PROJECT_ACCESS));
            _tclVariables.put(Person_mxJPO.TCL_VAR_MEMBER_ATTR_PROJECT_ROLE,    _paramCache.getValueString(Person_mxJPO.PARAM_MEMBER_ATTR_PROJECT_ROLE));
            _tclVariables.put(Person_mxJPO.TCL_VAR_MEMBER_RELATION,             _paramCache.getValueString(Person_mxJPO.PARAM_MEMBER_RELATION));
            _tclVariables.put(
                    Person_mxJPO.TCL_VAR_MEMBER_TYPES,
                    StringUtil_mxJPO.join(',', false, _paramCache.getValueList(Person_mxJPO.PARAM_MEMBER_TYPES), "*"));

            _tclVariables.put(
                    Person_mxJPO.TCL_VAR_REPRESENTATIVE_RELATION,
                    _paramCache.getValueString(Person_mxJPO.PARAM_REPRESENTATIVE_RELATION));
            _tclVariables.put(
                    Person_mxJPO.TCL_VAR_REPRESENTATIVE_TYPES,
                    StringUtil_mxJPO.join(',', false, _paramCache.getValueList(Person_mxJPO.PARAM_REPRESENTATIVE_TYPES), "*"));
        }
    }

    /**
     * Class to hold the information about referenced organization objects.
     */
    private static final class ReferencedOrganization
        implements Comparable<Person_mxJPO.ReferencedOrganization>
    {
        /** Type of the referenced organization. */
        private final String type;

        /** Name of the referenced organization. */
        private final String name;

        /** Revision of the referenced organization. */
        private final String revision;

        /**
         * All attribute values on the connection to the referenced
         * organization.
         */
        private final List<String> attrValues;

        /**
         *
         * @param _type         type of referenced organization
         * @param _name         name of referenced organization
         * @param _revision     revision of referenced organization
         * @param _attrValues   attribute values
         */
        public ReferencedOrganization(final String _type,
                                      final String _name,
                                      final String _revision,
                                      final List<String> _attrValues)
        {
            this.type = _type;
            this.name = _name;
            this.revision = _revision;
            this.attrValues = _attrValues;
        }

        /**
         * Appends the information about this referenced organization to
         * <code>_out</code>.
         *
         * @param _out      appendable instance
         * @throws IOException if append failed
         */
        protected void write(final Appendable _out)
            throws IOException
        {
            _out.append(" \\\n    {\"").append(StringUtil_mxJPO.convertTcl(this.type))
                .append("\" \"").append(StringUtil_mxJPO.convertTcl(this.name))
                .append("\" \"").append(StringUtil_mxJPO.convertTcl(this.revision));
            for (final String attrValue : this.attrValues)  {
                _out.append("\" \"").append(StringUtil_mxJPO.convertTcl(attrValue));
            }
            _out.append("\"}");
        }

        /**
         * {@inheritDoc}
         */
        @Override()
        public int compareTo(final ReferencedOrganization _other)
        {
            int ret = this.type.compareTo(_other.type);
            if (ret == 0)  {
                ret = this.name.compareTo(_other.name);
            }
            if (ret == 0)  {
                ret = this.revision.compareTo(_other.revision);
            }
            return ret;
        }
    }
}

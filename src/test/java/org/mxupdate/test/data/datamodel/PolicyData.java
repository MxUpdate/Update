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

package org.mxupdate.test.data.datamodel;

import java.util.ArrayList;
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
import org.mxupdate.test.ExportParser.Line;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * Used to define a policy, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class PolicyData
    extends AbstractAdminData<PolicyData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
        PolicyData.REQUIRED_EXPORT_VALUES.put("description", "");
        PolicyData.REQUIRED_EXPORT_VALUES.put("defaultformat", "");
        PolicyData.REQUIRED_EXPORT_VALUES.put("store", "");
        PolicyData.REQUIRED_EXPORT_VALUES.put("hidden", "false");
    }

    /** Are all types assigned? */
    private boolean allTypes = false;
    /** All defined types for this policy. */
    private final Set<TypeData> types = new HashSet<TypeData>();

    /** Are all formats assigned? */
    private boolean allFormats = false;
    /** All defined formats for this policy. */
    private final Set<FormatData> formats = new HashSet<FormatData>();

    /** Access for all states. */
    private AllState allState;

    /** All states for this policy. */
    private final List<State> states = new ArrayList<State>();

    /**
     * Initialize this policy data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this policy is
     *                  defined)
     * @param _name     name of the policy
     */
    public PolicyData(final AbstractTest _test,
                      final String _name)
    {
        super(_test, AbstractTest.CI.DM_POLICY, _name, PolicyData.REQUIRED_EXPORT_VALUES, null);
    }

    /**
     * Defines if all types are allowed for this policy.
     *
     * @param _allTypes     new value
     * @return this policy data instance
     * @see #allTypes
     */
    public PolicyData setAllTypes(final boolean _allTypes)
    {
        this.allTypes = _allTypes;
        return this;
    }

    /**
     * Appends given <code>_types</code> to the list of all {@link #types}.
     *
     * @param _types    type instances to append
     * @return this instance
     * @see #types
     */
    public PolicyData appendTypes(final TypeData... _types)
    {
        this.types.addAll(Arrays.asList(_types));
        return this;
    }

    /**
     * Returns all {@link #types} of this policy.
     *
     * @return all assigned types
     * @see #types
     */
    public Set<TypeData> getTypes()
    {
        return this.types;
    }

    /**
     * Defines if all types are allowed for this policy.
     *
     * @param _allFormats     new value
     * @return this policy data instance
     * @see #allFormats
     */
    public PolicyData setAllFormats(final boolean _allFormats)
    {
        this.allFormats = _allFormats;
        return this;
    }

    /**
     * Appends given <code>_formats</code> to the list of all {@link #formats}.
     *
     * @param _formats    format instances to append
     * @return this instance
     * @see #formats
     */
    public PolicyData appendFormats(final FormatData... _formats)
    {
        this.formats.addAll(Arrays.asList(_formats));
        return this;
    }

    /**
     * Returns all {@link #formats} of this policy.
     *
     * @return all assigned formats
     * @see #formats
     */
    public Set<FormatData> getFormats()
    {
        return this.formats;
    }

    /**
     * Defines whether {@link #allState}.
     *
     * @param _allState     new value
     * @return this policy data instance
     */
    public PolicyData setAllState(final AllState _allState)
    {
        this.allState = _allState;
        return this;
    }

    /**
     * Appends {@code _state} to this policy.
     *
     * @param _state        state to append
     * @return this policy instance
     * @see #states
     */
    public PolicyData addState(final State _state)
    {
        this.states.add(_state);
        return this;
    }

    /**
     * Returns all defined {@link #states} of this policy.
     *
     * @return defined states
     */
    public List<State> getStates()
    {
        return this.states;
    }

    /**
     * Prepares and returns the string of the CI file.
     *
     * @return string of the CI file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);

        strg.append("updatePolicy \"${NAME}\" {\n")
            .append("  hidden \"").append(this.getFlags().get("hidden") != null ? this.getFlags().get("hidden") : false).append("\"\n");

        // append values
        this.getValues().append4CIFileValues("  ", strg, "\n");

        // type definition
        if (this.allTypes)  {
            strg.append(" type all\n");
        } else if (!this.types.isEmpty()) {
            final List<String> typeNames = new ArrayList<String>();
            for (final TypeData type : this.types)  {
                typeNames.add(type.getName());
            }
            strg.append(" type {").append(StringUtil_mxJPO.joinMql(' ', true, typeNames, null)).append("}\n");
        }

        // format definition
        if (this.allFormats)  {
            strg.append(" format all\n");
        } else if (!this.formats.isEmpty()) {
            final List<String> formatNames = new ArrayList<String>();
            for (final FormatData format : this.formats)  {
                formatNames.add(format.getName());
            }
            strg.append(" format {").append(StringUtil_mxJPO.joinMql(' ', true, formatNames, null)).append("}\n");
        }

        // enforce
        if (this.getFlags().get("enforce") != null)  {
            strg.append("  enforce \"").append(this.getFlags().get("enforce")).append("\"\n");
        }

        if (this.allState != null)  {
            this.allState.append4CIFile(strg);
        }

        // append state information
        for (final PolicyData.State state : this.states)
        {
            state.append4CIFile(strg);
        }

        strg.append("}");

        // append properties
        for (final PropertyDef prop : this.getProperties())  {
            strg.append('\n').append(prop.getCITCLString(this.getCI()));
        }

        return strg.toString();
    }

    /**
     * Create the related policy in MX for this policy data instance.
     *
     * @return this policy data instance
     * @throws MatrixException if create failed
     */
    @Override()
    public PolicyData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add policy \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            // assign types
            if (this.allTypes)  {
                cmd.append(" type all ");
            } else if (!this.types.isEmpty()) {
                final List<String> typeNames = new ArrayList<String>();
                for (final TypeData type : this.types)  {
                    typeNames.add(type.getName());
                    type.create();
                }
                cmd.append(" type ").append(StringUtil_mxJPO.joinMql(',', true, typeNames, null));
            }

            // assign formats / enforce flag
            if (this.allFormats)  {
                cmd.append(" format all ");
            } else if (!this.formats.isEmpty()) {
                final List<String> formatNames = new ArrayList<String>();
                for (final FormatData format : this.formats)  {
                    formatNames.add(format.getName());
                    format.create();
                }
                cmd.append(" format ").append(StringUtil_mxJPO.joinMql(',', true, formatNames, null));
            }
            if (this.getFlags().get("enforce") != null)  {
                cmd.append(' ');
                if (!this.getFlags().get("enforce"))  {
                    cmd.append("not");
                }
                cmd.append("enforce");
            }

            if (this.allState != null)  {
                this.allState.append4Create(cmd);
            }

            this.append4Create(cmd);

            // append state information
            for (final PolicyData.State state : this.states)
            {
                state.append4Create(cmd);
            }

            cmd.append(";\n")
                .append("escape add property ").append(this.getSymbolicName())
                .append(" on program eServiceSchemaVariableMapping.tcl")
                .append(" to policy \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * Creates assigned {@link #types}, {@link #formats} and all assigned users
     * of {@link #allStateAccess}.
     *
     * @see #formats
     * @see #types
     * @see #allStateAccess
     */
    @Override()
    public PolicyData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create assigned types
        for (final TypeData type : this.types)  {
            type.create();
        }
        // create assigned formats
        for (final FormatData format : this.formats)  {
            format.create();
        }
        // create assigned users
        if (this.allState != null)  {
            for (final Access access : this.allState.access)  {
                access.createDependings();
            }
        }
        for (final State state : this.states)  {
            for (final Access access : state.access)  {
                access.createDependings();
            }
        }

        return this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        this.getProperties().checkExportPropertiesAddFormat(_exportParser, this.getCI());

        // check symbolic name
        Assert.assertEquals(
                _exportParser.getSymbolicName(),
                this.getSymbolicName(),
                "check symbolic name");

        // check for all required values
        for (final String valueName : PolicyData.REQUIRED_EXPORT_VALUES.keySet())  {
            Assert.assertEquals(_exportParser.getLines("/updatePolicy/" + valueName + "/@value").size(),
                                1,
                                "required check that minimum and maximum one " + valueName + " is defined");
        }

        // check for defined values
        this.getValues().checkExport(_exportParser);

        // check for hidden flag
        if ((this.getFlags().get("hidden") == null) || !this.getFlags().get("hidden"))  {
            this.checkSingleValue(_exportParser,
                                  "hidden flag (must be false)",
                                  "hidden",
                                   "\"false\"");
        } else  {
            this.checkSingleValue(_exportParser,
                                  "hidden flag (must be true)",
                                  "hidden",
                                  "\"true\"");
        }

        // check for types
        if (this.allTypes)  {
            this.checkSingleValue(_exportParser, "all types", "type", "all");
        } else if (!this.types.isEmpty()) {
            final Set<String> typeNames = new TreeSet<String>();
            for (final TypeData type : this.types)  {
                typeNames.add(type.getName());
            }
            this.checkSingleValue(_exportParser, "types", "type", "{" + StringUtil_mxJPO.joinMql(' ', true, typeNames, null) + "}");
        } else  {
            this.checkSingleValue(_exportParser, "types", "type", "{}");
        }

        // check for enforce flag
        if (this.getFlags().get("enforce") == null)  {
            this.checkSingleValue(
                    _exportParser,
                    "enforce flag must be not defined",
                    "enforce",
                    null);
        } else  {
            this.checkSingleValue(
                    _exportParser,
                    "enforce flag",
                    "enforce",
                    "\""+ this.getFlags().get("enforce") + "\"");
        }

        // check for formats
        if (this.allFormats)  {
            this.checkSingleValue(_exportParser, "all formats", "format", "all");
        } else if (!this.formats.isEmpty()) {
            final Set<String> formatNames = new TreeSet<String>();
            for (final FormatData format : this.formats)  {
                formatNames.add(format.getName());
            }
            this.checkSingleValue(_exportParser, "formats", "format", "{" + StringUtil_mxJPO.joinMql(' ', true, formatNames, null) + "}");
        } else  {
            this.checkSingleValue(_exportParser, "formats", "format", "{}");
        }

        // check for all state flag
        if (this.allState == null)  {
            Assert.assertEquals(_exportParser.getLines("/updatePolicy/allstate/@value").size(),
                                0,
                                "check that not all state access is defined");
        } else  {
            Assert.assertEquals(
                    _exportParser.getLines("/updatePolicy/allstate/@value").size(),
                    1,
                    "check that all state access is defined");
            this.allState.checkExport(_exportParser);
        }

        // check all states
        for (final State state : this.states)
        {
            state.checkExport(_exportParser);
        }
    }


    /**
     * {@inheritDoc}
     * The original method is overwritten because for policies another path
     * exists for the values.
     *
     * @param _exportParser     parsed export
     * @param _kind             kind of the check
     * @param _tag              tag to check
     * @param _value            value to check (or <code>null</code> if value
     *                          is not defined)
     */
    public static void checkSingleValue(final Line _parentLine,
                                        final String _kind,
                                        final String _tag,
                                        final String _value)
    {
        if (_value != null)  {
            Assert.assertEquals(
                    _parentLine.getLines(_tag + "/@value").size(),
                    1,
                    "check " + _kind + " that " + _tag + " is defined");

            Assert.assertEquals(
                    _parentLine.getLines(_tag + "/@value").get(0),
                    _value,
                    "check " + _kind + " that " + _tag + " is " + _value);

        } else  {
            Assert.assertEquals(
                    _parentLine.getLines(_tag + "/@value").size(),
                    0,
                    "check " + _kind + " that no " + _tag + " is defined");
        }
    }

    /**
     * Abstract definition of a state.
     */
    public static abstract class AbstractState<AS extends AbstractState<AS>>
    {
        /** Access definitions for this state. */
        public final List<Access> access = new ArrayList<Access>();
        /** Values of this state. */
        private final Map<String,Object> values = new HashMap<String,Object>();

        /**
         * Appends a access filter definition.
         *
         * @param _access    access filters to append
         * @return this state instance
         */
        @SuppressWarnings("unchecked")
        public AS addAccess(final Access... _access)
        {
            this.access.addAll(Arrays.asList(_access));
            return (AS) this;
        }

        /**
         * Defines a new value entry which is put into {@link #values}.
         *
         * @param _key      key of the value (e.g. &quot;description&quot;)
         * @param _value    value of the value
         * @return this state instance
         */
        @SuppressWarnings("unchecked")
        public AS setValue(final String _key,
                           final Object _value)
        {
            this.values.put(_key, _value);
            return (AS) this;
        }

        /**
         * Returns the {@link #values} of this state.
         *
         * @return defined values
         */
        public Map<String,Object> getValues()
        {
            return this.values;
        }
    }

    /**
     * Represents one state of a policy.
     */
    public static class State
        extends AbstractState<State>
    {
        /** Name of the state. */
        private String name;
        /** List of all signatures for this state. */
        private final List<Signature> signatures = new ArrayList<Signature>();

        /**
         * Defines the {@code _name} of the state.
         *
         * @param _name     name of the state
         * @return this state instance
         */
        public State setName(final String _name)
        {
            this.name = _name;
            return this;
        }

        /**
         * Returns the {@link #name} of the state.
         *
         * @return name
         */
        public String getName()
        {
            return this.name;
        }

        /**
         * Appends a signature.
         *
         * @param _signature    signature to append
         * @return this state instance
         * @see #signatures
         */
        public PolicyData.State addSignature(final Signature _signature)
        {
            this.signatures.add(_signature);
            return this;
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         */
        protected void append4CIFile(final StringBuilder _cmd)
        {
            _cmd.append("    state \"").append(StringUtil_mxJPO.convertTcl(this.name)).append("\" {\n");
            for (final Access accessFilter : this.access)  {
                accessFilter.append4CIFile(_cmd);
            }
            for (final Map.Entry<String,Object> value : this.getValues().entrySet())  {
                _cmd.append("      ").append(value.getKey())
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(value.getValue().toString())).append("\"\n");
            }
            for (final Signature signature : this.signatures)
            {
                signature.append4CIFile(_cmd);
            }
            _cmd.append("    }\n");
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         */
        protected void append4Create(final StringBuilder _cmd)
        {
            _cmd.append("    state \"").append(StringUtil_mxJPO.convertMql(this.name)).append("\" public none owner none");
            for (final Access accessFilter : this.access)  {
                _cmd.append(' ').append(accessFilter.getMQLCreateString());
            }
            for (final Map.Entry<String,Object> value : this.getValues().entrySet())  {
                _cmd.append(' ').append(value.getKey())
                    .append(" \"").append(StringUtil_mxJPO.convertMql(value.getValue().toString())).append('\"');
            }
            for (final Signature signature : this.signatures)
            {
                signature.append4CIFile(_cmd);
            }
        }

        /**
         *
         * @param _exportParser     export parsed
         * @throws MatrixException if information could not be fetched
         */
        public void checkExport(final ExportParser _exportParser)
            throws MatrixException
        {
            boolean found = false;
            final String value = "\"" + AbstractTest.convertTcl(this.name) + "\"";
            for (final ExportParser.Line line : _exportParser.getRootLines().get(0).getChildren())  {
                if ("state".equals(line.getTag()) && line.getValue().startsWith(value))  {
                    found = true;

                    // access filter
                    String exportAccess  = "";
                    for (final Line subLine : line.getChildren())  {
                        if (subLine.getTag().equals("public")
                                || subLine.getTag().equals("owner")
                                || subLine.getTag().equals("user")
                                || subLine.getTag().equals("login")
                                || subLine.getTag().equals("revoke"))  {

                            exportAccess += "      " + subLine.getTag() + ' ' + subLine.getValue() + '\n';
                        }
                    }
                    final StringBuilder expAccess = new StringBuilder();
                    for (final Access accessFilter : this.access)  {
                        accessFilter.append4CIFile(expAccess);
                    }
                    Assert.assertEquals(
                            exportAccess,
                            expAccess.toString(),
                            "check access definition for state " + line.getValue());

                    // signature
                    for (final Signature signature : this.signatures)  {
                        signature.checkExport(line);
                    }

                    // check for defined values
                    for (final Map.Entry<String,Object> entry : this.getValues().entrySet())  {
                        PolicyData.checkSingleValue(
                                line,
                                entry.getKey(),
                                entry.getKey(),
                                (entry.getValue() instanceof Character)
                                        ? entry.getValue().toString()
                                        : "\"" + AbstractTest.convertTcl(entry.getValue().toString()) + "\"");
                    }
                }
            }
            Assert.assertTrue(found, "check that state '" + this.name + "' is found");
        }
    }

    /**
     * All state definition.
     */
    public static class AllState
        extends AbstractState<AllState>
    {
        /**
         * Appends the MQL statements to define the CI file.
         *
         * @param _cmd  string builder where to append the TCL statements
         */
        protected void append4CIFile(final StringBuilder _cmd)
        {
            _cmd.append("    allstate {\n");
            for (final Access accessFilter : this.access)  {
                accessFilter.append4CIFile(_cmd);
            }
            _cmd.append("    }\n");
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         */
        protected void append4Create(final StringBuilder _cmd)
        {
            _cmd.append(" allstate public none owner none");
            for (final Access accessFilter : this.access)  {
                _cmd.append(' ').append(accessFilter.getMQLCreateString());
            }
        }

        /**
         *
         * @param _exportParser     export parsed
         * @throws MatrixException if information could not be fetched
         */
        public void checkExport(final ExportParser _exportParser)
            throws MatrixException
        {
            int found = 0;
            for (final ExportParser.Line line : _exportParser.getRootLines().get(0).getChildren())  {
                if ("allstate".equals(line.getTag()))  {
                    found++;

                    // prepare export definition
                    String exportAccess  = "";
                    for (final Line subLine : line.getChildren())  {
                        if (subLine.getTag().equals("public")
                                || subLine.getTag().equals("owner")
                                || subLine.getTag().equals("user")
                                || subLine.getTag().equals("login")
                                || subLine.getTag().equals("revoke"))  {

                            exportAccess += "      " + subLine.getTag() + ' ' + subLine.getValue() + '\n';
                        }
                    }
                    // prepare expected definition
                    final StringBuilder expAccess = new StringBuilder();
                    for (final Access accessFilter : this.access)  {
                        accessFilter.append4CIFile(expAccess);
                    }
                    Assert.assertEquals(
                            exportAccess,
                            expAccess.toString(),
                            "check access definition for allstate " + line.getValue());
                }
            }
            Assert.assertEquals(found, 1, "exact one allstate must be defined");
        }
    }

    /**
     * Signature for a state of a policy.
     */
    public static class Signature
    {
        /** Name of the signature. */
        private String name;

        /** Filter of the signature. */
        private String filter;

        /** Branch of the signature. */
        private String branch;

        /**
         * Defines the {@code _name} of the signature.
         *
         * @param _name     name of the signature
         * @return this signature instance
         * @see #name
         */
        public Signature setName(final String _name)
        {
            this.name = _name;
            return this;
        }

        /**
         * Defines the {@code _filter} of the signature.
         *
         * @param _filter     filter of the signature
         * @return this signature instance
         * @see #filter
         */
        public Signature setFilter(final String _filter)
        {
            this.filter = _filter;
            return this;
        }

        /**
         * Defines the {@code _branch} of the signature.
         *
         * @param _branch     branch of the signature
         * @return this signature instance
         * @see #branch
         */
        public Signature setBranch(final String _branch)
        {
            this.branch = _branch;
            return this;
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         */
        protected void append4CIFile(final StringBuilder _cmd)
        {
            _cmd.append("        signature \"").append(AbstractTest.convertTcl(this.name)).append("\" {\n")
                .append("            branch \"").append(AbstractTest.convertTcl(this.branch)).append("\"\n")
                .append("            approve {}\n")
                .append("            ignore {}\n")
                .append("            reject {}\n")
                .append("            filter \"").append(AbstractTest.convertTcl(this.filter)).append("\"\n")
                .append("        }\n");
        }

        /**
         * Checks that the export is equal to the definition.
         *
         * @param _exportState  parsed line with exported state
         * @throws MatrixException if information could not be fetched
         */
        protected void checkExport(final ExportParser.Line _exportState)
        {
            boolean found = false;
            final String value = "\"" + AbstractTest.convertTcl(this.name) + "\"";
            for (final ExportParser.Line line : _exportState.getChildren())  {
                if ("signature".equals(line.getTag()) && line.getValue().startsWith(value))  {
                    found = true;
                    Assert.assertEquals(
                            line.evalSingleValue("branch"),
                            "\"" + AbstractTest.convertTcl(this.branch) + "\"",
                            "check for correct branch");
                    Assert.assertEquals(
                            line.evalSingleValue("approve"),
                            "{}",
                            "check for correct approve user");
                    Assert.assertEquals(
                            line.evalSingleValue("ignore"),
                            "{}",
                            "check for correct ignore user");
                    Assert.assertEquals(
                            line.evalSingleValue("reject"),
                            "{}",
                            "check for correct reject user");
                    Assert.assertEquals(
                            line.evalSingleValue("filter"),
                            "\"" + AbstractTest.convertTcl(this.filter) + "\"",
                            "check for correct filter");
                }
            }
            Assert.assertTrue(found, "check that signature " + this.name + " is found");
        }
    }
}

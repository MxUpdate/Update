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

package org.mxupdate.test.data.datamodel;

import java.util.Arrays;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.helper.LocalPathTypeDataList;
import org.mxupdate.test.data.datamodel.helper.LocaleAttributeList;
import org.mxupdate.test.data.util.DataList;
import org.mxupdate.test.data.util.FlagList;
import org.mxupdate.test.data.util.KeyNotDefinedList;
import org.mxupdate.test.data.util.SingleValueList;
import org.mxupdate.test.data.util.StringValueList;

import matrix.util.MatrixException;

/**
 * Used to define a relationship, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class RelationshipData
    extends AbstractDataWithTrigger<RelationshipData>
{
    /** Values for the from side. */
    private final FromTo from = new FromTo("from");
    /** Values for the to side. */
    private final FromTo to = new FromTo("to");
    /** All rule of this data with rule instances. */
    private final DataList<RuleData> rules = new DataList<>();
    /** Local attributes. */
    private final LocaleAttributeList localAttributes = new LocaleAttributeList();
    /** Local path types. */
    private final LocalPathTypeDataList localPathTypes = new LocalPathTypeDataList();

    /**
     * Initialize this relationship data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this relationship is
     *                  defined)
     * @param _name     name of the relationship
     */
    public RelationshipData(final AbstractTest _test,
                            final String _name)
    {
        super(_test, AbstractTest.CI.DM_RELATIONSHIP, _name);
    }

    /**
     * Returns the {@link #from} side information.
     *
     * @return from side information
     * @see #from
     */
    public FromTo from()
    {
        return this.from;
    }

    /**
     * Returns the {@link #to} side information.
     *
     * @return to side information
     * @see #to
     */
    public FromTo to()
    {
        return this.to;
    }

    /**
     * Assigns the {@code _rule} to this relationship instance. Hint! Maximum
     * one rule technical is possible!
     *
     * @param _rule     rule to defined
     * @return this relationship instance
     */
    public RelationshipData setRule(final RuleData _rule)
    {
        this.rules.add("rule", _rule);
        return this;
    }

    /**
     * Appends given {@code _attributes}.
     *
     * @param _attributes    attributes list to append
     * @return this type data instance
     */
    public RelationshipData addLocalAttribute(final AttributeData... _attributes)
    {
        this.localAttributes.addAll(Arrays.asList(_attributes));
        return this;
    }

    /**
     * Appends given {@code _localPathTypes}.
     *
     * @param _localPathTypes       local path types to append
     * @return this type data instance
     */
    public RelationshipData addLocalPathType(final PathTypeData... _localPathTypes)
    {
        this.localPathTypes.addAll(Arrays.asList(_localPathTypes));
        return this;
    }

    /**
     * Returns the TCL update file of this relationship data instance.
     *
     * @return TCL update file content
     */
    @Override
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate relationship \"${NAME}\" {\n");

        this.getFlags()     .append4Update("    ", strg);
        this.getValues()    .append4Update("    ", strg);
        this.getSingles()   .append4Update("    ", strg);
        this.getKeyValues() .append4Update("    ", strg);
        this.getTriggers()  .append4Update("    ", strg);
        this.getDatas()     .append4Update("    ", strg);
        this.rules          .append4Update("    ", strg);
        this.from           .appendUpdate(strg);
        this.to             .appendUpdate(strg);
        this.localAttributes.append4Update("    ", strg);
        this.localPathTypes .append4Update("    ", strg);
        this.getProperties().append4Update("    ", strg);

        strg.append("}");

        return strg.toString();
    }

    /**
     * Create the related relationship in MX for this relationship data
     * instance and appends the {@link #localAttributes}.
     *
     * @return this relationship data instance
     * @throws MatrixException if create failed
     */
    @Override
    public RelationshipData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd1 = new StringBuilder();
            cmd1.append("escape add relationship \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            this.getFlags().append4Create(cmd1);

            this.from.append4Create(cmd1);
            this.to.append4Create(cmd1);

            this.append4Create(cmd1);

            this.getTest().mql(cmd1);

            final StringBuilder cmd2 = new StringBuilder()
                        .append("escape mod relationship \"").append(AbstractTest.convertMql(this.getName())).append('\"');
            this.rules.append4CreateViaAdd(cmd2);

            this.getTest().mql(cmd2);

        }

        return this;
    }

    /**
     * {@inheritDoc}
     * Creates all depending types and relationships.
     *
     * @see #from
     * @see #to
     */
    @Override
    public RelationshipData createDependings()
        throws MatrixException
    {
        super.createDependings();

        this.rules          .createDependings();
        this.from.datas     .createDependings();
        this.to.datas       .createDependings();
        this.localAttributes.createDependings();
        this.localPathTypes .createDependings();

        return this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     */
    @Override
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        this.from           .checkExport(_exportParser);
        this.to             .checkExport(_exportParser);
        this.rules          .check4Export(_exportParser, "");
        this.localAttributes.checkExport(_exportParser);
        this.localPathTypes .checkExport(_exportParser);
    }

    /**
     * Cardinality of a side.
     */
    public enum Cardinality
    {
        /** Cardinality 1. */
        ONE,
        /** Cardinality Many. */
        MANY;
    }

    /**
     * Behavior in the case of revise or clone.
     */
    public enum Behavior
    {
        /** None. */
        NONE,
        /** Float. */
        FLOAT,
        /** Replicate. */
        REPLICATE;
    }

    /**
     * Defines one side definition of a relationship.
     */
    public final class FromTo
    {
        /** Information string about the side (value is to or from). */
        private final String side;
        /** Defines flags for this data piece. */
        private final FlagList flags = new FlagList();
        /** Single values of this data piece. */
        private final SingleValueList singles = new SingleValueList();
        /** String values of this data piece. */
        private final StringValueList strings = new StringValueList();
        /** All defined data elements. */
        private final DataList<AbstractAdminData<?>> datas = new DataList<>();
        /** List of not defined keys. */
        private final KeyNotDefinedList keyNotDefineds = new KeyNotDefinedList();

        /**
         * Defines the side string.
         *
         * @param _side     side string
         */
        private FromTo(final String _side)
        {
            this.side = _side;
        }

        /**
         * Defines the flag and the value.
         *
         * @param _key          key (name) of the flag
         * @param _value        <i>true</i> to activate the flag; otherwise
         *                      <i>false</i>; to undefine set to <code>null</code>
         * @return this data instance
         */
        public RelationshipData defFlag(final String _key,
                                        final Boolean _value)
        {
            this.flags.setFlag(_key, _value);
            return RelationshipData.this;
        }

        /**
         * Defines a new value entry which is put into {@link #singles}.
         *
         * @param _key      key of the value (e.g. &quot;description&quot;)
         * @param _value    value of the value
         * @return this original data instance
         */
        public RelationshipData defSingle(final String _key,
                                          final String _value)
        {
            this.singles.def(_key, _value);
            return RelationshipData.this;
        }

        /**
         * Defines a new value entry which is put into {@link #strings}.
         *
         * @param _key      key of the value (e.g. &quot;description&quot;)
         * @param _value    value of the value
         * @return this original data instance
         */
        public RelationshipData defString(final String _key,
                                          final String _value)
        {
            this.strings.def(_key, _value);
            return RelationshipData.this;
        }

        /**
         * Defines a {@code _data} for given {@code _tag}.
         *
         * @param _tag          used tag (name) of the data
         * @param _data         data instance
         * @return this data instance
         */
        public RelationshipData defData(final String _tag,
                                        final AbstractAdminData<?> _data)
        {
            this.datas.add(_tag, _data);
            return RelationshipData.this;
        }

        /**
         * Defines 'all' given {@code _tag}.
         *
         * @param _tag          used tag (name)
         * @return this data instance
         */
        public RelationshipData defDataAll(final String _tag)
        {
            this.datas.addAll(_tag);
            return RelationshipData.this;
        }

        /**
         * Defines key which must not be defined.
         *
         * @param _key  not defined key
         */
        public RelationshipData defNotDefined(final String _key)
        {
            this.keyNotDefineds.defKeyNotDefined(_key);
            return RelationshipData.this;
        }

        /**
         * Appends the TCL statements for the CI file for one side.
         *
         * @param _cmd      TCL string builder
         */
        protected void appendUpdate(final StringBuilder _cmd)
        {
            _cmd.append("    ").append(this.side).append(" {\n");

            this.flags  .append4Update("        ", _cmd);
            this.singles.append4Update("        ", _cmd);
            this.strings.append4Update("        ", _cmd);
            this.datas  .append4Update("        ", _cmd);

            _cmd.append("    }\n");
        }

        /**
         * Appends the MQL statements for the create for one side.
         *
         * @param _cmd      MQL string builder
         * @throws MatrixException if sub types / relationships could not be
         *                         created
         */
        protected void append4Create(final StringBuilder _cmd)
            throws MatrixException
        {
            _cmd.append(' ').append(this.side);

            this.flags  .append4Create(_cmd);
            this.singles.append4Create(_cmd);
            this.strings.append4Create(_cmd);
            this.datas  .append4Create(_cmd);
        }

        /**
         * Checks the export for one side of a relationship.
         *
         * @param _exportParser     export parser
         */
        protected void checkExport(final ExportParser _exportParser)
        {
            this.flags          .check4Export(_exportParser, this.side);
            this.singles        .check4Export(_exportParser, this.side);
            this.strings        .check4Export(_exportParser, this.side);
            this.datas          .check4Export(_exportParser, this.side);
            this.keyNotDefineds .check4Export(_exportParser, this.side);
        }
    }
}

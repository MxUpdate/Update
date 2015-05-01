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
import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.util.DataList;
import org.mxupdate.test.data.util.FlagList;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * Used to define a relationship, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class RelationshipData
    extends AbstractDataWithTrigger<RelationshipData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
        RelationshipData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /** Values for the from side. */
    private final FromTo from = new FromTo("from");
    /** Values for the to side. */
    private final FromTo to = new FromTo("to");
    /** All attributes of this data with attribute instances. */
    private final DataList<AbstractAttributeData<?>> attributes = new DataList<AbstractAttributeData<?>>();
    /** All rule of this data with rule instances. */
    private final DataList<RuleData> rules = new DataList<RuleData>();

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
        super(_test, AbstractTest.CI.DM_RELATIONSHIP, _name,
              RelationshipData.REQUIRED_EXPORT_VALUES,
              null);
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
     * Assigns the {@code attributes} to this data instance.
     *
     * @param _attributes       attribute to assign
     * @return this data instance
     */
    @Override
    public RelationshipData addAttribute(final AbstractAttributeData<?>... _attributes)
    {
        this.attributes.addAll(Arrays.asList(_attributes));
        return this;
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
        this.rules.clear();
        this.rules.add(_rule);
        return this;
    }

    /**
     * Returns the TCL update file of this relationship data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate relationship \"${NAME}\" {\n");

        this.getFlags()     .appendUpdate("    ", strg);
        this.getValues()    .appendUpdate("    ", strg);
        this.getSingles()   .appendUpdate("    ", strg);
        this.rules          .appendUpdate("    ", strg);
        this.from           .appendUpdate(strg);
        this.to             .appendUpdate(strg);
        this.attributes     .appendUpdate("    ", strg);
        this.getProperties().appendUpdate("    ", strg);

        strg.append("}");

        return strg.toString();
    }

    /**
     * Create the related relationship in MX for this relationship data
     * instance and appends the {@link #attributes}.
     *
     * @return this relationship data instance
     * @throws MatrixException if create failed
     */
    @Override()
    public RelationshipData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add relationship \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            this.getFlags().append4Create(cmd);

            this.from.append4Create(cmd);
            this.to.append4Create(cmd);
            this.attributes.append4Create(cmd);

            this.append4Create(cmd);

            cmd.append(";escape mod relationship \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            this.rules.append4CreateViaAdd(cmd);

            this.getTest().mql(cmd);
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
    @Override()
    public RelationshipData createDependings()
        throws MatrixException
    {
        super.createDependings();

        this.attributes.createDependings();
        this.rules.createDependings();

        this.from.types.createDependings();
        this.from.relationships.createDependings();

        this.to.types.createDependings();
        this.to.relationships.createDependings();

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
        // check symbolic name
        Assert.assertEquals(
                _exportParser.getSymbolicName(),
                this.getSymbolicName(),
                "check symbolic name");

        this.getFlags()  .checkExport(_exportParser, "");
        this.getValues() .checkExport(_exportParser, "");
        this.getSingles().checkExport(_exportParser, "");
        this.from        .checkExport(_exportParser);
        this.to          .checkExport(_exportParser);
        this.attributes  .checkExport(_exportParser, "");
        this.rules       .checkExport(_exportParser, "");
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
        /** Meaning of the side. */
        private String meaning;
        /** Cardinality of the side. */
        private Cardinality cardinality = Cardinality.MANY;
        /** Clone behavior of the side. */
        private Behavior clone = Behavior.NONE;
        /** Revise behavior of the side. */
        private Behavior revision = Behavior.NONE;

        /** All types flag for the from side. */
        private boolean allTypes;
        /** Defined types on the from side. */
        private final DataList<TypeData> types = new DataList<TypeData>();

        /** All relationship flag for the from side. */
        private boolean allRelationships;
        /** Defined relationships on the from side. */
        private final DataList<RelationshipData> relationships = new DataList<RelationshipData>();

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
        public RelationshipData setFlag(final String _key,
                                        final Boolean _value)
        {
            this.flags.setFlag(_key, _value);
            return RelationshipData.this;
        }

        /**
         * Defines the {@link #meaning} of the side.
         *
         * @param _meaning      meaning of the side
         * @return relationship data instance
         * @see #meaning
         */
        public RelationshipData setMeaning(final String _meaning)
        {
            this.meaning = _meaning;
            return RelationshipData.this;
        }

        /**
         * Defines the {@link #cardinality} of the side.
         *
         * @param _cardinality      cardinality of the side
         * @return relationship data instance
         * @see #cardinality
         */
        public RelationshipData setCardinality(final Cardinality _cardinality)
        {
            this.cardinality = _cardinality;
            return RelationshipData.this;
        }

        /**
         * Defines the {@link #clone} behavior of the side.
         *
         * @param _clone    clone behavior of the side
         * @return relationship data instance
         * @see #clone
         */
        public RelationshipData setClone(final Behavior _clone)
        {
            this.clone = _clone;
            return RelationshipData.this;
        }

        /**
         * Defines the {@link #revision} behavior of the side.
         *
         * @param _revision     revision behavior of the side
         * @return relationship data instance
         * @see #revision
         */
        public RelationshipData setRevision(final Behavior _revision)
        {
            this.revision = _revision;
            return RelationshipData.this;
        }

        /**
         * Assigns <code>_types</code> to the {@link #types list of types}
         * for this relationship.
         *
         * @param _types     types to assign
         * @return this relationship data instance
         * @see #types
         */
        public RelationshipData addType(final TypeData... _types)
        {
            this.types.addAll(Arrays.asList(_types));
            return RelationshipData.this;
        }

        /**
         * Defines that the relationship is assigned to all from types.
         *
         * @return this relationship data instance
         * @see #allTypes
         */
        public RelationshipData addAllTypes()
        {
            this.allTypes = true;
            return RelationshipData.this;
        }

        /**
         * Assigns <code>_relationships</code> to the
         * {@link #relationships list of relationships} for this
         * relationship.
         *
         * @param _relationships    relationship to assign
         * @return this relationship data instance
         * @see #relationships
         */
        public RelationshipData addRelationship(final RelationshipData... _relationships)
        {
            this.relationships.addAll(Arrays.asList(_relationships));
            return RelationshipData.this;
        }

        /**
         * Defines that the relationship is assigned to all relationships.
         *
         * @return this relationship data instance
         * @see #allRelationships
         */
        public RelationshipData addAllRelationships()
        {
            this.allRelationships = true;
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

            this.flags.appendUpdate("        ", _cmd);

            // meaning
            if (this.meaning != null)  {
                _cmd.append("        meaning \"").append(StringUtil_mxJPO.convertUpdate(this.meaning)).append("\"\n");
            }

            // cardinality
            if (this.cardinality != null)  {
                _cmd.append("        cardinality ").append(this.cardinality.name().toLowerCase()).append("\n");
            }

            // clone behavior
            if (this.clone != null)  {
                _cmd.append("        clone ").append(this.clone.name().toLowerCase()).append("\n");
            }

            // clone behavior
            if (this.revision != null)  {
                _cmd.append("        revision ").append(this.revision.name().toLowerCase()).append("\n");
            }

            // append from types
            if (this.allTypes)  {
                _cmd.append("        type all\n");
            } else  {
                this.types.appendUpdate("        ", _cmd);
            }

            // append from relationships
            if (this.allRelationships)  {
                _cmd.append("        relationship all\n");
            } else  {
                this.relationships.appendUpdate("        ", _cmd);
            }

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

            this.flags.append4Create(_cmd);

            // meaning
            if (this.meaning != null)  {
                _cmd.append(" meaning \"").append(AbstractTest.convertMql(this.meaning)).append('\"');
            }

            // cardinality
            if (this.cardinality != null)  {
                _cmd.append(" cardinality ").append(this.cardinality.name().toLowerCase());
            } else  {
                _cmd.append(" cardinality one");
            }

            // clone behavior
            if (this.clone != null)  {
                _cmd.append(" clone ").append(this.clone.name().toLowerCase());
            }

            // clone behavior
            if (this.revision != null)  {
                _cmd.append(" revision ").append(this.revision.name().toLowerCase());
            }

            // append to types
            if (this.allTypes)  {
                _cmd.append(" type all");
            } else  {
                this.types.append4Create(_cmd);
            }

            // append to relationships
            if (this.allRelationships)  {
                _cmd.append(" relationship all");
            } else if (!this.relationships.isEmpty())  {
                this.relationships.append4Create(_cmd);
            }
        }

        /**
         * Checks the export for one side of a relationship.
         *
         * @param _exportParser     export parser
         * @throws MatrixException if check failed
         */
        protected void checkExport(final ExportParser _exportParser)
            throws MatrixException
        {
            this.flags.checkExport(_exportParser, this.side);

            _exportParser
                    .checkValue(this.side + "/meaning",      "\"" + StringUtil_mxJPO.convertUpdate(this.meaning) + "\"")
                    .checkValue(this.side + "/cardinality",  this.cardinality.name().toLowerCase())
                    .checkValue(this.side + "/clone",        this.clone.name().toLowerCase())
                    .checkValue(this.side + "/revision",     this.revision.name().toLowerCase())
                    .checkList( this.side + "/type",         this.allTypes ? Arrays.asList(new String[]{"all"}) : this.types.toUpdateStringList())
                    .checkList( this.side + "/relationship", this.allRelationships ? Arrays.asList(new String[]{"all"}) : this.relationships.toUpdateStringList());
        }
    }
}

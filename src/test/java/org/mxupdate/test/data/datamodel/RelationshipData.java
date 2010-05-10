/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.test.data.datamodel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * Used to define a relationship, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class RelationshipData
    extends AbstractDataWithTrigger<RelationshipData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(1);
    static  {
        RelationshipData.REQUIRED_EXPORT_VALUES.add("description");
    }

    /**
     * Does the relationship prevent duplicates?
     *
     * @see #setPreventDuplicates(Boolean)
     */
    private Boolean preventDuplicates = false;

    /**
     * Values for the from side.
     *
     * @see #from()
     */
    private final FromTo from = new FromTo("from");

    /**
     * Values for the to side.
     *
     * @see #to()
     */
    private final FromTo to = new FromTo("to");

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
              RelationshipData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Defines if this relationship data instance must prevent duplicates.
     *
     * @param _preventDuplicates    <i>true</i> if the data instance must
     *                              prevent duplicates; otherwise <i>false</i>
     * @return this relationship data instance
     * @see #preventDuplicates
     */
    public RelationshipData setPreventDuplicates(final Boolean _preventDuplicates)
    {
        this.preventDuplicates = _preventDuplicates;
        return this;
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
     * Returns the TCL update file of this relationship data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod relationship \"${NAME}\"");
        this.append4CIFileValues(cmd);

        // prevent duplicates
        if (this.preventDuplicates != null)  {
            cmd.append(' ');
            if (!this.preventDuplicates)  {
                cmd.append('!');
            }
            cmd.append("preventduplicates");
        }

        // append from side
        this.from.append4CIFile(cmd);

        // append to types
        this.to.append4CIFile(cmd);

        // append attributes
        this.append4CIAttributes(cmd);

        return cmd.toString();
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

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add relationship \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            // prevent duplicates
            if (this.preventDuplicates != null)  {
                cmd.append(' ');
                if (!this.preventDuplicates)  {
                    cmd.append('!');
                }
                cmd.append("preventduplicates");
            }

            // append from side
            this.from.append4Create(cmd);

            // append to side
            this.to.append4Create(cmd);

            this.append4Create(cmd);

            this.getTest().mql(cmd);
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
        super.checkExport(_exportParser);

        // prevent duplicates
        Assert.assertEquals(
                _exportParser.getLines("/mql/preventduplicates/@value").size(),
                ((this.preventDuplicates != null) && this.preventDuplicates) ? 1 : 0,
                "check prevent duplicates is defined " + _exportParser.getLines("/mql/preventduplicates/@value"));
        Assert.assertEquals(
                _exportParser.getLines("/mql/!preventduplicates/@value").size(),
                ((this.preventDuplicates != null) && this.preventDuplicates) ? 0 : 1,
                "check prevent duplicates is defined " + _exportParser.getLines("/mql/!preventduplicates/@value"));

        // from side
        this.from.checkExport(_exportParser);

        // to side
        this.to.checkExport(_exportParser);
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
        /**
         * Information string about the side (value is to or from).
         */
        private final String side;

        /**
         * Must the connection propagated?
         *
         * @see #setPropagateConnection(Boolean)
         */
        private Boolean propagateConnection = false;

        /**
         * Must the modification propagated?
         *
         * @see #setPropagateModify(Boolean)
         */
        private Boolean propagateModify = false;

        /**
         * Meaning of the side.
         *
         * @see #setMeaning(String)
         */
        private String meaning;

        /**
         * Cardinality of the side.
         *
         * @see #setCardinality(Cardinality)
         */
        private Cardinality cardinality;

        /**
         * Clone behavior of the side.
         *
         * @see #setClone(Behavior)
         */
        private Behavior clone;

        /**
         * Revise behavior of the side.
         *
         * @see #setRevision(Behavior)
         */
        private Behavior revision;

        /**
         * All types flag for the from side.
         *
         * @see #addFromAllTypes()
         * @see #create()
         * @see #checkExport(ExportParser)
         */
        private boolean allTypes;

        /**
         * Defined types on the from side.
         *
         * @see #addFromType(TypeData...)
         * @see #create()
         * @see #checkExport(ExportParser)
         */
        private final Set<TypeData> types = new HashSet<TypeData>();

        /**
         * All relationship flag for the from side.
         *
         * @see #addFromAllRelationships()
         * @see #create()
         * @see #checkExport(ExportParser)
         */
        private boolean allRelationships;

        /**
         * Defined relationships on the from side.
         *
         * @see #addFromRelationship(RelationshipData...)
         * @see #create()
         * @see #checkExport(ExportParser)
         */
        private final Set<RelationshipData> relationships = new HashSet<RelationshipData>();

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
         * Defines if this relationship data instance must propate connection.
         *
         * @param _propagateConnection  <i>true</i> if the data instance must
         *                              propagate connections; otherwise
         *                              <i>false</i>
         * @return this relationship data instance
         * @see #propagateConnection
         */
        public RelationshipData setPropagateConnection(final Boolean _propagateConnection)
        {
            this.propagateConnection = _propagateConnection;
            return RelationshipData.this;
        }

        /**
         * Defines if this relationship data instance must propagate modify.
         *
         * @param _propagateModify  <i>true</i> if the data instance must
         *                          propagate modify; otherwise
         *                          <i>false</i>
         * @return this relationship data instance
         * @see #propagateConnection
         */
        public RelationshipData setPropagateModify(final Boolean _propagateModify)
        {
            this.propagateModify = _propagateModify;
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
         * Assigns <code>_types</code> to the {@link #fromTypes list of from types}
         * for this relationship.
         *
         * @param _types     types to assign
         * @return this relationship data instance
         * @see #fromTypes
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
         * @see #fromAllTypes
         */
        public RelationshipData addAllTypes()
        {
            this.allTypes = true;
            return RelationshipData.this;
        }

        /**
         * Returns all assigned {@link #fromTypes from types}.
         *
         * @return all from types
         * @see #fromTypes
         */
        public Set<TypeData> getTypes()
        {
            return this.types;
        }

        /**
         * Assigns <code>_relationships</code> to the
         * {@link #fromRelationships list of from relationships} for this
         * relationship.
         *
         * @param _relationships    relationship to assign
         * @return this relationship data instance
         * @see #fromRelationships
         */
        public RelationshipData addRelationship(final RelationshipData... _relationships)
        {
            this.relationships.addAll(Arrays.asList(_relationships));
            return RelationshipData.this;
        }

        /**
         * Defines that the relationship is assigned to all from relationships.
         *
         * @return this relationship data instance
         * @see #fromAllTypes
         */
        public RelationshipData addAllRelationships()
        {
            this.allRelationships = true;
            return RelationshipData.this;
        }

        /**
         * Returns all assigned {@link #fromRelationships from relationships}.
         *
         * @return all from types
         * @see #fromRelationships
         */
        public Set<RelationshipData> getRelationships()
        {
            return this.relationships;
        }

        /**
         * Appends the TCL statements for the CI file for one side.
         *
         * @param _cmd      TCL string builder
         */
        protected void append4CIFile(final StringBuilder _cmd)
        {
            // propagate connection
            if (this.propagateConnection != null)  {
                _cmd.append(' ').append(this.side).append(' ');
                if (!this.propagateConnection)  {
                    _cmd.append('!');
                }
                _cmd.append("propagateconnection");
            }

            // propagate modify
            if (this.propagateModify != null)  {
                _cmd.append(' ').append(this.side).append(' ');
                if (!this.propagateModify)  {
                    _cmd.append('!');
                }
                _cmd.append("propagatemodify");
            }

            // meaning
            if (this.meaning != null)  {
                _cmd.append(" meaning \"").append(StringUtil_mxJPO.convertTcl(this.meaning)).append('\"');
            }

            // cardinality
            if (this.cardinality != null)  {
                _cmd.append(" cardinality ").append(this.cardinality.name().toLowerCase());
            }

            // clone behavior
            if (this.clone != null)  {
                _cmd.append(" clone ").append(this.clone.name().toLowerCase());
            }

            // clone behavior
            if (this.revision != null)  {
                _cmd.append(" revision ").append(this.revision.name().toLowerCase());
            }

            // append from types
            if (this.allTypes)  {
                _cmd.append(" " + this.side + " add type all");
            } else if (!this.types.isEmpty())  {
                _cmd.append(" " + this.side);
                for (final TypeData type : this.types)  {
                    _cmd.append(" add type \"").append(AbstractTest.convertTcl(type.getName())).append("\"");
                }
            }

            // append from relationships
            if (this.allRelationships)  {
                _cmd.append(" " + this.side + " add relationship all");
            } else if (!this.relationships.isEmpty())  {
                _cmd.append(" " + this.side);
                for (final RelationshipData relationship : this.relationships)  {
                    _cmd.append(" add relationship \"").append(AbstractTest.convertTcl(relationship.getName())).append("\"");
                }
            }
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

            // propagate connection
            _cmd.append(' ');
            if ((this.propagateConnection == null) || !this.propagateConnection)  {
                    _cmd.append('!');
            }
            _cmd.append("propagateconnection");

            // propagate modify
            _cmd.append(' ');
            if ((this.propagateModify == null) || !this.propagateModify)  {
                _cmd.append('!');
            }
            _cmd.append("propagatemodify");

            // meaning
            if (this.meaning != null)  {
                _cmd.append(" meaning \"").append(StringUtil_mxJPO.convertMql(this.meaning)).append('\"');
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
            } else if (!this.types.isEmpty())  {
                _cmd.append(" type ");
                boolean first = true;
                for (final TypeData type : this.types)  {
                    type.create();
                    if (first)  {
                        first = false;
                    } else  {
                        _cmd.append(',');
                    }
                    _cmd.append("\"").append(AbstractTest.convertMql(type.getName())).append("\"");
                }
            }

            // append to relationships
            if (this.allRelationships)  {
                _cmd.append(" relationship all");
            } else if (!this.relationships.isEmpty())  {
                _cmd.append(" relationship ");
                boolean first = true;
                for (final RelationshipData relationship : this.relationships)  {
                    relationship.create();
                    if (first)  {
                        first = false;
                    } else  {
                        _cmd.append(',');
                    }
                    _cmd.append("\"").append(AbstractTest.convertMql(relationship.getName())).append("\"");
                }
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
            // propagate modify
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/propagateconnection/@value").size(),
                    ((this.propagateConnection != null) && this.propagateConnection) ? 1 : 0,
                    "check propagate connection is defined " + _exportParser.getLines("/mql/" + this.side + "/propagateconnection/@value"));
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/!propagateconnection/@value").size(),
                    ((this.propagateConnection != null) && this.propagateConnection) ? 0 : 1,
                    "check propagate connection is defined " + _exportParser.getLines("/mql/" + this.side + "/!propagateconnection/@value"));

            // propagate modify
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/propagatemodify/@value").size(),
                    ((this.propagateModify != null) && this.propagateModify) ? 1 : 0,
                    "check propagate modify is defined " + _exportParser.getLines("/mql/" + this.side + "/propagatemodify/@value"));
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/!propagatemodify/@value").size(),
                    ((this.propagateModify != null) && this.propagateModify) ? 0 : 1,
                    "check propagate modify is defined " + _exportParser.getLines("/mql/" + this.side + "/!propagatemodify/@value"));

            // meaning
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/meaning/@value").get(0),
                    (this.meaning != null) ? "\"" + StringUtil_mxJPO.convertTcl(this.meaning) + "\"" : "\"\"",
                    "check for correct meaning");

            // cardinality
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/cardinality/@value").get(0),
                    (this.cardinality == null) || (this.cardinality == RelationshipData.Cardinality.ONE) ? "\"One\"" : "\"N\"",
                    "check for correct cardinality");

            // clone behavior
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/clone/@value").get(0),
                    (this.clone != null) ? "\"" + this.clone.name().toLowerCase() + "\"" : "\"none\"",
                    "check for correct clone behavior");

            // revision behavior
            Assert.assertEquals(
                    _exportParser.getLines("/mql/" + this.side + "/revision/@value").get(0),
                    (this.revision != null) ? "\"" + this.revision.name().toLowerCase() + "\"" : "\"none\"",
                    "check for correct revision behavior");

            // types and relationships
            final Set<String> paresedFroms = new HashSet<String>(_exportParser.getLines("/mql/" + this.side + "/add/@value"));
            Assert.assertEquals(
                    paresedFroms.size(),
                    ((this.allTypes) ? 1 : this.types.size()) + ((this.allRelationships) ? 1 : this.relationships.size()),
                    "check that all " + this.side + " types and relationshipos are defined");
            if (this.allTypes)  {
                Assert.assertTrue(
                        paresedFroms.contains("type \"all\""),
                        "check that all " + this.side + " types are defined " + paresedFroms);
            } else  {
                for (final TypeData type : this.types)  {
                    final String line = "type \"" + StringUtil_mxJPO.convertTcl(type.getName()) + "\"";
                    Assert.assertTrue(
                            paresedFroms.contains(line),
                            "check that " + this.side + " " + type.getName() + " is defined");
                }
            }
            if (this.allRelationships)  {
                Assert.assertTrue(
                        paresedFroms.contains("relationship \"all\""),
                        "check that all " + this.side + " relationships are defined " + paresedFroms);
            } else  {
                for (final RelationshipData relationship : this.relationships)  {
                    final String line = "relationship \"" + StringUtil_mxJPO.convertTcl(relationship.getName()) + "\"";
                    Assert.assertTrue(
                            paresedFroms.contains(line),
                            "check that " + this.side + " " + relationship.getName() + " is defined");
                }
            }
        }
    }
}

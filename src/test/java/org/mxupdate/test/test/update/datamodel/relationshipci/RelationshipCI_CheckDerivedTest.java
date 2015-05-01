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

package org.mxupdate.test.test.update.datamodel.relationshipci;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test implements checks what is allowed to change and what is not allowed to
 * changed for a derived relationship.
 *
 * @author The MxUpdate Team
 */
public class RelationshipCI_CheckDerivedTest
    extends AbstractTest
{
    /** Type used for from / to definition. */
    private final String type1   = AbstractTest.PREFIX + "Type1";
    /** Type used for from / to definition. */
    private final String type2   = AbstractTest.PREFIX + "Type2";

    /** Relationship used for from / to definition. */
    private final String rel1    = AbstractTest.PREFIX + "Rel1";
    /** Relationship used for from / to definition. */
    private final String rel2    = AbstractTest.PREFIX + "Rel2";

    /** Root relationship. */
    private final String relRoot = AbstractTest.PREFIX + "Root";
    /** Child relationship */
    private final String relName = AbstractTest.PREFIX + "Test";

    /**
     * Test data to check not allowed updates for child relationship.
     *
     * @return test data
     */
    @DataProvider(name = "dataNotAllowed")
    public Object[][] getDataNotAllowed()
    {
        return new Object[][]  {
            // compositional
            {"",                            "compositional"},
            // from meaning
            {"from meaning abc",            "from meaning def"},
            // from type
            {"from type " + this.type1,     "from add type " + this.type2},
            // from type
            {"from relation " + this.rel1,  "from add relation " + this.rel2},
            // from cardinality
            {"from cardinality one",        "from cardinality many"},
            {"from cardinality many",       "from cardinality one"},
            // from revision
            {"from revision none",          "from revision float"},
            {"from revision none",          "from revision replicate"},
            {"from revision float",         "from revision none"},
            {"from revision float",         "from revision replicate"},
            {"from revision replicate",     "from revision none"},
            {"from revision replicate",     "from revision float"},
            // from clone
            {"from clone none",             "from clone float"},
            {"from clone none",             "from clone replicate"},
            {"from clone float",            "from clone none"},
            {"from clone float",            "from clone replicate"},
            {"from clone replicate",        "from clone none"},
            {"from clone replicate",        "from clone float"},
            // from propagate modify
            {"from propagatemodify",        "from notpropagatemodify"},
            {"from notpropagatemodify",     "from propagatemodify"},
            // from propagate connection
            {"from propagateconnection",    "from notpropagateconnection"},
            {"from notpropagateconnection", "from propagateconnection"},
            // to meaning
            {"to meaning abc",              "to meaning def"},
            // to type
            {"to type " + this.type1,       "to add type " + this.type2},
            // to cardinality
            {"to cardinality one",          "to cardinality many"},
            {"to cardinality many",         "to cardinality one"},
            // to revision
            {"to revision none",            "to revision float"},
            {"to revision none",            "to revision replicate"},
            {"to revision float",           "to revision none"},
            {"to revision float",           "to revision replicate"},
            {"to revision replicate",       "to revision none"},
            {"to revision replicate",       "to revision float"},
            // to clone
            {"to clone none",               "to clone float"},
            {"to clone none",               "to clone replicate"},
            {"to clone float",              "to clone none"},
            {"to clone float",              "to clone replicate"},
            {"to clone replicate",          "to clone none"},
            {"to clone replicate",          "to clone float"},
            // to propagate modify
            {"to propagatemodify",          "to notpropagatemodify"},
            {"to notpropagatemodify",       "to propagatemodify"},
            // to propagate connection
            {"to propagateconnection",      "to notpropagateconnection"},
            {"to notpropagateconnection",   "to propagateconnection"},
        };
    }

    /**
     * Negative test to check not allowed change in the derived relationship.
     *
     * @param _rootMql      MQL for the root relationship
     * @param _childMql     MQL for the child relationship
     * @throws Exception if test failed
     */
    @Test(description = "negative test to check not allowed change in the derived relationship",
          dataProvider = "dataNotAllowed",
          expectedExceptions = MatrixException.class,
          expectedExceptionsMessageRegExp = "(.|\n)*("
                                          +  "(Cannot override sub clause)"
                                          + "|(the end types of parent relationship do not allow the type)"
                                          + "|(the end relationships of parent relationship do not allow the relationship)"
                                          + "|(Compositional relationship type must be derived from the compositional relationship type)"
                                          + ")(.|\n)*")
    public void negativeTestNotAllowed(final String _rootMql,
                                       final String _childMql)
        throws Exception
    {
        if (_rootMql.contains("type"))  {
            this.mql("add type " + this.type1);
            this.mql("add type " + this.type2);
        }
        if (_rootMql.contains("relation"))  {
            this.mql("add relation " + this.rel1);
            this.mql("add relation " + this.rel2);
        }

        this.mql("add relation " + this.relRoot + " " + _rootMql);

        this.mql("add relation " + this.relName + " derived " + this.relRoot);
        this.mql("mod relation " + this.relName + " "+ _childMql);
    }

    /**
     * Test data to check allowed updates for child relationship.
     *
     * @return test data
     */
    @DataProvider(name = "dataAllowed")
    public Object[][] getDataAllowed()
    {
        return new Object[][]  {
            // description
            {"description abc",             "description def"},
            // compositional
            {"compositional",               ""},
            // prevent duplicates
            {"preventduplicates",           "!preventduplicates"},
            {"!preventduplicates",           "preventduplicates"},
            // hidden
            {"hidden",                      "!hidden"},
            {"!hidden",                      "hidden"},
            // from type
            {"from type all",               "from add type " + this.type1},
            {"from type " + this.type1,     "from add type all"},
            // from relation
            {"from relation all",           "from add relation " + this.rel1},
            {"from relation " + this.rel1,  "from add relation all"},
            // to type
            {"to type all",                 "to add type " + this.type1},
            {"to type " + this.type1,       "to add type all"},
            // to relation
            {"to relation all",             "to add relation " + this.rel1},
            {"to relation " + this.rel1,    "to add relation all"},
        };
    }

    /**
     * Positive test to check allowed change in the derived relationship.
     *
     * @param _rootMql      MQL for the root relationship
     * @param _childMql     MQL for the child relationship
     * @throws Exception if test failed
     */
    @Test(description = "positive test to check allowed change in the derived relationship",
          dataProvider = "dataAllowed")
    public void positiveTestAllowed(final String _rootMql,
                                    final String _childMql)
        throws Exception
    {
        if (_rootMql.contains("type"))  {
            this.mql("add type " + this.type1);
            this.mql("add type " + this.type2);
        }
        if (_rootMql.contains("relation"))  {
            this.mql("add relation " + this.rel1);
            this.mql("add relation " + this.rel2);
        }

        this.mql("add relation " + this.relRoot + " " + _rootMql);

        this.mql("add relation " + this.relName + " derived " + this.relRoot);
        this.mql("mod relation " + this.relName + " "+ _childMql);
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }
}

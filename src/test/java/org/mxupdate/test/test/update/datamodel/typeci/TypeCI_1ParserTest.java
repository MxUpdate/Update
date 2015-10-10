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

package org.mxupdate.test.test.update.datamodel.typeci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Type_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Type_mxJPO type CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class TypeCI_1ParserTest
    extends AbstractParserTest<Type_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" !hidden"},
            // registered name
            {"1a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" !hidden"},
            {"1b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden"},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"2b) description not defined",
                    "description \"\" !hidden",
                    "!hidden"},
            {"2c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden"},
            {"2d) tab's in description",
                    "",
                    "description \"abc\tdef\" !hidden"},
            // kind
            {"3a) basic kind",
                    "description \"\" !hidden",
                    "description \"\" kind basic !hidden"},
            {"3b) composed kind",
                    "",
                    "description \"\" kind composed !hidden"},
            // abstract flag
            {"4a) not abstract",
                    "description \"\" !hidden",
                    "description \"\" !abstract !hidden"},
            {"4b) abstract",
                    "",
                    "description \"\" abstract !hidden"},
            // derived
            {"5a) derived",
                    "",
                    "description \"\" derived \"123\" !hidden"},
            {"5b) not derived (with empty string)",
                    "description \"\" !hidden",
                    "description \"\" derived \"\" !hidden"},
            // hidden flag
            {"6a) hidden",
                    "",
                    "description \"\" hidden"},
            {"6b) not hidden (not defined)",
                    "description \"\" !hidden",
                    "description \"\""},
            // action trigger
            {"7a) action trigger with input",
                    "",
                    "description \"\" !hidden trigger createevent action \"{}\\\"\" input \"{}\\\"\""},
            {"7b) action trigger w/o input",
                    "description \"\" !hidden trigger createevent action \"{}\\\"\" input \"\"",
                    "description \"\" !hidden trigger createevent action \"{}\\\"\""},
            // check trigger
            {"8a) check trigger with input",
                    "",
                    "description \"\" !hidden trigger createevent check \"{}\\\"\" input \"{}\\\"\""},
            {"8b) check trigger w/o input",
                    "description \"\" !hidden trigger createevent check \"{}\\\"\" input \"\"",
                    "description \"\" !hidden trigger createevent check \"{}\\\"\""},
            // override trigger
            {"9a) override trigger with input",
                    "",
                    "description \"\" !hidden trigger createevent override \"{}\\\"\" input \"{}\\\"\""},
            {"9b) override trigger w/o input",
                    "description \"\" !hidden trigger createevent override \"{}\\\"\" input \"\"",
                    "description \"\" !hidden trigger createevent override \"{}\\\"\""},
            // methods
            {"10a) method",
                    "",
                    "description \"\" !hidden method \"111\""},
            {"10b) method name w/o apostrophe",
                    "description \"\" !hidden method \"111\"",
                    "description \"\" !hidden method 111"},
            {"10c) two method (to check sort)",
                    "description \"\" !hidden method \"111\" method \"222\"",
                    "description \"\" !hidden method \"222\" method \"111\""},
             // attribute
            {"11a) attribute",
                    "",
                    "description \"\" !hidden attribute \"111\""},
            {"11b) attribute name w/o apostrophe",
                    "description \"\" !hidden attribute \"111\"",
                    "description \"\" !hidden attribute 111"},
            {"11c) two attributes (to check sort)",
                    "description \"\" !hidden attribute \"111\" attribute \"222\"",
                    "description \"\" !hidden attribute \"222\" attribute \"111\""},
            // property
            {"12a) property special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\""},
            {"12b) property and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" value \"{}\\\"\""},
            {"12c) property link special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\""},
            {"12d) property link and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // local attribute

            // general attribute definition
            {"100a) local binary attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" } "},
            {"100b) local boolean attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" } "},
            {"100c) local date attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "},
            {"100d) local integer attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "},
            {"100e) local real attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "},
            {"100f) local string attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" } "},

             // attribute registered name
            {"101a) attribute symbolic name",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"101b) attribute two symbolic names",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute description
            {"102a) attribute description",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"102b) attribute description not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"102c) multi-line attribute description",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute hidden flag
            {"103a) attribute hidden",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"103b) attribute not hidden (not defined)",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute multivalue flag
            {"104a) attribute multivalue flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" }"},
            {"104b) attribute multivalue flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" }"},

            // attribute resetonclone flag
            {"105a) attribute resetonclone flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" }"},
            {"105b) attribute resetonclone flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" }"},

            // attribute resetonrevision flag
            {"106a) attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" }"},
            {"106b) attribute resetonrevision flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" }"},

            // attribute default value
            {"107a) attribute default value",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  }"},
            {"107b) attribute default value not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      }"},
            {"107c) multi-line attribute default value",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" }"},

            // real attribute rangevalue flag
            {"108a) real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" }"},
            {"108b) real attribute rangevalue flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" }"},

            // string attribute multiline flag
            {"109a) attribute multiline flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" }"},
            {"109b) attribute multiline flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" }"},

             // string attribute maxlength
            {"110a) attribute maxlength",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" }"},
            {"110b) attribute maxlength not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" }"},

            // attribute rule
            {"111a) attribute rule",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  }"},
            {"111a) attribute rule list (if more than one none)",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  }"},

            // attribute dimension
            {"112a) real attribute dimension",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  }"},

            // attribute action trigger
            {"120a) action trigger with input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" }"},
            {"120b) action trigger w/o input",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" }"},
            //  attribute check trigger
            {"121a) check trigger with input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" }"},
            {"121b) check trigger w/o input",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" }"},
            //  attribute override trigger
            {"122a) override trigger with input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" }"},
            {"122b) override trigger w/o input",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" }"},

            // attribute ranges
            {"130a) attribute range",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" }"},
            {"130b) attribute range",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" }"},
            {"130c) attribute range >",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" }"},
            {"130d) attribute range >=",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" }"},
            {"130e) attribute range <",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" }"},
            {"130f) attribute range <=",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" }"},
            {"130g) attribute range !=",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" }"},
            {"130h) attribute range match",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" }"},
            {"130i) attribute range !match",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" }"},
            {"130j) attribute range smatch",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" }"},
            {"130k) attribute range !smatch",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" }"},
            {"130l) attribute range program",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" }"},
            {"130m) attribute range program input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" }"},
            {"130n) attribute range between inclusive",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive }"},
            {"130o) attribute range between exclusive",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive }"},

            // attribute property
            {"140a) attribute property special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" }"},
            {"140b) attribute property and value special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" }"},
            {"140c) attribute property link special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" }"},
            {"140d) attribute property link and value special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // local path type

            {"300) local type path",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // registered name
            {"301a) local type path: symbolic name",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_abc\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"301b) local type path: two symbolic names",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // description
            {"302a) local type path: description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc def\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"302b) local type path: description not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "!hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"302c) local type path: multi-line description ",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc\ndef\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"302d) local type path: tab's in description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc\tdef\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // global attribute
            {"311a) local type path: global attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + " attribute \"111\" "
                            + "}"},
            {"311b) local type path: global attribute name w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + " attribute \"111\" "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute 111 "
                            + "}"},
            {"311c) local type path: two global attributes (to check sort)",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute \"111\" attribute \"222\" "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute \"222\" attribute \"111\" "
                            + "}"},
            // property
            {"312a) local type path: property special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" "
                            + "}"},
            {"312b) local type path: property and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" value \"{}\\\"\" "
                            + "}"},
            {"312c) local type path: property link special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" to type \"{}\\\"\" "
                            + "}"},
            {"312d) local type path: property link and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" "
                            + "}"},

            // from cardinality
            {"301a) local type path from: one cardinality",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality one  } "
                                    + "to   { } "
                            + "}"},
            {"301b) local type path from: default cardinality",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from {                   } "
                                    + "to   { } "
                            + "}"},
            // from type
            {"302a) local type path from: all type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type all } "
                                    + "to   { } "
                            + "}"},
            {"302b) local type path from: one type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"302c) local type path from: one type w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type ABC } "
                                    + "to   { } "
                            + "}"},
            {"302d) local type path from: two types unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" type \"DEF\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"DEF\" type \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            // from relationship
            {"303a) local type path from: all relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship all } "
                                    + "to   { } "
                            + "}"},
            {"303b) local type path from: 1 relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"303c) local type path from: 1 relationship w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship ABC } "
                                    + "to   { } "
                            + "}"},
            {"303d) local type path from: 2 relationships unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" relationship \"DEF\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"DEF\" relationship \"ABC\" } "
                                    + "to   { } "
                            + "}"},

            // to type
            {"312a) local type path to: all type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type all } "
                            + "}"},
            {"312b) local type path to: one type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type \"ABC\" } "
                            + "}"},
            {"312c) local type path to: one type w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type \"ABC\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type ABC } "
                            + "}"},
            {"312d) local type path to: two types unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type \"ABC\" type \"DEF\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type \"DEF\" type \"ABC\" } "
                            + "}"},
            // to relationship
            {"313a) local type path to: all relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship all } "
                            + "}"},
            {"313b) local type path to: 1 relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship \"ABC\" } "
                            + "}"},
            {"313c) local type path to: 1 relationship w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship \"ABC\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship ABC } "
                            + "}"},
            {"313d) local type path to: 2 relationships unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship \"ABC\" relationship \"DEF\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship \"DEF\" relationship \"ABC\" } "
                            + "}"},

            // general attribute definition
            {"320a) local type path: local binary attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" } "
                            + "}"},
            {"320b) local type path: local boolean attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" } "
                            + "}"},
            {"320c) local type path: local date attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"320d) local type path: local integer attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"320e) local type path: local real attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"320f) local type path: local string attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" } "
                            + "}"},

             // attribute registered name
            {"321a) local type path: local attribute symbolic name",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"321b) local type path: attribute two symbolic names",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute description
            {"322a) local type path: locale attribute description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"322b) local type path: local attribute description not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"322c) local type path: multi-line local attribute description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute hidden flag
            {"323a) local type path: local attribute hidden",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"323b) local type path: local attribute not hidden (not defined)",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute multivalue flag
            {"324a) local type path: local attribute multivalue flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"324b) local type path: local attribute multivalue flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute resetonclone flag
            {"325a) local type path: local attribute resetonclone flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" } "
                            + "}"},
            {"325b) local type path: local attribute resetonclone flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" } "
                            + "}"},

            // attribute resetonrevision flag
            {"326a) local type path: local attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" } "
                            + "}"},
            {"326b) local type path: local attribute resetonrevision flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" } "
                            + "}"},

            // attribute default value
            {"327a) local type path: local attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  } "
                            + "}"},
            {"327b) local type path: local attribute default value not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      } "
                            + "}"},
            {"327c) local type path: multi-line local attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" } "
                            + "}"},

            // real attribute rangevalue flag
            {"328a) local type path: local real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" } "
                            + "}"},
            {"328b) local type path: local real attribute rangevalue flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" } "
                            + "}"},

            // string attribute multiline flag
            {"329a) local type path: local attribute multiline flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" } "
                            + "}"},
            {"329b) local type path: local attribute multiline flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" } "
                            + "}"},

             // string attribute maxlength
            {"330a) local type path: local attribute maxlength",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" } "
                            + "}"},
            {"330b) local type path: local attribute maxlength not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" } "
                            + "}"},

            // attribute rule
            {"331a) local type path: local attribute rule",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  } "
                            + "}"},
            {"331a) local type path: local attribute rule list (if more than one none)",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  } "
                            + "}"},

            // attribute dimension
            {"332a) local type path: local real attribute dimension",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  } "
                            + "}"},

            // attribute action trigger
            {"330a) local type path: local action trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"330b) local type path: local action trigger w/o input",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" } "
                            + "}"},
            //  attribute check trigger
            {"331a) local type path: local attribute check trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"331b) local type path: local attributecheck trigger w/o input",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" } "
                            + "}"},
            //  attribute override trigger
            {"332a) local type path: local attribute override trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"332b) local type path: local attribute override trigger w/o input",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" } "
                            + "}"},

            // attribute ranges
            {"340a) local type path: local attribute range",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" } "
                            + "}"},
            {"340b) local type path: local attribute range",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" } "
                            + "}"},
            {"340c) local type path: local attribute range >",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" } "
                            + "}"},
            {"340d) local type path: local attribute range >=",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" } "
                            + "}"},
            {"340e) local type path: local attribute range <",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" } "
                            + "}"},
            {"340f) local type path: local attribute range <=",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" } "
                            + "}"},
            {"340g) local type path: local attribute range !=",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" } "
                            + "}"},
            {"340h) local type path: local attribute range match",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" } "
                            + "}"},
            {"340i) local type path: local attribute range !match",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" } "
                            + "}"},
            {"340j) local type path: local attribute range smatch",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" } "
                            + "}"},
            {"340k) local type path: local attribute range !smatch",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" } "
                            + "}"},
            {"340l) local type path: local attribute range program",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" } "
                            + "}"},
            {"340m) local type path: local attribute range program input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" } "
                            + "}"},
            {"340n) local type path: local attribute range between inclusive",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive } "
                            + "}"},
            {"340o) local type path: local attribute range between exclusive",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive } "
                            + "}"},

            // attribute property
            {"350a) local type path: local attribute property special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" } "
                            + "}"},
            {"350b) local type path: local attribute property and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" } "
                            + "}"},
            {"350c) local type path: local attribute property link special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" } "
                            + "}"},
            {"350d) local type path: local attribute property link and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" } "
                            + "}"},
        };
    }

    @Override()
    protected Type_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Type_mxJPO(_paramCache.getMapping().getTypeDef("Type"), _name);
    }
}

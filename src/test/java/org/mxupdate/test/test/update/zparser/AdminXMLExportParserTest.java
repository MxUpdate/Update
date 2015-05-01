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

package org.mxupdate.test.test.update.zparser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.zparser.AdminXMLExportObject_mxJPO;
import org.mxupdate.update.zparser.AdminXMLExportParser_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO {@link AdminXMLExportParser_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class AdminXMLExportParserTest
{
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]{
                {"multiple tags",               "<m><mx><cProps>\n<rel>RELEASE</rel>\n<dtime>2020-20-20</dtime>\n</cProps>\n</mx></m>",     new String[]{"", "", "/cProps", "", "/cProps/rel", "RELEASE", "/cProps/dtime", "2020-20-20"}},
                {"closing tag",                 "<m><mx>\n<myprog/>\n</mx></m>",                                                            new String[]{"", "", "/myprog", null}},
                {"multiline text",              "<m><mx>\n<code>multiline\ncode</code></mx></m>",                                           new String[]{"", "", "/code", "multiline\ncode"}},
                {"ignore xml version",          "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><m><mx></mx></m>",                              new String[]{"", ""}},
                {"ignore xml comment",          "<!-- Copyright (c) The MxUpdate Team --><m><mx><test/></mx></m>",                          new String[]{"", "", "/test", null}},
                {"ignore comment with DOCTYPE", "<!-- Copyright (c) The MxUpdate Team -->\n<!DOCTYPE><m><mx><test/></mx></m>",              new String[]{"", "", "/test", null}},
                {"cdata",                       "<m><mx><test><![CDATA[content]]></test></mx></m>",                                         new String[]{"", "", "/test", "content"}},
                {"mixed cdata + text",          "<m><mx><test>all <![CDATA[content]]> is <![CDATA[tested]]></test></mx></m>",               new String[]{"", "", "/test", "all content is tested"}},
                {"tag with attribute",          "<m><mx><test count=\"1\">test</test></mx></m>",                                            new String[]{"", "", "/test", "test"}},
                {"closing tag with attribute",  "<m><mx><test count=\"0\"/></mx></m>",                                                      new String[]{"", "", "/test", null}},
                {"dtdinfo",                     "<m><mx><dtdInfo>&ematrixProductDtd;</dtdInfo></mx></m>",                                   new String[]{"", "", "/dtdInfo", "&ematrixProductDtd;"}},
                {"text with apostrophes",       "<m><mx><dtdInfo>test 'test'</dtdInfo></mx></m>",                                           new String[]{"", "", "/dtdInfo", "test 'test'"}},
                {"text with apostrophes",       "<m><mx><dtdInfo>test \"test\"</dtdInfo></mx></m>",                                         new String[]{"", "", "/dtdInfo", "test \"test\""}},
                {"text with greater than",      "<m><mx><dtdInfo>class => java</dtdInfo></mx></m>",                                         new String[]{"", "", "/dtdInfo", "class => java"}},
                {"text with special character", "<m><mx><dtdInfo>class \u2019 java</dtdInfo></mx></m>",                                     new String[]{"", "", "/dtdInfo", "class \u2019 java"}},

        };
    }

    @Test(dataProvider = "data")
    public void positiveTest(final String _descr,
                             final String _code,
                             final Object[] _expected)
        throws Exception
    {
        final List<String> actual = new ArrayList<String>();

        new AdminXMLExportParser_mxJPO(new StringReader(_code)).parse(null, new AdminXMLExportObject_mxJPO()
        {
            @Override
            public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache, final String _url,
                    final String _content)
            {
                actual.add(_url);actual.add(_content);
                // TODO Auto-generated method stub
                return false;
            }

        });

        Assert.assertEquals(
                actual,
                Arrays.asList(_expected),
                "found:"+actual + " have:" + Arrays.asList(_expected));
    }
}

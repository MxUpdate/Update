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

package org.mxupdate.test.ci.program;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.update.program.AbstractCode_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the encoding of program exports from old MX versions.
 *
 * @author The MxUpdate Team
 */
public class TestOldXMLExportEncoding
{
    /**
     * Inserted text in the cod if the program includes a <code>CDATA</code>
     * (two closing squared brackets '&#93;&#93;').
     */
    private static final String INSERT_TEXT = "]Inserted_by_ENO" + "VIA]Inserted_by_ENO" + "VIA";

    /** Start tag of the code section. */
    private static final String CODE_START = "<code>";

    /** End tag of the code section. */
    private static final String CODE_END = "</code>";

    /** Start of the code with <code>CDATA</code> section. */
    private static final String CDATA_START = TestOldXMLExportEncoding.CODE_START + "<![CDATA[";

    /** End of the code with <code>CDATA</code> section. */
    private static final String CDATA_END = "]]>" + TestOldXMLExportEncoding.CODE_END;

    /**
     * Prepares test data.
     *
     * @return prepared test data
     */
    @DataProvider(name = "xmlExports")
    public Object[][] getXMLExports()
    {
        return new Object[][]{
                new Object[]{
                        TestOldXMLExportEncoding.CODE_START
                        + "\nTest code"
                        + TestOldXMLExportEncoding.CODE_END},
                new Object[]{
                        TestOldXMLExportEncoding.CDATA_START
                        + "\nTest code"
                        + TestOldXMLExportEncoding.CDATA_END},
                new Object[]{
                        TestOldXMLExportEncoding.CDATA_START
                        + "Test code" + TestOldXMLExportEncoding.INSERT_TEXT
                        + TestOldXMLExportEncoding.CDATA_END},
                new Object[]{
                        TestOldXMLExportEncoding.CDATA_START
                        + "Test code \n"
                        + TestOldXMLExportEncoding.INSERT_TEXT + "\n"
                        + "Test code \n"
                        + TestOldXMLExportEncoding.CDATA_END},
                };
    }

    /**
     * Tests the encoding of old MX versions in the case that the two closing
     * square brackets are not replaced.
     *
     * @param _xml      XML used to test the encoding
     */
    @Test(dataProvider = "xmlExports",
          description ="Tests the encoding for given XML string")
    public void testEncoding(final String _xml)
    {
        final TestCode test = new TestCode();

        final String xmlEncode = _xml.replaceAll(TestOldXMLExportEncoding.INSERT_TEXT, "]]");

        Assert.assertEquals(test.encode(xmlEncode), _xml);
    }

    /**
     * The class makes the
     * {@link AbstractCode_mxJPO#encodeXMLExport(String) encoding method} from
     * class {@link AbstractCode_mxJPO} visible, because otherwise the method
     * could not be tested from
     * {@link TestOldXMLExportEncoding#testEncoding(String) testEncoding(String)}.
     */
    private static final class TestCode
        extends AbstractCode_mxJPO<TestCode>
    {
        /**
         * Default constructor where all parameters are initialized with
         * <code>null</code>.
         */
        private TestCode()
        {
            super(null, null);
        }

        /**
         * Encodes given <code>_xml</code> string and returns the result.
         *
         * @param _xml  XML to encode
         * @return encoded XML string
         */
        private String encode(final String _xml)
        {
            return this.encodeXMLExport(_xml);
        }

        @Override()
        public void parseUpdate(final File _file,
                                final String _code)
            throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void calcDelta(final ParameterCache_mxJPO _paramCache, final MultiLineMqlBuilder _mql, final TestCode _current)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
        {
            // TODO Auto-generated method stub
        }
    }
}

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

package org.mxupdate.test.ci.program;

import org.mxupdate.update.program.AbstractCode_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
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

    /**
     * Start tag of the code section.
     */
    private static final String CODE_START = "<code>";

    /**
     * End tag of the code section.
     */
    private static final String CODE_END = "</code>";

    /**
     * Start of the code with <code>CDATA</code> section.
     */
    private static final String CDATA_START = TestOldXMLExportEncoding.CODE_START + "<![CDATA[";

    /**
     * End of the code with <code>CDATA</code> section.
     */
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
        extends AbstractCode_mxJPO
    {
        /**
         * Default serial ID (because class is not serialized).
         */
        private static final long serialVersionUID = 1L;

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

        /**
         * Only a stub method because not required for the test.
         *
         * @param _paramCache   parameter cache
         * @param _out          TCL update file
         */
        @Override()
        protected void writeObject(final ParameterCache_mxJPO _paramCache,
                                   final Appendable _out)
        {
        }
    }
}

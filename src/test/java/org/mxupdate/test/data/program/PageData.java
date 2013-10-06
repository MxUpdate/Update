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

package org.mxupdate.test.data.program;

import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.testng.Assert;

/**
 * The class is used to define all page program objects used to create / update
 * and to export.
 *
 * @author The MxUpdate Team
 */
public class PageData
    extends AbstractAdminData<PageData>
{
    /**
     * Within export the description and mime type must be defined.
     *
     * @see #PageData(AbstractTest, String)
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
    static  {
        PageData.REQUIRED_EXPORT_VALUES.put("description", "");
        PageData.REQUIRED_EXPORT_VALUES.put("mime", "");
    }

    /**
     * Separator used between the configuration item update code the the
     * page content itself.
     */
    private static final String PAGE_CONTENT_SEPARATOR
            = "# do not change the next three lines, they are needed as separator information:\n"
            + "################################################################################\n"
            + "# PAGE CONTENT                                                                 #\n"
            + "################################################################################";

    /**
     * Related code of this program.
     *
     * @see #setContent(String)
     * @see #getCode()
     */
    private String content = "";

    /**
     * Initializes this MQL program.
     *
     * @param _test     related test instance
     * @param _name     name of the MQL program
     */
    public PageData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.PRG_PAGE, _name, PageData.REQUIRED_EXPORT_VALUES, null);
    }

    /**
     * Defines the {@link #content} for this page.
     *
     * @param _content     new code of this page
     * @return this page instance
     * @see #content
     */
    public PageData setContent(final String _content)
    {
        this.content = _content;
        return this;
    }

    /**
     * Returns the {@link #content} of this page.
     *
     * @return related {@link #content} of this page
     * @see #content
     */
    public String getCode()
    {
        return this.content;
    }

    /**
     * Returns the symbolic name of a page object which must be always
     * <code>null</code>.
     *
     * @return always <code>null</code>
     */
    @Override()
    public String getSymbolicName()
    {
        return null;
    }

    /**
     * The related configuration item file is the {@link #content} of the page.
     *
     *  @return {@link #content} of the page
     *  @see #content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod page \"${NAME}\" file [file join \"${FILE}\"]");
        this.append4CIFileValues(cmd);

        // append embedded inquiry code
        if (this.content != null)  {
            cmd.append("\n\n").append(PageData.PAGE_CONTENT_SEPARATOR).append('\n')
               .append(this.content);
        }

        return cmd.toString();
    }

    /**
     * Creates this MQL program within MX.
     *
     * @return this MQL program instance
     * @throws MatrixException if create of the MQL program failed
     */
    @Override()
    public PageData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add page \"").append(AbstractTest.convertMql(this.getName())).append("\"");
            this.append4Create(cmd);
            // append content
            if (this.content != null)  {
                cmd.append(" content \"").append(AbstractTest.convertMql(this.content)).append("\"");
            }

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * Checks the export of this page all values and the embedded page content
     * are correct defined. The original method is overwritten to test the page
     * content.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check no symbolic name is defined
        Assert.assertTrue((_exportParser.getSymbolicName() == null),
                          "check that no symbolic name is defined");

        // check embedded inquiry code
        final PageExportParser exportParser = (PageExportParser) _exportParser;
        if (this.content == null)  {
            Assert.assertEquals(exportParser.pageContent,
                                "",
                                "check that no embedded page content is defined");
        } else  {
            Assert.assertEquals(exportParser.pageContent,
                                this.content,
                                "check that correct embedded page content is defined");
        }
    }

    /**
     * {@inheritDoc}
     * The original method is overwritten to use a specific export parser
     * implementation for pages.
     */
    @Override()
    protected ExportParser parseExport(final AbstractTest.CI _ci,
                                       final String _code,
                                       final String _log)
    {
        return new PageExportParser(_ci, _code, _log);
    }

    /**
     * The class overwrites the original export parser to handle the embedded
     * page content within configuration item update files.
     */
    private static class PageExportParser
        extends ExportParser
    {
        /**
         * Defines the embedded page content within the configuration item
         * update code.
         *
         * @see #extractUpdateCode(String)
         */
        private final String pageContent;

        /**
         * Initializes the inquiry export parser.
         *
         * @param _ci       type of the configuration item
         * @param _code     exported configuration item update code
         * @param _log      logging text from the server
         */
        public PageExportParser(final AbstractTest.CI _ci,
                                final String _code,
                                final String _log)
        {
            super(_ci, _code, _log);
            final int idx = _code.indexOf(PageData.PAGE_CONTENT_SEPARATOR);
            if (idx >= 0)  {
                this.pageContent = _code.substring(idx + PageData.PAGE_CONTENT_SEPARATOR.length()).trim();
            } else  {
                this.pageContent = "";
            }
        }

        /**
         * Extracts from the <code>_origCode</code> the update code without
         * header. Because the configuration item update code of an page
         * includes also the content of the page, this page content must be
         * removed from the original configuration item code so that the
         * update code could be extracted correct.
         *
         * @param _origCode     original code from which the update code must
         *                      be extracted
         * @return extracted update code (without header and page content)
         */
        @Override()
        protected String extractUpdateCode(final String _origCode)
        {
            final String origCode;
            final int idx = _origCode.indexOf(PageData.PAGE_CONTENT_SEPARATOR);
            if (idx >= 0)  {
                origCode = _origCode.substring(0, idx);
            } else  {
                origCode = _origCode;
            }
            return super.extractUpdateCode(origCode);
        }
    }
}

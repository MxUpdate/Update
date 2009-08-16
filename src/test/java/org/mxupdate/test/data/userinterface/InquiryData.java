/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.test.data.userinterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractData;
import org.testng.Assert;

/**
 * Used to define an inquiry, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class InquiryData
    extends AbstractData<InquiryData>
{
    /**
     * Within export the description, pattern, format and file must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
        InquiryData.REQUIRED_EXPORT_VALUES.add("description");
        InquiryData.REQUIRED_EXPORT_VALUES.add("pattern");
        InquiryData.REQUIRED_EXPORT_VALUES.add("format");
        InquiryData.REQUIRED_EXPORT_VALUES.add("file");
    }

    /**
     * Separator used between the configuration item update code the the
     * inquiry code itself.
     */
    private static final String INQUIRY_CODE_SEPARATOR
            = "# do not change the next three lines, they are needed as separator information:\n"
            + "################################################################################\n"
            + "# INQUIRY CODE                                                                 #\n"
            + "################################################################################";

    /**
     * Related code of this inquiry.
     */
    private String code;

    /**
     * All arguments of this inquiry.
     *
     * @see #setArgument(String, String)
     * @see #append4Create(StringBuilder)
     * @see #evalAdds4CheckExport(Set)
     */
    private final Map<String,String> arguments = new HashMap<String,String>();

    /**
     * Constructor to initialize this command.
     *
     * @param _test     related test implementation (where this inquiry is
     *                  defined)
     * @param _name     name of the inquiry
     */
    public InquiryData(final AbstractTest _test,
                       final String _name)
    {
        super(_test, AbstractTest.CI.INQUIRY, _name,
              "INQUIRY_", "userinterface/inquiry",
              InquiryData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Defines the {@link #code} for this inquiry.
     *
     * @param _code     new code of this inquiry
     * @return this inquiry instance
     * @see #code
     */
    public InquiryData setCode(final String _code)
    {
        this.code = _code;
        return this;
    }

    /**
     * Defines a new argument for this inquiry.
     *
     * @param _key      key of the argument
     * @param _value    value of the argument
     * @return this inquiry instance
     * @see #arguments
     */
    public InquiryData setArgument(final String _key,
                                   final String _value)
    {
        this.arguments.put(_key, _value);
        return this;
    }

    /**
     * Returns the content for the configuration item update file for this
     * inquiry instance including the embedded {@link #code inquiry code}.
     *
     * @return configuration item update file
     * @see #code
     */
    @Override
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod inquiry \"${NAME}\" file [file join \"${FILE}\"]");
        this.append4CIFileValues(cmd);

        // append embedded inquiry code
        cmd.append("\n\n").append(InquiryData.INQUIRY_CODE_SEPARATOR).append('\n');
        if (this.code != null)  {
            cmd.append(this.code);
        }

        return cmd.toString();
    }

    /**
     * Creates this inquiry with all values, {@link #arguments} and
     * {@link #code}.
     *
     * @return this inquiry instance
     * @throws MatrixException if create failed
     */
    @Override
    public InquiryData create() throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape add inquiry \"" + AbstractTest.convertMql(this.getName()) + "\"");
        this.append4Create(cmd);

        if (this.code != null)  {
            cmd.append(" code \"").append(AbstractTest.convertMql(this.code)).append("\"");
        }

        cmd.append(';');

        this.getTest().mql(cmd);

        return this;
    }

    /**
     * Appends to the MQL create commands in <code>_cmd</code> the
     * {@link #arguments}.
     *
     * @param _cmd      string builder used to append the MQL commands
     * @throws MatrixException if append failed
     * @see #arguments
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);
        for (final Map.Entry<String,String> argument : this.arguments.entrySet())  {
            _cmd.append(" argument \"").append(AbstractTest.convertMql(argument.getKey())).append("\" \"")
                .append(AbstractTest.convertMql(argument.getValue()))
                .append('\"');
        }
    }

    /**
     * Checks the export of this inquiry all values and the embedded inquiry
     * code are correct defined. The original method is overwritten to test
     * the inquiry code.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check embedded inquiry code
        final InquiryExportParser exportParser = (InquiryExportParser) _exportParser;
        if (this.code == null)  {
            Assert.assertEquals(exportParser.inquiryCode,
                                "",
                                "check that no embedded inquiry code is defined");
        } else  {
            Assert.assertEquals(exportParser.inquiryCode,
                    this.code,
                    "check that correct embedded inquiry code is defined");
        }
    }

    /**
     * Evaluates all 'adds' in the configuration item file defined from the
     * {@link #arguments} (<code>add argument</code>).
     *
     * @param _needAdds     set with add strings used to append the adds for
     *                      {@link #arguments}
     * @see #arguments
     */
    @Override
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);
        for (final Map.Entry<String,String> argument : this.arguments.entrySet())
        {
            _needAdds.add("argument \"" + AbstractTest.convertTcl(argument.getKey())
                    + "\" \"" + AbstractTest.convertTcl(argument.getValue()) +  "\"");
        }
    }

    /**
     * Returns the parsed export instance.
     *
     * @param _ci       related configuration item type
     * @param _code     code of the exported configuration item
     * @return parsed export instance
     */
    @Override()
    protected ExportParser parseExport(final AbstractTest.CI _ci,
                                       final String _code)
    {
        return new InquiryExportParser(_ci, _code);
    }

    /**
     * The class overwrites the original export parser to handle the embedded
     * inquiry code within configuration item update files.
     */
    private static class InquiryExportParser
        extends ExportParser
    {
        /**
         * Defines the embedded inquiry code within the configuration item
         * update code.
         *
         * @see #extractUpdateCode(String)
         */
        private final String inquiryCode;

        /**
         * Initializes the inquiry export parser.
         *
         * @param _ci       type of the configuration item
         * @param _code     exported configuration item update code
         */
        public InquiryExportParser(final AbstractTest.CI _ci,
                                   final String _code)
        {
            super(_ci, _code);
            final int idx = _code.indexOf(InquiryData.INQUIRY_CODE_SEPARATOR);
            if (idx >= 0)  {
                this.inquiryCode = _code.substring(idx + InquiryData.INQUIRY_CODE_SEPARATOR.length()).trim();
            } else  {
                this.inquiryCode = "";
            }
        }

        /**
         * Extracts from the <code>_origCode</code> the update code without
         * header. Because the configuration item update code of an inquiry
         * includes also the code of the inquiry, this code must be removed
         * from the original code so that the update code could be extracted
         * correct.
         *
         * @param _origCode     original code from which the update code must
         *                      be extracted
         * @return extracted update code (without header and inquiry code)
         */
        @Override
        protected String extractUpdateCode(final String _origCode)
        {
            final String origCode;
            final int idx = _origCode.indexOf(InquiryData.INQUIRY_CODE_SEPARATOR);
            if (idx >= 0)  {
                origCode = _origCode.substring(0, idx);
            } else  {
                origCode = _origCode;
            }
            return super.extractUpdateCode(origCode);
        }
    }
}

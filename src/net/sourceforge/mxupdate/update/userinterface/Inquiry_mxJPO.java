/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.userinterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;

import net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO;
import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@InfoAnno_mxJPO(adminType = "inquiry",
                title = "INQUIRY",
                filePrefix = "INQUIRY_",
                fileSuffix = ".tcl",
                filePath = "userinterface/inquiry",
                description = "inquiry")
public class Inquiry_mxJPO
       extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -6884861954912987897L;

    /**
     * Separator used between the inquiry update statements and the inquiry
     * code itself.
     */
    private final static String INQUIRY_SEPARATOR
        = "################################################################################\n"
        + "# INQUIRY CODE                                                                 #\n"
        + "################################################################################";


    /**
     * Code for the inquiry.
     */
    private String code = null;

    /**
     * Format for the inquiry.
     */
    private String format = null;

    /**
     * Pattern for the inquiry.
     */
    private String pattern = null;

    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/code".equals(_url))  {
            this.code = _content;
        } else if ("/fmt".equals(_url))  {
            this.format = _content;
        } else if ("/pattern".equals(_url))  {
            this.pattern = _content;
        } else  {
            super.parse(_url, _content);
        }
    }

    @Override
    protected void writeObject(Writer _out) throws IOException
    {
        _out.append(" \\\n    pattern \"").append(convertTcl(this.pattern)).append("\"")
            .append(" \\\n    format \"").append(convertTcl(this.format)).append("\"")
            .append(" \\\n    file \"${FILE}\"");
        for (final AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _out.append(" \\\n    add argument \"").append(convertTcl(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(convertTcl(prop.getValue())).append("\"");
            }
        }
   }

    @Override
    protected void writeEnd(final Writer _out)
            throws IOException
    {
        _out.append("\n\n# do not change the next three lines, they are needed as separator information:\n")
            .append(INQUIRY_SEPARATOR)
            .append("\n\n").append(this.code);
    }

    /**
     * Updates this inquiry. Because the TCL source code of an inquiry includes
     * also the inquiry code itself, this inquiry code must be separated and
     * written in a temporary file. This temporary file is used while the
     * update is running (defined via TCL variable <code>FILE</code>). After
     * the update, the temporary file is removed (because not needed anymore).
     * Also the MQL statements to reset this inquiry are appended to the
     * statements in <code>_preMQLCode</code> to:
     * <ul>
     * <li>reset the description, pattern and code</li>
     * <li>remove all arguments</li>
     * </ul>
     *
     * @param _context          context for this request
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _tclCode          TCL code from the file used to update
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @throws Exception if update failed
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        // reset HRef, description, alt, label and height
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(getInfoAnno().adminType())
                .append(" \"").append(getName()).append('\"')
                .append(" description \"\" pattern \"\" code \"\"");

        // reset arguments
        for (final AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                preMQLCode.append(" remove argument \"").append(prop.getName().substring(1)).append('\"');
            }
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // separate the inquiry code and the TCL code
        final int idx = _tclCode.toString().lastIndexOf(INQUIRY_SEPARATOR);
        final CharSequence code = (idx >= 0)
                                  ? _tclCode.subSequence(0, idx)
                                  : _tclCode;
        final CharSequence inqu = (idx >= 0)
                                  ? _tclCode.subSequence(idx + INQUIRY_SEPARATOR.length() + 1, _tclCode.length())
                                  : "";

        final File tmpFile = File.createTempFile("TMP_", ".inquiry");

        try  {
            // write inquiry code
            final Writer out = new FileWriter(tmpFile);
            out.append(inqu.toString().trim());
            out.flush();
            out.close();

            // define TCL variable for the file
            final Map<String,String> tclVariables = new HashMap<String,String>();
            tclVariables.putAll(_tclVariables);
            tclVariables.put("FILE", tmpFile.getPath());

            // and update
            super.update(_context, preMQLCode, _postMQLCode, code, tclVariables);
        } finally  {
            tmpFile.delete();
        }
    }
}

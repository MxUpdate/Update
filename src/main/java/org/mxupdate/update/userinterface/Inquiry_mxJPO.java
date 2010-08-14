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

package org.mxupdate.update.userinterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export and import / update inquiry configuration items.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Inquiry_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the parameter for the comment in front of the separator between
     * TCL update code and the inquiry code.
     *
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_SEPARATOR_COMMENT = "UIInquirySeparatorComment";

    /**
     * Defines the parameter for the separator between TCL update code and the
     * inquiry.
     *
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_SEPARATOR_TEXT = "UIInquirySeparatorText";

    /**
     * Code for the inquiry.
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private String code;

    /**
     * Format for the inquiry.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String format;

    /**
     * Pattern for the inquiry.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String pattern;

    /**
     * Constructor used to initialize the inquiry instance.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Inquiry_mxJPO(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses the {@link #code}, {@@ink #format} and {@link #pattern} of an
     * inquiry.
     *
     * @param _url      URL to parse
     * @param _content  related content of the URL to parse
     * @see #code
     * @see #format
     * @see #pattern
     */
    @Override()
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

    /**
     * Writes the inquiry specific information to the TCL update write. This
     * includes
     * <ul>
     * <li>hidden flag (only if hidden)</li>
     * <li>{@link #pattern}</li>
     * <li>{@link #format}</li>
     * <li>all arguments (properties starting with &quot;%&quot;)</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the menu could not be
     *                     written
     * @see #pattern
     * @see #format
     * @see #getPropertiesMap()
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        if (this.isHidden())  {
            _out.append(" \\\n    hidden");
        }
        _out.append(" \\\n    pattern \"").append(StringUtil_mxJPO.convertTcl(this.pattern)).append("\"")
            .append(" \\\n    format \"").append(StringUtil_mxJPO.convertTcl(this.format)).append("\"")
            .append(" \\\n    file [file join \"${FILE}\"]");
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
                _out.append(" \\\n    add argument \"")
                    .append(StringUtil_mxJPO.convertTcl(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getValue())).append("\"");
            }
        }
    }

    /**
     * At the end of the TCL update file the inquiry code must be appended.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     * @see #code
     * @see #PARAM_SEPARATOR_COMMENT
     * @see #PARAM_SEPARATOR_TEXT
     */
    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
        _out.append("\n\n").append(_paramCache.getValueString(Inquiry_mxJPO.PARAM_SEPARATOR_COMMENT))
            .append('\n').append(_paramCache.getValueString(Inquiry_mxJPO.PARAM_SEPARATOR_TEXT)).append("\n\n");
        if (this.code != null)  {
            _out.append(this.code);
        }
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
     * <li>reset the description, {@link #pattern}, {@link #format} and
     *     {@link #code}</li>
     * <li>set to not hidden</li>
     * <li>remove all arguments</li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     * @see #PARAM_SEPARATOR_TEXT
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        // reset HRef, description, alt, label and height
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" !hidden description \"\" pattern \"\" format \"\" code \"\"");

        // reset arguments
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
                preMQLCode.append(" remove argument \"")
                          .append(StringUtil_mxJPO.convertMql(prop.getName().substring(1))).append('\"');
            }
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // separate the inquiry code and the TCL code
        final String sep = _paramCache.getValueString(Inquiry_mxJPO.PARAM_SEPARATOR_TEXT);
        final StringBuilder orgCode = this.getCode(_sourceFile);
        final int idx = orgCode.lastIndexOf(sep);
        final CharSequence code = (idx >= 0)
                                  ? orgCode.subSequence(0, idx)
                                  : orgCode;
        final CharSequence inqu = (idx >= 0)
                                  ? orgCode.subSequence(idx + sep.length() + 1,
                                                        orgCode.length())
                                  : "";

        final File tmpTclFile = File.createTempFile("TMP_", ".tcl");
        try  {
            final File tmpInqFile = File.createTempFile("TMP_", ".inquiry");
            try  {
                // write TCL code
                final Writer outTCL = new FileWriter(tmpTclFile);
                try {
                    outTCL.append(code.toString().trim());
                } finally  {
                    outTCL.close();
                }

                // write inquiry code
                final Writer outInq = new FileWriter(tmpInqFile);
                try {
                    outInq.append(inqu.toString().trim());
                } finally  {
                    outInq.close();
                }

                // define TCL variable for the file
                final Map<String,String> tclVariables = new HashMap<String,String>();
                tclVariables.putAll(_tclVariables);
                tclVariables.put("FILE", tmpInqFile.getPath());

                // and update
                super.update(_paramCache, preMQLCode, _postMQLCode, code, tclVariables, tmpTclFile);
            } finally  {
                tmpInqFile.delete();
            }
        } finally  {
            tmpTclFile.delete();
        }
    }
}

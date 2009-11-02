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

package org.mxupdate.update.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update pages within MX.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Page_mxJPO
    extends AbstractCode_mxJPO
{
    /**
     * Defines the parameter for the comment in front of the separator between
     * TCL update code and the page content.
     *
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_SEPARATOR_COMMENT = "ProgramPageSeparatorComment";

    /**
     * Defines the parameter for the separator between TCL update code and the
     * page content.
     *
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_SEPARATOR_TEXT = "ProgramPageSeparatorText";

    /**
     * Related mime-type of this page.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String mimeType;

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef      defines the related type definition enumeration
     * @param _mxName       MX name of the page object
     */
    public Page_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Pages could not handle symbolic names. Therefore the original method
     * must be overwritten so that nothing happens.
     *
     * @param _paramCache   parameter cache
     */
    @Override()
    protected void readSymbolicNames(final ParameterCache_mxJPO _paramCache)
    {
    }

    /**
     * At the end of the update of the page the append statements to define
     * symbolic names are not allowed because pages could not handle symbolic
     * names.
     *
     * @param _paramCache   parameter cache - ignored
     * @param _symbName     symbolic name which must be set - ignored
     * @param _mqlCode      string builder where the MQL command must be
     *                      appended - ignored
     */
    @Override()
    protected void appendSymbolicNameRegistration(final ParameterCache_mxJPO _paramCache,
                                                  final String _symbName,
                                                  final StringBuilder _mqlCode)
    {
    }

    /**
     * Because no symbolic names are allowed for pages (not supported from MX),
     * <code>null</code> must be always returned.
     *
     * @return allways <code>null</code>
     */
    @Override()
    protected Set<String> getSymbolicNames()
    {
        return null;
    }

    /**
     * <p>Parses all page specific URL values. This includes:
     * <ul>
     * <li>{@link #mimeType mime type}</li>
     * </ul></p>
     *
     * @param _url      URL to parse
     * @param _content  content depending on the URL
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/mimeType".equals(_url))  {
            this.mimeType = _content;
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes the page specific information to the TCL update write. This
     * includes:
     * <ul>
     * <li>{@link #mimeType mime type}</li>
     * <li>file definition for the {@link #getCode() page content}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the menu could not be
     *                     written
     * @see #getCode()
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        _out.append(" \\\n    mime \"").append((this.mimeType == null) ? "" : StringUtil_mxJPO.convertTcl(this.mimeType)).append("\"")
            .append(" \\\n    file [file join \"${FILE}\"]");
    }

    /**
     * At the end of the TCL update file the {@link #getCode() page content}
     * must be appended.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     * @see #getCode()
     * @see #PARAM_SEPARATOR_COMMENT
     * @see #PARAM_SEPARATOR_TEXT
     */
    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
        _out.append("\n\n").append(_paramCache.getValueString(Page_mxJPO.PARAM_SEPARATOR_COMMENT))
            .append('\n').append(_paramCache.getValueString(Page_mxJPO.PARAM_SEPARATOR_TEXT)).append("\n\n");
        if (this.getCode() != null)  {
            _out.append(this.getCode());
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to update this page program. Following
     * steps are done within update:
     * <ul>
     * <li>The description is set to zero length string.</li>
     * <li>The mime type is reset.</li>
     * <li>The page content is removed.</li>
     * <li>The page content is extracted from the <code>_sourceFile</code> and
     *     the path to the file set as TCL variable <code>FILE</code>.
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
                .append(" description \"\" mime \"\" content \"\"");

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // separate the page content content and the TCL update code
        final String sep = _paramCache.getValueString(Page_mxJPO.PARAM_SEPARATOR_TEXT);
        final StringBuilder orgCode = this.getCode(_sourceFile);
        final int idx = orgCode.lastIndexOf(sep);
        final CharSequence code = (idx >= 0)
                                  ? orgCode.subSequence(0, idx)
                                  : orgCode;
        final CharSequence page = (idx >= 0)
                                  ? orgCode.subSequence(idx + sep.length() + 1, orgCode.length())
                                  : "";

        final File tmpPageFile = File.createTempFile("TMP_", ".page");
        try  {
            final File tmpTclFile = File.createTempFile("TMP_", ".tcl");
            try  {
                // write TCL update code
                final Writer outTCL = new FileWriter(tmpTclFile);
                try  {
                    outTCL.append(code.toString().trim());
                } finally  {
                    outTCL.close();
                }

                // write page content
                final Writer outPage = new FileWriter(tmpPageFile);
                try  {
                    outPage.append(page.toString().trim());
                } finally {
                    outPage.close();
                }

                // define TCL variable for the file
                final Map<String,String> tclVariables = new HashMap<String,String>(_tclVariables);
                tclVariables.put("FILE", tmpPageFile.getPath());

                // and update
                super.update(_paramCache, preMQLCode, _postMQLCode, code, tclVariables, tmpTclFile);
            } finally  {
                tmpTclFile.delete();
            }
        } finally  {
            tmpPageFile.delete();
        }
    }
}

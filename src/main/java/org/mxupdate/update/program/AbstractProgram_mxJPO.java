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

package org.mxupdate.update.program;

import java.io.IOException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Common definition for the code of a program.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractProgram_mxJPO
    extends AbstractCode_mxJPO
{
    /** User in which context the MQL program is executed. */
    private String user;

    /** Execution of the program is deferred. */
    private boolean deferred = false;

    /** The program needs context of a business object. */
    private boolean needsBusinessObjectContext;

    /** Program is downloadable. */
    private boolean downloadable = false;

    /** Program uses pipes. */
    private boolean pipe = false;

    /** Program is pooled (used for TCL). */
    private boolean pooled = false;

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef  type definition of the program
     * @param _mxName   MX name of the program object
     */
    protected AbstractProgram_mxJPO(final TypeDef_mxJPO _typeDef,
                                    final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * <p>Parses all program specific URL values. This includes:
     * <ul>
     * <li>{@link #deferred} execution</li>
     * <li>execute {@link #user}</li>
     * <li>{@link #needsBusinessObjectContext needs context} of a business
     *      object</li>
     * <li>program is {@link #downloadable}</li>
     * <li>input / output of the program is {@link #pipe piped}</li>
     * <li>program is {@link #pooled}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content depending on the URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if ("/deferred".equals(_url))  {
            this.deferred = true;
            parsed = true;
        } else if ("/downloadable".equals(_url) || "/usesInterface".equals(_url))  {
            this.downloadable = true;
            parsed = true;
        } else if ("/mqlPipe".equals(_url))  {
            this.pipe = true;
            parsed = true;
        } else if ("/needsContext".equals(_url))  {
            this.needsBusinessObjectContext = true;
            parsed = true;
        } else if ("/pooled".equals(_url))  {
            this.pooled = true;
            parsed = true;
        } else if ("/userRef".equals(_url))  {
            this.user = _content;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Must be implemented so that the class could be derived. The method is
     * only a stub implementation.
     *
     * @param _paramCache   parameter cache; ignored
     * @param _out          ignored
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
    {
    }

    /**
     * Appends the TCL update code to <code>_out</code> for this MQL program.
     * This includes:
     * <ul>
     * <li>{@link #deferred} execution</li>
     * <li>execute {@link #user}</li>
     * <li>{@link #getDescription() description}</li>
     * <li>{@link #isHidden() hidden flag}</li>
     * <li>{@link #needsBusinessObjectContext needs context} of a business
     *      object</li>
     * <li>program is {@link #downloadable}</li>
     * <li>input / output of the program is {@link #pipe piped}</li>
     * <li>program is {@link #pooled}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          update file (where the MQL program with the TCL
     *                      update file is written)
     * @param _markStart    start marker of the TCL update
     * @param _markEnd      end marker of the TCL update
     * @param _linePrefix   line prefix (before each TCL update line)
     * @throws IOException if the TCL code could not be written to the file
     */
    protected void writeUpdateCode(final ParameterCache_mxJPO _paramCache,
                                   final Appendable _out,
                                   final String _markStart,
                                   final String _markEnd,
                                   final String _linePrefix)
        throws IOException
    {
        final StringBuilder cmd = new StringBuilder();
        if (this.deferred || this.needsBusinessObjectContext
                || this.downloadable || this.pipe || this.pooled
                || (this.user != null)
                || ((this.getDescription() != null) && !"".equals(this.getDescription()))
                || this.isHidden())  {
            cmd.append("\nmql mod program \"${NAME}\"");
            if (this.deferred)  {
                cmd.append(" \\\n    execute deferred");
            }
            if (this.user != null)  {
                cmd.append(" \\\n    execute user \"").append(StringUtil_mxJPO.convertTcl(this.user)).append('\"');
            }
            if ((this.getDescription() != null) && !"".equals(this.getDescription()))  {
                cmd.append(" \\\n    description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append('\"');
            }
            if (this.isHidden())  {
                cmd.append(" \\\n    hidden");
            }
            if (this.needsBusinessObjectContext)  {
                cmd.append(" \\\n    needsbusinessobject");
            }
            if (this.downloadable)  {
                cmd.append(" \\\n    downloadable");
            }
            if (this.pipe)  {
                cmd.append(" \\\n    pipe");
            }
            if (this.pooled)  {
                cmd.append(" \\\n    pooled");
            }
        }

        this.getProperties().writeAddFormat(_paramCache, _out, this.getTypeDef());

        if (!"".equals(cmd.toString()))  {
            _out.append(_markStart.trim()).append('\n')
                .append(this.makeLinePrefix(_linePrefix, cmd));
            if (_linePrefix != null)  {
                _out.append(_linePrefix);
            }
            _out.append('\n').append(_markEnd.trim()).append("\n\n");
        }
    }

    /**
     * For each line in the <code>_lines</code> text (separated by new line)
     * the <code>_linePrefix</code> is added.
     *
     * @param _linePrefix   prefix for the lines; or <code>null</code> if no
     *                      line prefix is defined
     * @param _lines        complete lines as text with new lines
     * @return new lines with prefixes
     */
    protected String makeLinePrefix(final String _linePrefix,
                                    final CharSequence _lines)
    {
        final StringBuilder ret = new StringBuilder();

        if ((_linePrefix != null) && !"".equals(_linePrefix))  {
            for (final String line : _lines.toString().split("\n"))  {
                ret.append(_linePrefix).append(line).append('\n');
            }
        } else  {
            ret.append(_lines);
        }

        return ret.toString();
    }

    /**
     * Extracts from <code>_prgCode</code> the TCL update code which is between
     * <code>_markStart</code> and <code>_markEnd</code> defined. Each line of
     * the code starts with <code>_linePrefix</code>. Only if
     * <code>_execute</code> is set, the found TCL code is returned.
     *
     * @param _paramCache   parameter cache
     * @param _tclCode      TCL code string builder to append the TCL update
     *                      code which was extracted and if
     *                      <code>_execute</code> is <i>true</i>
     * @param _execute      <i>true</i> if TCL update code will be executed
     * @param _prgCode      program code
     * @param _markStart    start marker
     * @param _markEnd      end marker
     * @param _linePrefix   line prefix
     * @return source code without TCL update code; <code>null</code> if source
     *         includes no TCL update code
     */
    protected String extractTclUpdateCode(final ParameterCache_mxJPO _paramCache,
                                          final StringBuilder _tclCode,
                                          final boolean _execute,
                                          final StringBuilder _prgCode,
                                          final String _markStart,
                                          final String _markEnd,
                                          final String _linePrefix)
    {
        final String ret;

        final int start = _prgCode.indexOf(_markStart);
        final int end = _prgCode.indexOf(_markEnd);
        if ((start >= 0) && (end > 0))  {
            final String tclCode = _prgCode.substring(start + _markStart.length(), end).trim();
            if (!"".equals(tclCode))  {
                // TCL code must be executed only if allowed
                // and line prefix is defined
                if (_execute)  {
                    _paramCache.logTrace("    - TCL update code is executed");
                    // remove line prefixes from TCL code (if defined)
                    final int linePrefixLength = (_linePrefix != null) ? _linePrefix.length() : -1;
                    if (linePrefixLength > 0)  {
                        final StringBuilder tclUpdateCode = new StringBuilder();
                        for (final String line : tclCode.split("\n"))  {
                            tclUpdateCode.append(line.substring(linePrefixLength)).append('\n');
                        }
                        _tclCode.append(tclUpdateCode.toString());
                    } else  {
                        _tclCode.append(tclCode);
                    }
                } else  {
                    _paramCache.logError("    - Warning! Existing TCL update code is not executed!");
                }
            }
            ret = new StringBuilder()
                    .append(_prgCode.substring(0, start))
                    .append('\n')
                    .append(_prgCode.substring(end + _markEnd.length()))
                    .toString();
        } else  {
            ret = null;
        }
        return ret;
    }
}

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

package org.mxupdate.update.program;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MqlBuilder;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.CacheKey;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;

/**
 * Common definition for the code of a program.
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
public abstract class AbstractProgram_mxJPO<CLASS extends AbstractCode_mxJPO<CLASS>>
    extends AbstractCode_mxJPO<CLASS>
{
    /** Key used for the select statement. */
    private static final String SELECT_KEY = "@@@2@@@2@@@";

    /** Program kind. */
    private Kind kind = null;

    /** User in which context the MQL program is executed. */
    private String user;

    /** The program needs context of a business object. */
    private boolean needsBusinessObjectContext;

    /** Program is downloadable. */
    private boolean downloadable = false;

    /** Program uses pipes. */
    private boolean pipe = false;

    /** Program is pooled (used for TCL). */
    private boolean pooled = false;

    /** When is the program executed? */
    private Execute execute = Execute.IMMEDIATE;

    /** Rule. */
    private String rule;

    /** File with code to update. */
    private String file;

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _kind     program kind
     * @param _typeDef  type definition of the program
     * @param _mxName   MX name of the program object
     */
    protected AbstractProgram_mxJPO(final Kind _kind,
                                    final TypeDef_mxJPO _typeDef,
                                    final String _mxName)
    {
        super(_typeDef, _mxName);
        this.kind = _kind;
    }


    /**
     * Searches for all programs objects depending on the program {@link Kind}.
     *
     * @param _paramCache   parameter cache
     * @return set of MX names of all programs of kind {@link #kind}
     * @throws MatrixException if the query for ∏rogram objects failed
     */
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        @SuppressWarnings("unchecked")
        Map<Kind,Set<String>> progs = (Map<Kind,Set<String>>) _paramCache.getCache(CacheKey.Programs);

        if (progs == null)  {
            // prepare MQL statement
            final MqlBuilder mql = MqlBuilder_mxJPO.mql()
                        .cmd("escape list program ").arg("*")
                                .cmd(" select ").arg("name");
            for (final Kind kind : Kind.values())  {
                mql.cmd(" ").arg(kind.select);
            }
            mql.cmd(" dump ").arg(AbstractProgram_mxJPO.SELECT_KEY);

            // prepare list of programs
            progs = new HashMap<Kind,Set<String>>();
            for (final Kind tmpKind : Kind.values())  {
                progs.put(tmpKind, new HashSet<String>());
            }

            // evaluate list of programs
            for (final String lineStr : mql.exec(_paramCache).split("\n"))  {
                final String[] lineArr = lineStr.split(AbstractProgram_mxJPO.SELECT_KEY);
                int idx = 1;
                Kind lineKind = null;
                for (final Kind tmpKind : Kind.values())  {
                    if ("TRUE".equalsIgnoreCase(lineArr[idx++]))  {
                        lineKind = tmpKind;
                        break;
                    }
                }
                if (lineKind != null)  {
                    progs.get(lineKind).add(lineArr[0]);
                }
            }
            _paramCache.setCache(CacheKey.Programs, progs);
        }

        return progs.get(this.kind);
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new ProgramParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
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
        if ("/accessRuleRef".equals(_url))  {
            this.rule = _content;
            parsed = true;
        } else if ("/deferred".equals(_url))  {
            this.execute = Execute.DEFERRED;
            parsed = true;
        } else if ("/downloadable".equals(_url) || "/usesInterface".equals(_url))  {
            this.downloadable = true;
            parsed = true;
        } else if ("/mqlPipe".equals(_url))  {
            this.pipe = true;
            parsed = true;
        } else if ("/mqlProgram".equals(_url))  {
            this.kind = Kind.MQL;
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
    @Deprecated()
    protected void writeUpdateCode(final ParameterCache_mxJPO _paramCache,
                                   final Appendable _out,
                                   final String _markStart,
                                   final String _markEnd,
                                   final String _linePrefix)
        throws IOException
    {
        final StringBuilder cmd = new StringBuilder();
        if ((this.execute == Execute.DEFERRED) || this.needsBusinessObjectContext
                || this.downloadable || this.pipe || this.pooled
                || (this.user != null)
                || ((this.getDescription() != null) && !"".equals(this.getDescription()))
                || this.isHidden())  {
            cmd.append("\nmql mod program \"${NAME}\"");
            if (this.execute == Execute.DEFERRED)  {
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
    @Deprecated()
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

    @Override()
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag                 | default | value                                | write?
                .single(        "kind",                         this.kind.name().toLowerCase())
                .list(          "symbolicname",                 this.getSymbolicNames())
                .string(        "description",                  this.getDescription())
                .flagIfTrue(    "hidden",               false,  this.isHidden(),                        this.isHidden())
                .flagIfTrue(    "needsbusinessobject",  false,  this.needsBusinessObjectContext,        this.needsBusinessObjectContext)
                .flagIfTrue(    "downloadable",         false,  this.downloadable,                      this.downloadable)
                .flagIfTrue(    "pipe",                 false,  this.pipe,                              this.pipe)
                .flagIfTrue(    "pooled",               false,  this.pooled,                            this.pooled)
                .stringIfTrue(  "rule",                         this.rule,                              (this.rule != null) && !this.rule.isEmpty())
                .singleIfTrue(  "execute",                      this.execute.name().toLowerCase(),      (this.execute != Execute.IMMEDIATE))
                .stringIfTrue(  "execute user",                 this.user,                              (this.user != null) && !this.user.isEmpty())
                .properties(this.getProperties())
                .codeIfTrue(    "code",                         this.getCode(),                         (this.getCode() != null) && !this.getCode().isEmpty());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final CLASS _current)
    {
        final AbstractProgram_mxJPO<?> current = (AbstractProgram_mxJPO<?>) _current;

        // execute must be defined before downloadable...
        if (this.execute != current.execute)  {
            _mql.newLine().cmd("execute ").cmd(this.execute.name().toLowerCase());
        }

        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this.getTypeDef(), this.getName(), this.getSymbolicNames(), current.getSymbolicNames());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",                 this.getDescription(),              current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",              false,  this.isHidden(),                    current.isHidden());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "needsbusinessobject", false,  this.needsBusinessObjectContext,    current.needsBusinessObjectContext);
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "downloadable",        false,  this.downloadable,                  current.downloadable);
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "pipe",                false,  this.pipe,                          current.pipe);
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "pooled",              false,  this.pooled,                        current.pooled);

        if ((this.file != null) && !this.file.isEmpty())  {
            // code via file
            final String tmpFile;
            // absolute path?
            if (this.file.startsWith("/"))  {
                tmpFile = this.file;
            } else  {
                tmpFile = _mql.getFile().getParent() + "/" + this.file;
            }
            _mql.newLine().cmd("file ").arg(tmpFile);
        } else  {
            // code via code
            DeltaUtil_mxJPO.calcValueDelta(_mql, "code",                        this.getCode(),                     current.getCode());
        }

        // rule
        if (CompareToUtil_mxJPO.compare(0, this.rule, current.rule) != 0)  {
            if ((current.rule != null) && !current.rule.isEmpty())  {
                _mql.newLine().cmd("remove rule ").arg(current.rule);
            }
            if ((this.rule != null) && !this.rule.isEmpty())  {
                _mql.newLine().cmd("add rule ").arg(this.rule);
            }
        }

        // execute user
        if (CompareToUtil_mxJPO.compare(0, this.user, current.user) != 0)  {
            _mql.newLine().cmd("execute user ").arg(this.user);
        }

        this.getProperties().calcDelta(_mql, "", current.getProperties());
    }

    /** Enumeration for programs. */
    enum Kind
    {
        /** Java program. */
        JAVA("isjavaprogram"),
        /** MQL program. */
        MQL("ismqlprogram");

        /** MQL select statement. */
        private final String select;

        /**
         * Initialize the MQL select statement.
         *
         * @param _select   select statement
         */
        private Kind(final String _select)
        {
            this.select = _select;
        }
    }

    /** */
    enum Execute
    {

        /** */
        IMMEDIATE,
        /** */
        DEFERRED;
    }
}

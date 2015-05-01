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

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
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
     * The flag indicates that the back slashes are converted. In older MX
     * versions double back slashes was escaped. In this cases all escaped
     * double back slashes must be replaced. In newer MX versions this
     * 'feature' does not exists anymore if an MQL insert was done.
     */
    private boolean backslashUpgraded = false;

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
     * <li>{@link #backslashUpgraded back slash flag} to indicate the a JPO is
     *     upgraded</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content depending on the URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if ("/accessRuleRef".equals(_url))  {
            this.rule = _content;
            parsed = true;
        } else if ("/backslashUpgraded".equals(_url))  {
            this.backslashUpgraded = true;
            parsed = true;
        } else if ("/deferred".equals(_url))  {
            this.execute = Execute.DEFERRED;
            parsed = true;
        } else if ("/downloadable".equals(_url) || "/usesInterface".equals(_url))  {
            this.downloadable = true;
            parsed = true;
        } else if ("/eklProgram".equals(_url))  {
            this.kind = Kind.EKL;
            parsed = true;
        } else if ("/javaProgram".equals(_url))  {
            this.kind = Kind.JAVA;
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
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
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
                .codeIfTrue(    "code",                         this.getCode(),                         (this.kind != Kind.JAVA) && (this.getCode() != null) && !this.getCode().isEmpty())
                .stringIfTrue(  "file",     this.getName().replaceAll("\\.", "/") + "_" + "mxJPO.java", (this.kind == Kind.JAVA) && (this.getCode() != null) && !this.getCode().isEmpty());
    }

    /**
     *
     * @param _paramCache   parameter cache
     */
    public boolean hasNoValuesDefined(final ParameterCache_mxJPO _paramCache)
    {
        return     this.getSymbolicNames().isEmpty()
                && ((this.getDescription() == null) || this.getDescription().isEmpty())
                && !this.isHidden()
                && !this.needsBusinessObjectContext
                && !this.downloadable
                && !this.pipe
                && !this.pooled
                && ((this.rule == null) || this.rule.isEmpty())
                && (this.execute == Execute.IMMEDIATE)
                && ((this.user == null) || this.user.isEmpty())
                && this.getProperties().hasNoValuesDefined(_paramCache);
    }

    /**
     * Creates given program object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if create of JPO failed
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        MqlBuilder_mxJPO.mql().cmd("escape add ").cmd(this.getTypeDef().getMxAdminName()).cmd(" ").arg(this.getName()).cmd(" ").cmd(this.kind.name().toLowerCase()).exec(_paramCache);
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

    /**
     * Returns if backslashes for JPOs are upgraded.
     *
     * @return <i>true</i> if backslashes are upgraded for JPOs; otherwise
     *         <i>false</i>
     */
    public boolean isBackslashUpgraded()
    {
        return this.backslashUpgraded;
    }

    /** Enumeration for programs. */
    enum Kind
    {
        /** MQL program. */
        EKL,
        /** Java program. */
        JAVA,
        /** MQL program. */
        MQL;
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

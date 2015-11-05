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

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.FileUtils_mxJPO;
import org.mxupdate.util.JPOUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.util.StringUtils_mxJPO;

import matrix.util.MatrixException;

/**
 * Common definition for the code of a program. The handled properties are
 * <ul>
 * <li>package</li>
 * <li>{@link #kind} of program</li>
 * <li>uuid</li>
 * <li>symbolic names</li>
 * <li>description</li>
 * <li>{@link AbstractCode_mxJPO#getCode() content}</li>
 * <li>{@link #user}</li>
 * <li>{@link #needsBusinessObjectContext}</li>
 * <li>{@link #downloadable}</li>
 * <li>{@link #pipe}</li>
 * <li>{@link #pooled}</li>
 * <li>{@link #execute}</li>
 * <li>{@link #rule}</li>
 * </ul>
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
public class ProgramCI_mxJPO
    extends AbstractCode_mxJPO<ProgramCI_mxJPO>
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
     * @param _mxName   MX name of the program object
     */
    public ProgramCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Program, _mxName);
    }

    /**
     * Parses a program and sets the default {@link #kind} value to
     * {@link Kind#EXTERNAL}.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if XML export fails
     * @throws ParseException if parsing fails
     */
    @Override
    public void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, ParseException
    {
        super.parse(_paramCache);

        if (this.kind == null)  {
            this.kind = Kind.EXTERNAL;
        }
    }

    @Override
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
    @Override
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

    @Override
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                                | write?
                .stringNotNull( "package",                  this.getPackageRef())
                .single(        "kind",                     this.kind.name().toLowerCase())
                .stringNotNull( "uuid",                     this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flagIfTrue(    "hidden",           false,  this.isHidden(),                        this.isHidden())
                .flagIfTrue("needsbusinessobject",  false,  this.needsBusinessObjectContext,        this.needsBusinessObjectContext)
                .flagIfTrue(    "downloadable",     false,  this.downloadable,                      this.downloadable)
                .flagIfTrue(    "pipe",             false,  this.pipe,                              this.pipe)
                .flagIfTrue(    "pooled",           false,  this.pooled,                            this.pooled)
                .stringIfTrue(  "rule",                     this.rule,                              (this.rule != null) && !this.rule.isEmpty())
                .singleIfTrue(  "execute",                  this.execute.name().toLowerCase(),      (this.execute != Execute.IMMEDIATE))
                .stringIfTrue(  "execute user",             this.user,                              (this.user != null) && !this.user.isEmpty())
                .properties(this.getProperties())
                .codeIfTrue(    "code",                     this.getCode(),                         (this.kind != Kind.JAVA) && (this.getCode() != null) && !this.getCode().isEmpty())
                .stringIfTrue(  "file",     this.getName().replaceAll("\\.", "/") + "_" + "mxJPO.java", (this.kind == Kind.JAVA) && (this.getCode() != null) && !this.getCode().isEmpty());
    }

    /**
     *
     * @param _paramCache   parameter cache
     */
    public boolean hasNoValuesDefined(final ParameterCache_mxJPO _paramCache)
    {
        return     StringUtils_mxJPO.isEmpty(this.getPackageRef())
                && this.getSymbolicNames().isEmpty()
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

    @Override
    public void createOld(final ParameterCache_mxJPO _paramCache)
    {
    }

    /**
     * Creates given program object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if create of JPO failed
     */
    @Override
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        MqlBuilderUtil_mxJPO.mql().cmd("escape add program ").arg(this.getName()).cmd(" ").cmd(this.kind.name().toLowerCase()).exec(_paramCache.getContext());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final ProgramCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        boolean update = true;

        // evaluate file incl. path (if a file in the CI is defined)
        final File realFile = FileUtils_mxJPO.calcFile(_mql.getFile(), this.getFile());

        // check file date
        if (_paramCache.getValueBoolean(ValueKeys.UpdateCheckFileDate) && (realFile != null))  {
            final String fileDate = this.getProperties().getValue4KeyValue(_paramCache, PropertyDef_mxJPO.FILEDATE)
                                    + " ___ "
                                    + StringUtil_mxJPO.formatFileDate(_paramCache, new Date(realFile.lastModified()));
            this.getProperties().setValue4KeyValue(_paramCache, PropertyDef_mxJPO.FILEDATE, fileDate);

            update = !fileDate.equals(_current.getProperties().getValue4KeyValue(_paramCache, PropertyDef_mxJPO.FILEDATE));
        }

        if (update)  {
            // at first => check that kind is correct set
            if (this.kind != _current.kind)  {
                _mql.newLine().cmd(" ").cmd(this.kind.name().toLowerCase());
            }

            // execute must be defined before downloadable...
            if (this.execute != _current.execute)  {
                _mql.newLine().cmd("execute ").cmd(this.execute.name().toLowerCase());
            }

            DeltaUtil_mxJPO.calcPackage(_paramCache, _mql, this, _current);
            DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
            DeltaUtil_mxJPO.calcValueDelta(_mql, "description",                 this.getDescription(),              _current.getDescription());
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",              false,  this.isHidden(),                    _current.isHidden());
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "needsbusinessobject", false,  this.needsBusinessObjectContext,    _current.needsBusinessObjectContext);
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "downloadable",        false,  this.downloadable,                  _current.downloadable);
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "pipe",                false,  this.pipe,                          _current.pipe);
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "pooled",              false,  this.pooled,                        _current.pooled);

            final String newCode;
            if (!StringUtils_mxJPO.isEmpty(this.getFile()))  {
                // code via file
                switch (this.kind)  {
                    case JAVA:
                        newCode = JPOUtil_mxJPO.convertJavaToJPOCode(_current.backslashUpgraded, this.getName(), FileUtils_mxJPO.readFileToString(realFile));
                        break;
                    default:
                        newCode = FileUtils_mxJPO.readFileToString(realFile);
                        break;
                }
            } else  {
                // code via code
                newCode = this.getCode();
            }

            DeltaUtil_mxJPO.calcValueDelta(_mql, "code", newCode, _current.getCode());

            // rule
            if (CompareToUtil_mxJPO.compare(0, this.rule, _current.rule) != 0)  {
                if ((_current.rule != null) && !_current.rule.isEmpty())  {
                    _mql.newLine().cmd("remove rule ").arg(_current.rule);
                }
                if ((this.rule != null) && !this.rule.isEmpty())  {
                    _mql.newLine().cmd("add rule ").arg(this.rule);
                }
            }

            // execute user
            if (CompareToUtil_mxJPO.compare(0, this.user, _current.user) != 0)  {
                _mql.newLine().cmd("execute user ").arg(this.user);
            }

            this.getProperties().calcDelta(_mql, "", _current.getProperties());
        }
    }

    /**
     * Compile this program if the program is a JPO.
     *
     * @param _paramCache   parameter cache
     * @return always <i>true</i>
     * @throws Exception if the compile of the JPO failed
     */
    @Override
    public boolean compile(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        if (this.kind == Kind.JAVA)  {
            MqlBuilderUtil_mxJPO.mql().cmd("escape compile prog ").arg(this.getName()).exec(_paramCache.getContext());
        }
        return true;
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
        /** EKL program. */
        EKL,
        /** External program. */
        EXTERNAL,
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

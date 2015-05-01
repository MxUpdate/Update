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

package org.mxupdate.update.datamodel;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Handles the export and the update of the format configuration item.
 * The handled properties are:
 * <ul>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #mimeType mime type}</li>
 * <li>{@link #fileSuffix file suffix}</li>
 * <li>{@link #type}</li>
 * <li>{@link #version}</li>
 * <li>{@link #commandView view command} (legacy, only if parameter
 *     {@link ValueKeys#DMFormatSupportsPrograms} is true)</li>
 * <li>{@link #commandEdit edit command} (legacy, only if parameter
 *     {@link ValueKeys#DMFormatSupportsPrograms} is true)</li>
 * <li>{@link #commandPrint print command} (legacy, only if parameter
 *     {@link ValueKeys#DMFormatSupportsPrograms} is true)</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Format_mxJPO
    extends AbstractAdminObject_mxJPO<Format_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for formats. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        // to be ignored, because identically to fileType
        Format_mxJPO.IGNORED_URLS.add("/fileCreator");
    }

    /** Reference to the edit program. */
    private String commandEdit = null;
    /** Reference to the print program. */
    private String commandPrint = null;
    /** Reference to the view program. */
    private String commandView = null;

    /** Mime type of the format. */
    private String mimeType = "";
    /** File suffix of the format. */
    private String fileSuffix = "";

    /** Type and creator of the format (used only for MacOS, creator is always equal type!). */
    private String type = "";

    /** Version of the format. */
    private String version = "";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the format object
     */
    public Format_mxJPO(final TypeDef_mxJPO _typeDef,
                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new FormatParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * Parses all format specific URLs.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (Format_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/editCommand".equals(_url))  {
            this.commandEdit = _content;
            parsed = true;
        } else if ("/printCommand".equals(_url))  {
            this.commandPrint = _content;
            parsed = true;
        } else if ("/viewCommand".equals(_url))  {
            this.commandView = _content;
            parsed = true;

        } else if ("/fileSuffix".equals(_url))  {
            this.fileSuffix = _content;
            parsed = true;
        } else if ("/fileType".equals(_url))  {
            this.type = _content;
            parsed = true;
        } else if ("/mimeType".equals(_url))  {
            this.mimeType = _content;
            parsed = true;

        } else if ("/version".equals(_url))  {
            this.version = _content;
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
                //              tag             | default | value                              | write?
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flag(          "hidden",           false,  this.isHidden())
                .string(        "mime",                     this.mimeType)
                .string(        "suffix",                   this.fileSuffix)
                .string(        "type",                     this.type)
                .string(        "version",                  this.version)
                .stringIfTrue(  "view",                     this.commandView,                   _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMFormatSupportsPrograms))
                .stringIfTrue(  "edit",                     this.commandEdit,                   _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMFormatSupportsPrograms))
                .stringIfTrue(  "print",                    this.commandPrint,                  _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMFormatSupportsPrograms))
                .properties(this.getProperties());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Format_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this.getTypeDef(), this.getName(), this.getSymbolicNames(), _current.getSymbolicNames());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",             this.getDescription(), _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",          false,  this.isHidden(),       _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "mime",                    this.mimeType,         _current.mimeType);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "suffix",                  this.fileSuffix,       _current.fileSuffix);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "type",                    this.type,             _current.type);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "version",                 this.version,          _current.version);

        if (_paramCache.getValueBoolean(ValueKeys.DMFormatSupportsPrograms))  {
            DeltaUtil_mxJPO.calcValueDelta(_mql, "view", this.commandView, _current.commandView);
            DeltaUtil_mxJPO.calcValueDelta(_mql, "edit", this.commandEdit, _current.commandEdit);
            DeltaUtil_mxJPO.calcValueDelta(_mql, "print", this.commandPrint, _current.commandPrint);
        } else  {
            if (this.commandView != null)  {
                _paramCache.logInfo("    - view program " + this.commandView + " ignored (not supported anymore!)");
            }
            if (this.commandEdit != null)  {
                _paramCache.logInfo("    - edit program " + this.commandEdit + " ignored (not supported anymore!)");
            }
            if (this.commandPrint != null)  {
                _paramCache.logInfo("    - print program " + this.commandPrint + " ignored (not supported anymore!)");
            }
        }

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

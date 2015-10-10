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

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export, create, delete and update pages within MX.
 * <ul>
 * <li>description</li>
 * <li>hidden flag (only if hidden)</li>
 * <li>{@link AbstractCode_mxJPO#getCode() content}</li>
 * <li>{@link #mimeType mime type}</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Page_mxJPO
    extends AbstractCode_mxJPO<Page_mxJPO>
{
    /** Related mime-type of this page. */
    private String mimeType = "";

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

    @Override()
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new PageParser_mxJPO(new StringReader(_code)).parse(_file, this);
        this.prepare();
    }

    /**
     * <p>Parses all page specific URL values. This includes:
     * <ul>
     * <li>{@link #mimeType mime type}</li>
     * <li>{@link #content}</li>
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
        if ("/mimeType".equals(_url))  {
            this.mimeType = _content;
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
                //              tag             | default | value                              | write?
                .list(          "symbolicname",            this.getSymbolicNames())
                .string(        "description",             this.getDescription())
                .flagIfTrue(    "hidden",           false, this.isHidden(),                     this.isHidden())
                .string(        "mime",                    this.mimeType)
                .properties(this.getProperties())
                .codeIfTrue(    "content",                 this.getCode(),                      (this.getCode() != null) && !this.getCode().isEmpty());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Page_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),                      _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),                            _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "mime",                this.mimeType,                              _current.mimeType);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "content",             this.getCode(),                             _current.getCode());

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

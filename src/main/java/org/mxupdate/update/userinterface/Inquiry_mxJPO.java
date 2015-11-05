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

package org.mxupdate.update.userinterface;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * The class is used to export and import / update inquiry configuration items.
 * The handled properties are:
 * <ul>
 * <li>package</li>
 * <li>uuid</li>
 * <li>symbolic names</li>
 * <li>description</li>
 * <li>hidden flag (only if hidden)</li>
 * <li>{@link #pattern}</li>
 * <li>{@link #format}</li>
 * <li>all arguments (properties starting with &quot;%&quot;)</li>
 * <li>{@link #code}</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Inquiry_mxJPO
    extends AbstractAdminObject_mxJPO<Inquiry_mxJPO>
{
    /** Code for the inquiry. */
    private String code;

    /** Format for the inquiry. */
    private String format;

    /** Pattern for the inquiry. */
    private String pattern;

    /**
     * Constructor used to initialize the inquiry instance.
     *
     * @param _mxName   MX name of the administration object
     */
    public Inquiry_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Inquiry, _mxName);
        this.getProperties().setOtherPropTag("argument");
    }

    @Override
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new InquiryParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if ("/code".equals(_url))  {
            this.code = _content;
            parsed = true;
        } else if ("/fmt".equals(_url))  {
            this.format = _content;
            parsed = true;
        } else if ("/pattern".equals(_url))  {
            this.pattern = _content;
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
                .stringNotNull( "package",                  this.getPackageRef())
                .stringNotNull( "uuid",                     this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flagIfTrue(    "hidden",           false,  this.isHidden(),                     this.isHidden())
                .string(        "pattern",                  this.pattern)
                .string(        "format",                   this.format)
                .otherProps(this.getProperties())
                .properties(this.getProperties())
                .string(        "code",                     "\n" + ((this.code != null) ? this.code : "") + "\n");
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final Inquiry_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcPackage(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),                          _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),                                _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "pattern",             this.pattern,                                   _current.pattern);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "format",              this.format,                                    _current.format);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "code",                (this.code == null) ? "" : this.code.trim(),    _current.code);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

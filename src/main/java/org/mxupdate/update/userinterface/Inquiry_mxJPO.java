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

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export and import / update inquiry configuration items.
 * The handled properties are:
 * <ul>
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
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Inquiry_mxJPO(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
    {
        super(_typeDef, _mxName);
        this.getProperties().setOtherPropTag("argument");
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new InquiryParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
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
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO(_paramCache);

        this.writeHeader(_paramCache, updateBuilder.getStrg());

        updateBuilder
                .start("inquiry")
                //              tag             | default | value                              | write?
                .string(        "description",             this.getDescription())
                .flagIfTrue(    "hidden",           false, this.isHidden(),                     this.isHidden())
                .string(        "pattern",                 this.pattern)
                .string(        "format",                  this.format)
                .otherProps(this.getProperties())
                .properties(this.getProperties())
                .string(        "code",                    "\n" + ((this.code != null) ? this.code : "") + "\n")
                .end();

        _out.append(updateBuilder.toString());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Inquiry_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),                      _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),                            _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "pattern",             this.pattern,                               _current.pattern);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "format",              this.format,                                _current.format);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "code",                this.code,                                  _current.code);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

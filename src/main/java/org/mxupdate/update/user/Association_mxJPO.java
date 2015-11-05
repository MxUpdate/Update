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

package org.mxupdate.update.user;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

import matrix.util.MatrixException;

/**
 * The class is used to export and import / update association configuration
 * items.
 * The handled properties are:
 * <ul>
 * <li>package</li>
 * <li>uuid</li>
 * <li>symbolic names</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #definition}</li>
 * <li>properties</li>
 *
 * @author The MxUpdate Team
 */
public class Association_mxJPO
    extends AbstractAdminObject_mxJPO<Association_mxJPO>
{
    /** Stores the definition of this association instance. */
    private String definition = "";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _mxName   MX name of the association object
     */
    public Association_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Association, _mxName);
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new AssociationParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * Parses all association specific URLs.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if ("/definition".equals(_url))  {
            this.definition = _content;
            parsed = true;

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Returns the stored file date within Matrix for administration object
     * with given name. The original method is overwritten, because a select
     * statement of a &quot;print&quot; command does not work.
     *
     * @param _paramCache   parameter cache
     * @param _prop         property for which the date value is searched
     * @return modified date of given update object
     * @throws MatrixException if the MQL print failed
     */
    @Override
    public String getPropValue(final ParameterCache_mxJPO _paramCache,
                               final PropertyDef_mxJPO _prop)
        throws MatrixException
    {
        final String text = _prop.getPropName(_paramCache) + " on association " + this.getName() + " value ";
        final String curValue = MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
                .append("list property on asso \"").append(this.getName()).append("\""));

        final int idx = curValue.indexOf(text);
        final String value;
        if (idx >= 0)  {
            final int last = curValue.indexOf('\n', idx);
            if (last > 0)  {
                value = curValue.substring(idx + text.length(), last);
            } else  {
                value = curValue.substring(idx + text.length());
            }
        } else  {
            value = null;
        }

        return value;
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
                .flag(          "hidden",            false, this.isHidden())
                .string(        "definition",               this.definition)
                .properties(this.getProperties());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final Association_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcPackage(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(     _mql, "description",        this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(      _mql, "hidden",      false, this.isHidden(),        _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(     _mql, "definition",         this.definition,        _current.definition);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

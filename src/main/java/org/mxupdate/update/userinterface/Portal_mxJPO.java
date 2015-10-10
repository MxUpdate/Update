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

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.userinterface.helper.ChildRefList_mxJPO;
import org.mxupdate.update.userinterface.helper.ChildRefList_mxJPO.WriteAppendChildSyntax;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class parses the information about the portal and writes the script used
 * to update portals. The handles properties are
 * <ul>
 * <li>hidden flag (only if hidden)</li>
 * <li>{@link #label}</li>
 * <li>{@link #href}</li>
 * <li>{@link #alt}</li>
 * <li>settings defined as properties starting with &quot;%&quot; in
 *     {@link #getPropertiesMap()}</li>
 * <li>channel references {@link #children}</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Portal_mxJPO
    extends AbstractCommand_mxJPO<Portal_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for portals. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        Portal_mxJPO.IGNORED_URLS.add("/channelRefList");
    }

    /** All referenced children. */
    private final ChildRefList_mxJPO children = new ChildRefList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _mxName   MX name of the administration object
     */
    public Portal_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Portal, _mxName);
        this.getProperties().setOtherPropTag("setting");
   }

    @Override()
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new PortalParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * Parses the {@link #alt}, {@link #href}, {@link #label} and the channel
     * reference {@link #children}.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          url of the XML tag
     * @param _content      content of the XML tag
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (Portal_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;

        } else if (_url.startsWith("/channelRefList"))  {
            parsed = this.children.parse(_url.substring(15), _content);

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Order the channel references.
     */
    @Override()
    protected void prepare()
    {
        this.children.prepare();

        super.prepare();
    }

    @Override
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flagIfTrue(    "hidden",           false,  this.isHidden(),                     this.isHidden())
                .string(        "label",                    this.getLabel())
                .string(        "href",                     this.getHref())
                .string(        "alt",                      this.getAlt())
                .otherProps(this.getProperties())
                .write(this.children)
                .properties(this.getProperties());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final Portal_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),   _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),         _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "alt",                 this.getAlt(),           _current.getAlt());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "href",                this.getHref(),          _current.getHref());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "label",               this.getLabel(),         _current.getLabel());

        this.children.calcDelta(_mql, WriteAppendChildSyntax.PlaceWithNewRow, _current.children);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

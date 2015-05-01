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
import java.util.HashSet;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.userinterface.helper.ChildRefList_mxJPO;
import org.mxupdate.update.userinterface.helper.ChildRefList_mxJPO.WriteAppendChildSyntax;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export and import / update channel configuration items.
 *
 * @author The MxUpdate Team
 */
public class Channel_mxJPO
    extends AbstractCommand_mxJPO<Channel_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for channels. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Channel_mxJPO.IGNORED_URLS.add("/commandRefList");
    }

    /** Height of the channel. */
    private int height;

    /** All referenced children. */
    private final ChildRefList_mxJPO children = new ChildRefList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Channel_mxJPO(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new ChannelParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * Parses all channel specific values. This includes:
     * <ul>
     * <li>command references in {@link #commandRefs}</li>
     * <li>{@link #height}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      related content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Channel_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if (_url.startsWith("/commandRefList"))  {
            parsed = this.children.parse(_url.substring(15), _content);

        } else if ("/height".equals(_url))  {
            this.height = Integer.parseInt(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Order the command references.
     */
    @Override()
    protected void prepare()
    {
        this.children.prepare();

        super.prepare();
    }

    /**
     * Writes specific information about the cached channel to the given writer
     * instance. This includes
     * <ul>
     * <li>hidden flag (only if hidden)</li>
     * <li>{@link #label}</li>
     * <li>{@link #href}</li>
     * <li>{@link #alt}</li>
     * <li>{@link #height}</li>
     * <li>settings defined as properties starting with &quot;%&quot; in
     *     {@link #getPropertiesMap()}</li>
     * <li>command references {@link #children}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the channel could not be
     *                     written
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.writeHeader(_paramCache, _out);

        _out.append("mxUpdate channel \"${NAME}\"  {\n")
            .append("    description \"").append(StringUtil_mxJPO.convertUpdate(this.getDescription())).append("\"\n");
        if (this.isHidden())  {
            _out.append("    hidden\n");
        }
        _out.append("    label \"").append(StringUtil_mxJPO.convertUpdate(this.getLabel())).append("\"\n")
            .append("    alt \"").append(StringUtil_mxJPO.convertUpdate(this.getAlt())).append("\"\n");
        if ((this.getHref() != null) && !this.getHref().isEmpty())  {
            _out.append("    href \"").append(StringUtil_mxJPO.convertUpdate(this.getHref())).append("\"\n");
        }
        _out.append("    height ").append(String.valueOf(this.height)).append("\n");

        this.getProperties().writeSettings(_paramCache, _out, "    ");

        this.children.write(_out);

        this.getProperties().writeProperties(_paramCache, _out, "    ");

        _out.append("}");
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Channel_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),       _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),             _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "label",               this.getLabel(),             _current.getLabel());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "alt",                 this.getAlt(),               _current.getAlt());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "href",                this.getHref(),              _current.getHref());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "height",              String.valueOf(this.height), String.valueOf(_current.height));

        this.children       .calcDelta(_mql, WriteAppendChildSyntax.Place, _current.children);
        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

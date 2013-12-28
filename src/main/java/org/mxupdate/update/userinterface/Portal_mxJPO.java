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
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class parses the information about the portal and writes the script used
 * to update portals.
 *
 * @author The MxUpdate Team
 */
public class Portal_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for portals.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Portal_mxJPO.IGNORED_URLS.add("/channelRefList");
    }

    /**
     * Alt (label) of the portal.
     *
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String alt = null;

    /**
     * Href of the portal.
     *
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String href = null;

    /**
     * Label of the portal.
     *
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String label = null;

    /**
     * Stack used to parse the channel references.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     */
    private final Stack<ChannelRef> channelRefs = new Stack<ChannelRef>();

    /**
     * Ordered channel references by row and column.
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    final Map<Integer,Map<Integer,ChannelRef>> orderedChannelRefs = new TreeMap<Integer,Map<Integer,ChannelRef>>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Portal_mxJPO(final TypeDef_mxJPO _typeDef,
                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses the {@link #alt}, {@link #href}, {@link #label} and the channel
     * reference {@link #channelRefs}.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          url of the XML tag
     * @param _content      content of the XML tag
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Portal_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/alt".equals(_url))  {
            this.alt = _content;
            parsed = true;
        } else if ("/href".equals(_url))  {
            this.href = _content;
            parsed = true;
        } else if ("/label".equals(_url))  {
            this.label = _content;
            parsed = true;

        } else if ("/channelRefList/channelRef".equals(_url))  {
            this.channelRefs.add(new ChannelRef());
            parsed = true;
        } else if ("/channelRefList/channelRef/name".equals(_url))  {
            this.channelRefs.peek().name = _content;
            parsed = true;
        } else if ("/channelRefList/channelRef/portalRow".equals(_url))  {
            this.channelRefs.peek().row = Integer.parseInt(_content);
            parsed = true;
        } else if ("/channelRefList/channelRef/portalColumn".equals(_url))  {
            this.channelRefs.peek().column = Integer.parseInt(_content);
            parsed = true;

        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Order the channel references.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the preparation from derived class failed
     * @see #channelRefs        stack of not ordered channel references
     * @see #orderedChannelRefs ordered channel references
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // sort the channels by row and column
        for (final ChannelRef channelRef : this.channelRefs)  {
            Map<Integer,ChannelRef> sub = this.orderedChannelRefs.get(channelRef.row);
            if (sub == null)  {
                sub = new TreeMap<Integer,ChannelRef>();
                this.orderedChannelRefs.put(channelRef.row, sub);
            }
            sub.put(channelRef.column, channelRef);
        }

        super.prepare(_paramCache);
    }

    /**
     * Writes specific information about the cached portal to the given writer
     * instance. This includes
     * <ul>
     * <li>hidden flag (only if hidden)</li>
     * <li>{@link #label}</li>
     * <li>{@link #href}</li>
     * <li>{@link #alt}</li>
     * <li>settings defined as properties starting with &quot;%&quot; in
     *     {@link #getPropertiesMap()}</li>
     * <li>channel references {@link #orderedChannelRefs}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance to the TCL update file
     * @throws IOException if the TCL update code could not be written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        if (this.isHidden())  {
            _out.append(" \\\n    hidden");
        }
        _out.append(" \\\n    label \"").append(StringUtil_mxJPO.convertTcl(this.label)).append("\"");
        if (this.href != null)  {
            _out.append(" \\\n    href \"").append(StringUtil_mxJPO.convertTcl(this.href)).append("\"");
        }
        if (this.alt != null)  {
            _out.append(" \\\n    alt \"").append(StringUtil_mxJPO.convertTcl(this.alt)).append("\"");
        }
        // settings
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
                _out.append(" \\\n    add setting \"")
                    .append(StringUtil_mxJPO.convertTcl(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getValue())).append("\"");
            }
        }
        // channel references
        boolean firstRow = true;
        for (final Map<Integer,ChannelRef> channelRefs : this.orderedChannelRefs.values())  {
            boolean firstCol = true;
            for (final ChannelRef channelRef : channelRefs.values())  {
                _out.append(" \\\n    place \"").append(StringUtil_mxJPO.convertTcl(channelRef.name)).append("\"");
                if (!firstRow && firstCol)  {
                    _out.append(" newrow");
                }
                _out.append(" after \"\"");
                firstCol = false;
            }
            firstRow = false;
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this portal. Following steps
     * are done:
     * <ul>
     * <li>reset hidden flag, {@link #href}, description, {@link #alt} and
     *     {@link #label}</li>
     * <li>remove all settings and channels</li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        // HRef, description, alt and label
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" !hidden href \"\" description \"\" alt \"\" label \"\"");

        // reset settings
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
                preMQLCode.append(" remove setting \"").append(StringUtil_mxJPO.convertMql(prop.getName().substring(1))).append('\"');
            }
        }
        preMQLCode.append(";\n");

        // remove channels (each channel must be removed in a single line...)
        for (final ChannelRef channelRef : this.channelRefs)  {
            preMQLCode.append("escape mod ").append(this.getTypeDef().getMxAdminName())
                      .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                      .append(" remove channel \"").append(StringUtil_mxJPO.convertMql(channelRef.name)).append("\";\n");
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Class holding the channel reference.
     */
    private class ChannelRef
    {
        /**
         * Name of the channel.
         */
        String name = null;

        /**
         * Row of the channel.
         */
        Integer row = null;

        /**
         * Column of the channel.
         */
        Integer column = null;
    }
}

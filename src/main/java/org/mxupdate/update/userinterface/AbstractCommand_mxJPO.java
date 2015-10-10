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

import java.util.HashSet;
import java.util.Set;

import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The class is used to export and import / update command configuration items.
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
abstract class AbstractCommand_mxJPO<CLASS extends AbstractAdminObject_mxJPO<CLASS>>
    extends AbstractAdminObject_mxJPO<CLASS>
{
    /** Set of all ignored URLs from the XML definition for commands. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        AbstractCommand_mxJPO.IGNORED_URLS.add("/input");
    }

    /** Alt label of the command. */
    private String alt = "";
    /** Label of the command. */
    private String label = "";
    /** HRef of the command. */
    private String href = "";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _mxClassDef   MX class definition
     * @param _mxName       MX name of the administration object
     */
    public AbstractCommand_mxJPO(final EMxAdmin_mxJPO _mxClassDef,
                                 final String _mxName)
    {
        super(_mxClassDef, _mxName);
    }

    /**
     * Parses all command specific values. This includes:
     * <ul>
     * <li>{@link #alt}</li>
     * <li>{@link #href}</li>
     * <li>{@link #label}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      related content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (AbstractCommand_mxJPO.IGNORED_URLS.contains(_url))  {
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
        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Returns the {@link #alt} label of the command.
     *
     * @return alt label
     */
    protected String getAlt()
    {
        return this.alt;
    }

    /**
     * Returns the {@link #label} of the command.
     *
     * @return label
     */
    protected String getLabel()
    {
        return this.label;
    }

    /**
     * Returns the {@link #href} of the command.
     *
     * @return href
     */
    protected String getHref()
    {
        return this.href;
    }
}

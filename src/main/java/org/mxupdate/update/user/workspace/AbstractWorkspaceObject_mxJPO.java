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

package org.mxupdate.update.user.workspace;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO.AdminProperty;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store common information of a workspace object.
 *
 * @author The MxUpdate Team
 */
abstract class AbstractWorkspaceObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for common stuff of
     * users.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractWorkspaceObject_mxJPO.IGNORED_URLS.add("/creationInfo");
        AbstractWorkspaceObject_mxJPO.IGNORED_URLS.add("/creationInfo/datetime");
        AbstractWorkspaceObject_mxJPO.IGNORED_URLS.add("/modificationInfo");
        AbstractWorkspaceObject_mxJPO.IGNORED_URLS.add("/modificationInfo/datetime");
        AbstractWorkspaceObject_mxJPO.IGNORED_URLS.add("/propertyList");
        AbstractWorkspaceObject_mxJPO.IGNORED_URLS.add("/visibleUserList");
    }

    /** Related user of this workspace object. */
    private final AbstractUser_mxJPO user;

    /** MX administration type. */
    private final String mxAdminType;

    /** Name of the cue. */
    private String name;

    /** Is the workspace object hidden? */
    private boolean hidden = false;

    /** Specifies other existing users who can read the workspace item with MQL list, print  and evaluate commands. */
    private final Set<String> visibleFor = new TreeSet<String>();

    /** Admin Properties. */
    private final AdminPropertyList_mxJPO properties = new AdminPropertyList_mxJPO();

    /**
     * Default constructor to initialize the
     * {@link #mxAdminType MX administration type}.
     *
     * @param _user         related user for this workspace object
     * @param _mxAdminType  administration type of the workspace object
     */
    AbstractWorkspaceObject_mxJPO(final AbstractUser_mxJPO _user,
                                  final String _mxAdminType)
    {
        this.user = _user;
        this.mxAdminType = _mxAdminType;
    }

    /**
     * Returns the {@link #user} of the workspace object.
     *
     * @return user of the workspace object
     */
    protected AbstractUser_mxJPO getUser()
    {
        return this.user;
    }

    /**
     * Returns the {@link #name} of the workspace object.
     *
     * @return name of the workspace object
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns all defined {@link #properties} of this workspace object.
     *
     * @return defined properties
     */
    protected AdminPropertyList_mxJPO getProperties()
    {
        return this.properties;
    }

    /**
     * <p>Parses all common workspace object specific URL values. This
     * includes:
     * <ul>
     * <li>{@link #name}</li>
     * <li>{@link #hidden hidden flag}</li>
     * <li>{@link #visibleFor user for which this workspace object is visible}
     *     </li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if (AbstractWorkspaceObject_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/hidden".equals(_url))  {
            this.hidden = true;
            parsed = true;

        } else if ("/name".equals(_url))  {
            this.name = _content;
            parsed = true;

        } else if (_url.startsWith("/propertyList"))  {
            parsed = this.properties.parse(_paramCache, _url.substring(13), _content);

        } else if ("/visibleUserList/userRef".equals(_url))  {
            this.visibleFor.add(_content);
            parsed = true;
        } else  {
            parsed = false;
        }
        return parsed;
    }

    /**
     * Sorted the {@link #propertiesStack properties}.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException of preparation failed
     * @see #propertiesStack
     * @see #propertiesSet
     */
    public void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        this.properties.prepare();
    }

    /**
     * <p>Writes all common workspace object values and the create of the
     * workspace object itself to the TCL update file <code>_out</code>. This
     * includes:
     * <ul>
     * <li>{@link #name}</li>
     * <li>{@link #hidden hidden flag}</li>
     * <li>{@link #visibleFor user for which this workspace object is visible}
     *     </li>
     * <li>{@link #propertiesSet properties}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
        throws IOException
    {
        _out.append("\nmql escape add ").append(this.mxAdminType).append(" \"")
            .append(StringUtil_mxJPO.convertTcl(this.name))
            .append("\" \\\n    user \"${NAME}\"")
            .append(" \\\n    ").append(this.hidden ? "hidden" : "!hidden");
        for (final String user : this.visibleFor)  {
            _out.append(" \\\n    visible \"").append(StringUtil_mxJPO.convertTcl(user)).append("\"");
        }
        for (final AdminProperty prop : this.properties)  {
            if (!prop.isSetting())  {
                _out.append(" \\\n    property \"").append(StringUtil_mxJPO.convertTcl(prop.getName())).append("\"");
                if ((prop.getRefAdminName() != null) && (prop.getRefAdminType() != null))  {
                    _out.append(" to ").append(prop.getRefAdminType())
                        .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getRefAdminName())).append("\"");
                }
                if (prop.getValue() != null)  {
                    _out.append(" value \"").append(StringUtil_mxJPO.convertTcl(prop.getValue())).append("\"");
                }
            }
        }
    }
}

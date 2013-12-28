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

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store common information of a workspace object with
 * query functionality.
 *
 * @author The MxUpdate Team
 */
abstract class AbstractQueryWorkspaceObject_mxJPO
    extends AbstractWorkspaceObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for common stuff of
     * users.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractQueryWorkspaceObject_mxJPO.IGNORED_URLS.add("/queryStatement");
    }

    /**
     * Type pattern.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternType;

    /**
     * Name pattern.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternName;

    /**
     * Revision pattern.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternRevision;

    /**
     * Vault pattern.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternVault;

    /**
     * Owner pattern.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternOwner;

    /**
     * Where clause.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String whereClause;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     * @param _mxAdminType  administration type of the query workspace object
     */
    AbstractQueryWorkspaceObject_mxJPO(final AbstractUser_mxJPO _user,
                                       final String _mxAdminType)
    {
        super(_user, _mxAdminType);
    }

    /**
     * <p>Parses all common query workspace object specific URL values. This
     * includes:
     * <ul>
     * <li>{@link #patternName pattern for the name}</li>
     * <li>{@link #patternOwner pattern for the owner}</li>
     * <li>{@link #patternRevision pattern for the revision}</li>
     * <li>{@link #patternType pattern for the type}</li>
     * <li>{@link #patternVault pattern for the vault}</li>
     * <li>{@link #whereClause where clause}</li>
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
    @Override()
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if (AbstractQueryWorkspaceObject_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/queryStatement/namePattern".equals(_url))  {
            this.patternName = _content;
            parsed = true;
        } else if ("/queryStatement/ownerPattern".equals(_url))  {
            this.patternOwner = _content;
            parsed = true;
        } else if ("/queryStatement/revisionPattern".equals(_url))  {
            this.patternRevision = _content;
            parsed = true;
        } else if ("/queryStatement/typePattern".equals(_url))  {
            this.patternType = _content;
            parsed = true;
        } else if ("/queryStatement/vaultPattern".equals(_url))  {
            this.patternVault = _content;
            parsed = true;
        } else if ("/queryStatement/whereClause".equals(_url))  {
            this.whereClause = _content;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * <p>Writes all common query workspace object specific values to the TCL
     * update file <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #patternName pattern for the name}</li>
     * <li>{@link #patternOwner pattern for the owner}</li>
     * <li>{@link #patternRevision pattern for the revision}</li>
     * <li>{@link #patternType pattern for the type}</li>
     * <li>{@link #patternVault pattern for the vault}</li>
     * <li>{@link #whereClause where clause}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    @Override()
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
    throws IOException
    {
        super.write(_paramCache, _out);
        this.writeTypeNameRevision(_out, this.patternType, this.patternName, this.patternRevision);
        _out.append(" \\\n    vault \"").append(StringUtil_mxJPO.convertTcl(this.patternVault)).append("\"")
            .append(" \\\n    owner \"").append(StringUtil_mxJPO.convertTcl(this.patternOwner)).append("\"");
        if (this.whereClause != null)  {
            _out.append(" \\\n    where \"").append(StringUtil_mxJPO.convertTcl(this.whereClause)).append("\"");
        }
    }

    /**
     * Writes the pattern for the business type, name and revision to the TCL
     * update file in <code>_out</code>.
     *
     * @param _out          appendable instance to the TCL update file
     * @param _type         pattern type
     * @param _name         pattern name
     * @param _revision     pattern revision
     * @throws IOException if the TCL update code could not written
     */
    protected abstract void writeTypeNameRevision(final Appendable _out,
                                                  final String _type,
                                                  final String _name,
                                                  final String _revision)
        throws IOException;
}

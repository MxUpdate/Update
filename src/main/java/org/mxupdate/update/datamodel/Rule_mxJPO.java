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

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.helper.AccessList_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export and import / update rule configuration items.
 *
 * @author The MxUpdate Team
 */
public class Rule_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /** Set of all ignored URLs from the XML definition for rules. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Rule_mxJPO.IGNORED_URLS.add("/ownerAccess/access");
        Rule_mxJPO.IGNORED_URLS.add("/ownerRevoke/access");
        Rule_mxJPO.IGNORED_URLS.add("/publicAccess/access");
        Rule_mxJPO.IGNORED_URLS.add("/publicRevoke/access");
        Rule_mxJPO.IGNORED_URLS.add("/userAccessList");
        Rule_mxJPO.IGNORED_URLS.add("/userAccessList/userAccess/access");
    }

    /** Access list. */
    private final AccessList_mxJPO accessList = new AccessList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the rule object
     */
    public Rule_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all rule specific URLs.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
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
        if (Rule_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if (this.accessList.parse(_paramCache, _url, _content))  {
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes specific information about the cached rule to the given
     * writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the rule could not be
     *                     written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        // hidden?
        _out.append(" \\\n    ").append(this.isHidden() ? "hidden" : "!hidden");

        this.accessList.update(" \\\n    add", _out);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this rule. Following steps are
     * done:
     * <ul>
     * <li>set to not hidden</li>
     * <li>no owner and public access</li>
     * <li>no public or owner revoke definition (only if not defined or if not
     *     <code>none</code>)</li>
     * <li>remove all users</li>
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
     * @throws Exception if the called update from derived class failed
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
        final StringBuilder preMQLCode = new StringBuilder()
            .append("escape mod ").append(this.getTypeDef().getMxAdminName())
            .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
            .append(" !hidden ");

        this.accessList.cleanup(preMQLCode);

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}

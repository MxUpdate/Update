/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Rule_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -3114403435479263346L;

    /**
     * Set holding the complete owner access.
     */
    private final Set<String> ownerAccess = new TreeSet<String>();

    /**
     * Set holding the complete public access.
     */
    private final Set<String> publicAccess = new TreeSet<String>();

    /**
     * Stack used to hold the user access while parsing.
     *
     * @see #parse(String, String)
     */
    private final Stack<UserAccess> userAccess = new Stack<UserAccess>();

    /**
     * Sorted set of user access (by name of the user).
     *
     * @see #prepare(ParameterCache_mxJPO)
     */
    private final Set<UserAccess> userAccessSorted = new TreeSet<UserAccess>();

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
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/ownerAccess".equals(_url))  {
            // to be ignored ...
        } else if ("/ownerAccess/access".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/ownerAccess/access/"))  {
            this.ownerAccess.add(_url.replaceAll("^/ownerAccess/access/", "")
                                     .replaceAll("Access$", ""));

        } else if ("/publicAccess".equals(_url))  {
            // to be ignored ...
        } else if ("/publicAccess/access".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/publicAccess/access/"))  {
            this.publicAccess.add(_url.replaceAll("^/publicAccess/access/", "")
                                      .replaceAll("Access$", ""));

        } else if ("/userAccessList".equals(_url))  {
            // to be ignored ...
        } else if ("/userAccessList/userAccess".equals(_url))  {
            this.userAccess.add(new UserAccess());
        } else if ("/userAccessList/userAccess/userRef".equals(_url))  {
            this.userAccess.peek().userRef = _content;
        } else if ("/userAccessList/userAccess/access".equals(_url))  {
            // to be ignored ...
        } else if (_url.startsWith("/userAccessList/userAccess/access/"))  {
            this.userAccess.peek().access.add(_url.replaceAll("^/userAccessList/userAccess/access/", "")
                                                  .replaceAll("Access$", ""));
        } else if ("/userAccessList/userAccess/expressionFilter".equals(_url))  {
            this.userAccess.peek().expressionFilter = _content;

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * The user access instances are sorted.
     *
     * @param _paramCache   parameter cache
     * @see #userAccess         unsorted list of user access
     * @see #userAccessSorted   sorted list user access (after this method is
     *                          called)
     * @throws MatrixException if the prepare from derived class failed
     */
    @Override
    protected void prepare(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        for (final UserAccess range : this.userAccess)  {
            this.userAccessSorted.add(range);
        }
        super.prepare(_paramCache);
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
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        // hidden?
        _out.append(" \\\n    ").append(this.isHidden() ? "hidden" : "!hidden");

        // owner access
        _out.append(" \\\n    add owner \"")
            .append(StringUtil_mxJPO.joinTcl(',', false, this.ownerAccess, null))
            .append('\"')
        // public access
            .append(" \\\n    add public \"")
            .append(StringUtil_mxJPO.joinTcl(',', false, this.publicAccess, null))
            .append('\"');

        // user access
        for (final UserAccess userAccess : this.userAccessSorted)  {
            _out.append(" \\\n    add user \"").append(StringUtil_mxJPO.convertTcl(userAccess.userRef)).append("\" \"")
                .append(StringUtil_mxJPO.joinTcl(',', false, userAccess.access, null))
                .append("\" filter \"")
                .append(StringUtil_mxJPO.convertTcl(userAccess.expressionFilter))
                .append("\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this rule. Following steps are
     * done:
     * <ul>
     * <li>set to not hidden</li>
     * <li>no owner and public access</li>
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
    @Override
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
            .append(" !hidden owner none public none");
        // remove user access
        for (final UserAccess userAccess : this.userAccessSorted)  {
            preMQLCode.append(" remove user \"")
                        .append(StringUtil_mxJPO.convertMql(userAccess.userRef))
                        .append("\" all");
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Class used to hold the user access.
     */
    private class UserAccess
            implements Comparable<Rule_mxJPO.UserAccess>
    {
        /**
         * Holds the user references of a user access.
         */
        String userRef = null;

        /**
         * Holds the access of the user.
         */
        final Set<String> access = new TreeSet<String>();

        /**
         * Holds the expression filter of a user access.
         */
        String expressionFilter = null;

        /**
         * Compares this user access instance to another user access instance.
         * Only the user reference {@link #userRef} is used to compare.
         *
         * @param _userAccess   user access instance to which this instance
         *                      must be compared to
         * @return a negative integer, zero, or a positive integer as this
         *         object represented by {@link #userRef} is less than, equal
         *         to, or greater than the specified object represented by
         *         {@link #userRef}
         */
        public int compareTo(final UserAccess _userAccess)
        {
            return this.userRef.compareTo(_userAccess.userRef);
        }
    }
}

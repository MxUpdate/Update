/*
 * Copyright 2008-2010 The MxUpdate Team
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
import java.util.HashSet;
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
     * Set of all ignored URLs from the XML definition for rules.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Rule_mxJPO.IGNORED_URLS.add("/ownerAccess");
        Rule_mxJPO.IGNORED_URLS.add("/ownerAccess/access");
        Rule_mxJPO.IGNORED_URLS.add("/ownerRevoke");
        Rule_mxJPO.IGNORED_URLS.add("/ownerRevoke/access");
        Rule_mxJPO.IGNORED_URLS.add("/publicAccess");
        Rule_mxJPO.IGNORED_URLS.add("/publicAccess/access");
        Rule_mxJPO.IGNORED_URLS.add("/publicRevoke");
        Rule_mxJPO.IGNORED_URLS.add("/publicRevoke/access");
        Rule_mxJPO.IGNORED_URLS.add("/userAccessList");
        Rule_mxJPO.IGNORED_URLS.add("/userAccessList/userAccess/access");
    }

    /**
     * Set holding the complete owner access.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> ownerAccess = new TreeSet<String>();

    /**
     * Filter for the owner access.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String ownerAccessFilter;

    /**
     * Set holding the complete owner revoke.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> ownerRevoke = new TreeSet<String>();

    /**
     * Filter for the owner revoke.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String ownerRevokeFilter;

    /**
     * Set holding the complete public access.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> publicAccess = new TreeSet<String>();

    /**
     * Filter for the public access.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String publicAccessFilter;

    /**
     * Set holding the complete public revoke.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> publicRevoke = new TreeSet<String>();

    /**
     * Filter for the public revoke.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String publicRevokeFilter;

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
     * @see #IGNORED_URLS
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if (!Rule_mxJPO.IGNORED_URLS.contains(_url))  {
            if (_url.startsWith("/ownerAccess/access/"))  {
                this.ownerAccess.add(_url.replaceAll("^/ownerAccess/access/", "")
                                         .replaceAll("Access$", ""));
            } else if ("/ownerAccess/expressionFilter".equals(_url))  {
                this.ownerAccessFilter = _content;

            } else if (_url.startsWith("/ownerRevoke/access/"))  {
                this.ownerRevoke.add(_url.replaceAll("^/ownerRevoke/access/", "")
                                         .replaceAll("Access$", ""));
            } else if ("/ownerRevoke/expressionFilter".equals(_url))  {
                this.ownerRevokeFilter = _content;

            } else if (_url.startsWith("/publicAccess/access/"))  {
                this.publicAccess.add(_url.replaceAll("^/publicAccess/access/", "")
                                          .replaceAll("Access$", ""));
            } else if ("/publicAccess/expressionFilter".equals(_url))  {
                this.publicAccessFilter = _content;

            } else if (_url.startsWith("/publicRevoke/access/"))  {
                this.publicRevoke.add(_url.replaceAll("^/publicRevoke/access/", "")
                                          .replaceAll("Access$", ""));
            } else if ("/publicRevoke/expressionFilter".equals(_url))  {
                this.publicRevokeFilter = _content;

            } else if ("/userAccessList/userAccess".equals(_url))  {
                this.userAccess.add(new UserAccess());
            } else if ("/userAccessList/userAccess/userRef".equals(_url))  {
                this.userAccess.peek().userRef = _content;
            } else if (_url.startsWith("/userAccessList/userAccess/access/"))  {
                this.userAccess.peek().access.add(_url.replaceAll("^/userAccessList/userAccess/access/", "")
                                                      .replaceAll("Access$", ""));
            } else if ("/userAccessList/userAccess/expressionFilter".equals(_url))  {
                this.userAccess.peek().expressionFilter = _content;

            } else  {
                super.parse(_url, _content);
            }
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
    @Override()
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
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        // hidden?
        _out.append(" \\\n    ").append(this.isHidden() ? "hidden" : "!hidden");

        // owner access
        _out.append(" \\\n    add owner \"")
            .append(StringUtil_mxJPO.joinTcl(',', false, this.ownerAccess, null))
            .append('\"');
        if ((this.ownerAccessFilter != null) && !"".equals(this.ownerAccessFilter))  {
            _out.append(" filter \"")
                .append(StringUtil_mxJPO.convertTcl(this.ownerAccessFilter))
                .append("\"");
        }
        // owner revoke
        if ((!this.ownerRevoke.isEmpty() && !((this.ownerRevoke.size() == 1) && this.ownerRevoke.contains("none")))
                || ((this.ownerRevokeFilter != null) && !"".equals(this.ownerRevokeFilter)))  {
            _out.append(" \\\n    add revoke owner \"")
                .append(StringUtil_mxJPO.joinTcl(',', false, this.ownerRevoke, null))
                .append("\"");
            if ((this.ownerRevokeFilter != null) && !"".equals(this.ownerRevokeFilter))  {
                _out.append(" filter \"")
                    .append(StringUtil_mxJPO.convertTcl(this.ownerRevokeFilter))
                    .append("\"");
            }
        }
        // public access
        _out.append(" \\\n    add public \"")
            .append(StringUtil_mxJPO.joinTcl(',', false, this.publicAccess, null))
            .append('\"');
        if ((this.publicAccessFilter != null) && !"".equals(this.publicAccessFilter))  {
            _out.append(" filter \"")
                .append(StringUtil_mxJPO.convertTcl(this.publicAccessFilter))
                .append("\"");
        }
        // public revoke
        if ((!this.publicRevoke.isEmpty() && !((this.publicRevoke.size() == 1) && this.publicRevoke.contains("none")))
                || ((this.publicRevokeFilter != null) && !"".equals(this.publicRevokeFilter)))  {
            _out.append(" \\\n    add revoke public \"")
                .append(StringUtil_mxJPO.joinTcl(',', false, this.publicRevoke, null))
                .append("\"");
            if ((this.publicRevokeFilter != null) && !"".equals(this.publicRevokeFilter))  {
                _out.append(" filter \"")
                    .append(StringUtil_mxJPO.convertTcl(this.publicRevokeFilter))
                    .append("\"");
            }
        }
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
            .append(" !hidden owner none public none");
        // owner revoke
        if (!this.ownerRevoke.isEmpty() || (this.ownerAccessFilter != null))  {
            preMQLCode.append(" revoke owner none filter \"\"");
        }
        // public revoke
        if (!this.publicRevoke.isEmpty() || (this.publicAccessFilter != null))  {
            preMQLCode.append(" revoke public none filter \"\"");
        }
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

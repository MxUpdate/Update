/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "rule",
                                                     title = "RULE",
                                                     filePrefix = "RULE_",
                                                     fileSuffix = ".tcl",
                                                     filePath = "datamodel/rule",
                                                     description = "rule")
public class Rule_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMObject_mxJPO
{
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
     * @see #prepare(Context)   method used to sort the user access instances
     */
    private final Set<UserAccess> userAccessSorted = new TreeSet<UserAccess>();

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
     * @param _context   context for this request
     * @see #userAccess         unsorted list of user access
     * @see #userAccessSorted   sorted list user access (after this method is
     *                          called)
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        for (final UserAccess range : this.userAccess)  {
            this.userAccessSorted.add(range);
        }
        super.prepare(_context);
    }

    /**
     * Writes specific information about the cached rule to the given
     * writer instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        // hidden?
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "!hidden");

        // owner access
        _out.append(" \\\n    add owner \"");
        boolean first = true;
        for (final String access : this.ownerAccess)  {
            if (!first)  {
                _out.append(',');
            } else  {
                first = false;
            }
            _out.append(access);
        }
        _out.append('\"');

        // public access
        _out.append(" \\\n    add public \"");
        first = true;
        for (final String access : this.publicAccess)  {
            if (!first)  {
                _out.append(',');
            } else  {
                first = false;
            }
            _out.append(access);
        }
        _out.append('\"');

        // user access
        for (final UserAccess userAccess : this.userAccessSorted)  {
            _out.append(" \\\n    add user \"").append(convert(userAccess.userRef)).append("\" \"");
            first = true;
            for (final String access : userAccess.access)  {
                if (!first)  {
                    _out.append(',');
                } else  {
                    first = false;
                }
                _out.append(access);
            }
            _out.append("\" filter \"")
                .append(convert(userAccess.expressionFilter))
                .append("\"");
        }
    }

    /**
     * Appends the MQL statement to reset this rule:
     * <ul>
     * <li>set to not hidden</li>
     * <li>no owner and public access</li>
     * <li>remove all users and properties</li>
     * </ul>
     *
     * @param _context  context for this request
     * @param _cmd      string builder used to append the MQL statements
     */
    @Override
    protected void appendResetMQL(final Context _context,
                                  final StringBuilder _cmd)
    {
        _cmd.append("mod ").append(getInfoAnno().adminType())
            .append(" \"").append(getName()).append('\"')
            .append(" !hidden owner none public none");
        // remove user access
        for (final UserAccess userAccess : this.userAccessSorted)  {
            _cmd.append(" remove user \"").append(userAccess.userRef).append("\" all");
        }
        // reset properties
        this.appendResetProperties(_cmd);
    }

    /**
     * Class used to hold the user access.
     */
    private class UserAccess
            implements Comparable<UserAccess>
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
         */
        public int compareTo(final UserAccess _userAccess)
        {
            return this.userRef.compareTo(_userAccess.userRef);
        }
    }
}

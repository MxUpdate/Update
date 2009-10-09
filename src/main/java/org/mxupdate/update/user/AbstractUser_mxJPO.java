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

package org.mxupdate.update.user;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update common user
 * information within MX. Common user information are all workspace related
 * objects.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public abstract class AbstractUser_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 4072711818597692656L;

    /**
     * Set of all ignored URLs from the XML definition for common stuff of
     * users.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList");
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList/cue/creationInfo");
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList/cue/creationInfo/datetime");
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList/cue/modificationInfo");
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList/cue/modificationInfo/datetime");
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList/cue/queryStatement");
        AbstractUser_mxJPO.IGNORED_URLS.add("/cueList/cue/visibleUserList");
    }

    /**
     * Maps depending on the name of the cue to related cue information. The
     * map is used to sort the cues depending on the name.
     *
     * @see #parse(String, String)
     * @see #writeWorkspaceObjects(ParameterCache_mxJPO, Appendable)
     */
    private final Map<String,Cue> cues = new TreeMap<String,Cue>();

    /**
     * Current cue which is read.
     *
     * @see #parse(String, String)
     */
    private Cue currentCue;

    /**
     * Constructor used to initialize this user definition with related type
     * definition <code>_typeDef</code> for given <code>_name</code>.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractUser_mxJPO(final TypeDef_mxJPO _typeDef,
                                 final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * <p>Parses all common user specific URL values. This includes:
     * <ul>
     * <li>{@link #cues}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #IGNORED_URLS
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if (!AbstractUser_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/cueList/cue".equals(_url))  {
                this.currentCue = new Cue();
            } else if ("/cueList/cue/active".endsWith(_url))  {
                    this.currentCue.active = true;
            } else if ("/cueList/cue/hidden".equals(_url))  {
                this.currentCue.hidden = true;
            } else if ("/cueList/cue/name".equals(_url))  {
                this.cues.put(_content, this.currentCue);
                this.currentCue.name = _content;
            } else if ("/cueList/cue/queryStatement/namePattern".equals(_url))  {
                this.currentCue.patternName = _content;
            } else if ("/cueList/cue/queryStatement/ownerPattern".equals(_url))  {
                this.currentCue.patternOwner = _content;
            } else if ("/cueList/cue/queryStatement/revisionPattern".equals(_url))  {
                this.currentCue.patternRevision = _content;
            } else if ("/cueList/cue/queryStatement/typePattern".equals(_url))  {
                this.currentCue.patternType = _content;
            } else if ("/cueList/cue/queryStatement/vaultPattern".equals(_url))  {
                this.currentCue.patternVault = _content;
            } else if ("/cueList/cue/queryStatement/whereClause".equals(_url))  {
                this.currentCue.whereClause = _content;
            } else if ("/cueList/cue/targetType".equals(_url))  {
                if ("objects".equals(_content))  {
                    this.currentCue.appliesTo = AbstractUser_mxJPO.Cue.AppliesTo.BUSINESSOBJECT;
                } else if ("relationships".equals(_content))  {
                    this.currentCue.appliesTo = AbstractUser_mxJPO.Cue.AppliesTo.CONNECTION;
                } else if ("omni".equals(_content))  {
                    this.currentCue.appliesTo = AbstractUser_mxJPO.Cue.AppliesTo.BOTH;
                } else  {
                    throw new Error("Unknown target type (applies to) '" + _content + "' for cue defined!");
                }
            } else if ("/cueList/cue/zlevel".equals(_url))  {
                this.currentCue.order = _content;
            } else if ("/cueList/cue/foregroundColor".equals(_url))  {
                this.currentCue.foregroundColor = _content;
            } else if ("/cueList/cue/highlightColor".equals(_url))  {
                this.currentCue.highlightColor = _content;
            } else if ("/cueList/cue/fontName".equals(_url))  {
                this.currentCue.fontName = _content;
            } else if ("/cueList/cue/lineStyle".equals(_url))  {
                this.currentCue.lineStyle = _content;
            } else if ("/cueList/cue/visibleUserList/userRef".equals(_url))  {
                this.currentCue.visibleFor.add(_content);
            } else  {
                super.parse(_url, _content);
            }
        }
    }

    /**
     * Writes specific information about the cached role to the given
     * writer instance. The included information is:
     * <ul>
     * <li>{@link #cues}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    protected void writeWorkspaceObjects(final ParameterCache_mxJPO _paramCache,
                                         final Appendable _out)
        throws IOException
    {
        for (final Cue cue : this.cues.values())  {
            _out.append("\nmql escape add cue \"")
                .append(StringUtil_mxJPO.convertTcl(cue.name))
                .append("\" \\\n    user \"${NAME}\"")
                .append(" \\\n    ").append(cue.active ? "active" : "!active")
                .append(" \\\n    ").append(cue.hidden ? "hidden" : "!hidden");
            for (final String user : cue.visibleFor)  {
                _out.append(" \\\n    visible \"").append(StringUtil_mxJPO.convertTcl(user))
                    .append("\"");
            }
            _out.append(" \\\n    appliesto \"").append(cue.appliesTo.mxValue)
                .append("\"");
            if (cue.order != null)  {
                _out.append(" \\\n    order \"").append(StringUtil_mxJPO.convertTcl(cue.order))
                    .append("\"");
            }
            _out.append(" \\\n    type \"").append(StringUtil_mxJPO.convertTcl(cue.patternType))
                .append("\" \\\n    name \"").append(StringUtil_mxJPO.convertTcl(cue.patternName))
                .append("\" \\\n    revision \"").append(StringUtil_mxJPO.convertTcl(cue.patternRevision))
                .append("\" \\\n    vault \"").append(StringUtil_mxJPO.convertTcl(cue.patternVault))
                .append("\" \\\n    owner \"").append(StringUtil_mxJPO.convertTcl(cue.patternOwner))
                .append("\"");
            if (cue.whereClause != null)  {
                _out.append(" \\\n    where \"").append(StringUtil_mxJPO.convertTcl(cue.whereClause))
                    .append("\"");
            }
            if (cue.foregroundColor != null)  {
                _out.append(" \\\n    color \"").append(StringUtil_mxJPO.convertTcl(cue.foregroundColor))
                    .append("\"");
            }
            if (cue.highlightColor != null)  {
                _out.append(" \\\n    highlight \"").append(StringUtil_mxJPO.convertTcl(cue.highlightColor))
                    .append("\"");
            }
            if (cue.fontName != null)  {
                _out.append(" \\\n    font \"").append(StringUtil_mxJPO.convertTcl(cue.fontName))
                    .append("\"");
            }
            if (cue.lineStyle != null)  {
                _out.append(" \\\n    linestyle \"").append(StringUtil_mxJPO.convertTcl(cue.lineStyle))
                    .append("\"");
            }
        }
    }
    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this user. Following steps are
     * done:
     * <ul>
     * <li>remove all {@link #cues}</li>
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
        final StringBuilder preMQLCode = new StringBuilder();

        // remove all assigned cues
        for (final Cue cue : this.cues.values())  {
            preMQLCode.append("escape delete cue \"")
                      .append(StringUtil_mxJPO.convertMql(cue.name))
                      .append("\" user \"")
                      .append(StringUtil_mxJPO.convertMql(this.getName()))
                      .append("\";\n");
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * User specific class to store the workspace object for one cue.
     */
    private static final class Cue
    {
        /**
         * Enumeration for which a cue applies to.
         */
        enum AppliesTo
        {
            /**
             * Cue applies to business objects.
             */
            BUSINESSOBJECT("businessobject"),

            /**
             * Cue applies to connections.
             */
            CONNECTION("relationship"),

            /**
             * Cue applies to business objects and to connections.
             */
            BOTH("all");

            /**
             * Related value needed within TCL update.
             */
            final String mxValue;

            /**
             * Default constructor.
             *
             * @param _mxValue  related value used for the TCL update
             */
            private AppliesTo(final String _mxValue)
            {
                this.mxValue = _mxValue;
            }
        }

        /**
         * Default constructor because this is a private class.
         */
        private Cue()
        {
        }

        /**
         * Is the cue active?
         */
        private boolean active = false;

        /**
         * Is the cue hidden?
         */
        private boolean hidden = false;

        /**
         * Specifies other existing users who can read the workspace item with
         * MQL list, print  and evaluate commands.
         */
        private final Set<String> visibleFor = new TreeSet<String>();

        /**
         * Applies to business object / relationship or all.
         */
        private AppliesTo appliesTo = AbstractUser_mxJPO.Cue.AppliesTo.BOTH;

        /**
         * Name of the cue.
         */
        private String name;

        /**
         * Type pattern.
         */
        private String patternType;

        /**
         * Name pattern.
         */
        private String patternName;

        /**
         * Revision pattern.
         */
        private String patternRevision;

        /**
         * Vault pattern.
         */
        private String patternVault;

        /**
         * Owner pattern.
         */
        private String patternOwner;

        /**
         * Where clause.
         */
        private String whereClause;

        /**
         * Order in which the cue is applied in relation to other cues: before,
         * with or after other cues (-1 / 0 / 1).
         */
        private String order;

        /**
         * Foreground color.
         */
        private String foregroundColor;

        /**
         * Highlight color.
         */
        private String highlightColor;

        /**
         * Name of the font.
         */
        private String fontName;

        /**
         * Line style.
         */
        private String lineStyle;
    }
}

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
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Data model interface class.
 *
 * @author The MxUpdate Team
 */
public class Interface_mxJPO
    extends AbstractDMWithAttributes_mxJPO<Interface_mxJPO>
{
    /**
     * Key used to identify the update of an interface within
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     * @see #TCL_PROCEDURE
     */
    private static final String JPO_CALLER_KEY = "parents";

    /**
     * Called TCL procedure within the TCL update to assign parent interface to
     * this interface object. The first argument of the JPO caller is
     * &quot;parents&quot; to differ between an update for parent interface or
     * for attributes.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String TCL_PROCEDURE
            = "proc testParents {args}  {\n"
                + "set iIdx 0\n"
                + "set lsCmd [list mql exec prog org.mxupdate.update.util.JPOCaller " + Interface_mxJPO.JPO_CALLER_KEY + "]\n"
                + "while {$iIdx < [llength $args]}  {\n"
                +   "lappend lsCmd [lindex $args $iIdx]\n"
                +   "incr iIdx\n"
                + "}\n"
                + "eval $lsCmd\n"
            + "}\n";

    /**
     * Set of all ignored URLs from the XML definition for interfaces.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Interface_mxJPO.IGNORED_URLS.add("/derivedFromInterface");
        Interface_mxJPO.IGNORED_URLS.add("/derivedFromInterface/interfaceTypeRefList");
        Interface_mxJPO.IGNORED_URLS.add("/relationshipDefRefList");
        Interface_mxJPO.IGNORED_URLS.add("/typeRefList");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/creationInfo");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/creationInfo/datetime");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/modificationInfo");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/modificationInfo/datetime");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/propertyList");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/datetime");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/agent");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/event");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/order");
        Interface_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/string");
    }

    /**
     * From which interfaces is this interface derived?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> derived = new TreeSet<String>();

    /**
     * Is the interface abstract?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean abstractFlag;

    /**
     * Are all types allowed for this interface?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private boolean allTypes;

    /**
     * Information about all allowed types for this interface.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Set<String> types = new TreeSet<String>();

    /**
     * Are all relationships allowed for this interface?
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private boolean allRelationships;

    /**
     * Information about all allowed relationships for this interface.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Set<String> relationships = new TreeSet<String>();

    /**
     * Constructor used to initialize the interface class instance.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the interface object
     */
    public Interface_mxJPO(final TypeDef_mxJPO _typeDef,
                           final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
//        new InterfaceDefParser_mxJPO(new StringReader(_code)).parse(this);
//        this.prepare();
    }

    /**
     * Parses the interface specific XML export URL.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #abstractFlag
     * @see #allTypes
     * @see #types
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Interface_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/abstract".equals(_url))  {
            this.abstractFlag = true;
            parsed = true;
        } else if ("/allowAllRelationships".equals(_url))  {
            this.allRelationships = true;
            parsed = true;
        } else if ("/allowAllTypes".equals(_url))  {
            this.allTypes = true;
            parsed = true;
        } else if ("/derivedFromInterface/interfaceTypeRefList/interfaceTypeRef".equals(_url))  {
            this.derived.add(_content);
            parsed = true;
        } else if ("/relationshipDefRefList/relationshipDefRef".equals(_url))  {
            this.relationships.add(_content);
            parsed = true;
        } else if ("/typeRefList/typeRef".equals(_url))  {
            this.types.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes the interface specific information into the TCL update file. The
     * interface specific information are:
     * <ul>
     * <li>abstract information {@link #abstractFlag}</li>
     * <li>relationship information (all relationships defined with
     *     {@link #allRelationships} or some relationships defined with
     *     {@link #relationships})</li>
     * <li>type information (all types defined with {@link #allTypes} or some
     *     types defined with {@link #types})</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the interface specific information could not be
     *                     written
     * @see #abstractFlag
     * @see #allRelationships
     * @see #relationships
     * @see #allTypes
     * @see #types
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        // write abstract information
        _out.append(" \\\n    ").append(this.isHidden() ? "" : "!").append("hidden")
            .append(" \\\n    abstract \"").append(Boolean.toString(this.abstractFlag)).append('\"');

        // relationship information
        if (this.allRelationships)  {
            _out.append(" \\\n    add relationship all");
        } else  {
            for (final String relationship : this.relationships)  {
                _out.append(" \\\n    add relationship \"")
                    .append(StringUtil_mxJPO.convertTcl(relationship)).append('\"');
            }
        }

        // type information
        if (this.allTypes)  {
            _out.append(" \\\n    add type all");
        } else  {
            for (final String type : this.types)  {
                _out.append(" \\\n    add type \"").append(StringUtil_mxJPO.convertTcl(type)).append('\"');
            }
        }
    }

    /**
     * Appends at the end of the TCL update file the call to the
     * {@link #TCL_PROCEDURE} to define the parent interfaces for this
     * interface.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     * @see #derived
     */
    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
        _out.append("\n\ntestParents -").append(this.getTypeDef().getMxAdminName())
            .append(" \"${NAME}\" -parents [list \\\n");
        for (final String parent : this.derived)  {
            _out.append("    \"").append(StringUtil_mxJPO.convertTcl(parent)).append("\" \\\n");
        }
        _out.append("]");

        super.writeEnd(_paramCache, _out);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this interface and to append
     * the TCL procedure {@link #TCL_PROCEDURE}. This information is reset:
     * <ul>
     * <li>set to not hidden</li>
     * <li>set to not abstract</li>
     * <li>reset description</li>
     * <li>remove all types in {@link #allTypes} or {@link #types}</li>
     * <li>remove all relationships in {@link #allRelationships} or
     *     {@link #relationships}</li>
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
     * @see #TCL_PROCEDURE
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
                .append(" !hidden description \"\" abstract false");

        // relationship information
        if (this.allRelationships)  {
            preMQLCode.append(" remove relationship all");
        } else  {
            for (final String relationship : this.relationships)  {
                preMQLCode.append(" remove relationship \"")
                          .append(StringUtil_mxJPO.convertMql(relationship)).append('\"');
            }
        }

        // type information
        if (this.allTypes)  {
            preMQLCode.append(" remove type all");
        } else  {
            for (final String type : this.types)  {
                preMQLCode.append(" remove type \"").append(StringUtil_mxJPO.convertMql(type)).append('\"');
            }
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // add TCL code for the procedure
        final StringBuilder preTclCode = new StringBuilder()
                .append(Interface_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, preTclCode, _tclVariables, _sourceFile);
    }

    /**
     * The method is called within the update of an interface object. The
     * method is called directly within the update and checks which parent
     * interfaces are missed in the new definition and adds missing parent
     * interfaces to the interface object. If an interface is not defined
     * anymore but assigned in MX, an exception is thrown.
     * If the first argument is not &quot;parents&quot; method
     * {@link AbstractDMWithAttributes_mxJPO#jpoCallExecute(ParameterCache_mxJPO, String...)}
     * is called.
     *
     * @param _paramCache   parameter cache
     * @param _args         arguments from the TCL procedure
     * @throws Exception if an unknown parameter is defined, the given name of
     *                   the administration object is not the same or an
     *                   interface is assigned to the interface object
     *                   within MX but not defined anymore
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
            throws Exception
    {
        if ((_args.length == 0) || !Interface_mxJPO.JPO_CALLER_KEY.equals(_args[0]))  {
            super.jpoCallExecute(_paramCache, _args);
        } else  {
            // evaluate parameters
            final String nameParam = new StringBuilder()
                    .append('-').append(this.getTypeDef().getMxAdminName()).toString();
            int idx = 1;
            String name = null;
            String parentsStr = null;
            while (idx < _args.length)  {
                final String arg = _args[idx];
                if (nameParam.equals(arg))  {
                    name = _args[++idx];
                } else if ("-parents".equals(arg))  {
                    parentsStr = _args[++idx];
                } else  {
                    throw new UpdateException_mxJPO(
                            UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_UKNOWN_PARAMETER,
                            arg);
                }
                idx++;
            }

            // check for equal administration name
            if (!this.getName().equals(name))  {
                throw new UpdateException_mxJPO(
                        UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_WRONG_NAME,
                        this.getTypeDef().getLogging(),
                        this.getName(),
                        name);
            }

            // get all parent interfaces
            final Pattern pattern = Pattern.compile("(\\{[^\\{\\}]*\\} )|([^ \\{\\}]* )");
            final Matcher matcher = pattern.matcher(parentsStr + " ");
            final Set<String> newParents = new TreeSet<String>();
            while (matcher.find())  {
                final String parentName = matcher.group().trim().replaceAll("(^\\{)|(\\}$)", "");
                if (!"".equals(parentName))  {
                    newParents.add(parentName);
                }
            }

            // check if all current parents are within new parents
            for (final String curParent : this.derived)  {
                if (!newParents.contains(curParent))  {
                    throw new UpdateException_mxJPO(
                            UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_REMOVING_PARENT,
                            this.getTypeDef().getLogging(),
                            this.getName(),
                            curParent);
                }
            }

            // and append all not current derived parents
            if (!newParents.isEmpty())  {
                final StringBuilder cmd = new StringBuilder()
                        .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                        .append(" '").append(StringUtil_mxJPO.convertMql(this.getName())).append("\' derived ");
                boolean first = true;
                for (final String newParent : newParents)  {
                    if (!this.derived.contains(newParent))  {
                        _paramCache.logDebug("    - add to parent '" + newParent + "'");
                    }
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append('\"').append(StringUtil_mxJPO.convertMql(newParent)).append('\"');
                }
                MqlUtil_mxJPO.execMql(_paramCache, cmd);
            }
        }
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Interface_mxJPO _current)
        throws UpdateException_mxJPO
    {
    }
}

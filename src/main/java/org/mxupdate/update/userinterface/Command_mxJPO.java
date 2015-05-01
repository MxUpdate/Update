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

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export and import / update command configuration items.
 * The command specific information are:
 * <ul>
 * <li>description</li>
 * <li>hidden flag (only if <i>true</i>)</li>
 * <li>label</li>
 * <li>href</li>
 * <li>alt label</li>
 * <li>user</li>
 * <li>settings</li>
 * <li>properties</li>
 * <li>code</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Command_mxJPO
    extends AbstractCommand_mxJPO<Command_mxJPO>
{
    /**
     * Set of all ignored URLs from the XML definition for commands.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Command_mxJPO.IGNORED_URLS.add("/userRefList");
    }

    /** Sorted list of assigned users of the command. */
    private final SortedSet<String> users = new TreeSet<String>();
    /** Code of the command. */
    private String code;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Command_mxJPO(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
    {
        super(_typeDef, _mxName);
        this.getProperties().setOtherPropTag("setting");
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new CommandParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * Parses all command specific values. This includes:
     * <ul>
     * <li>{@link #code}</li>
     * <li>user references in {@link #users}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      related content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Command_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/code".equals(_url))  {
            this.code = _content;
            parsed = true;
        } else if ("/userRefList/userRef".equals(_url))  {
            this.users.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    @Override()
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .list(          "symbolicname",            this.getSymbolicNames())
                .string(        "description",             this.getDescription())
                .flagIfTrue(    "hidden",           false, this.isHidden(),                     this.isHidden())
                .string(        "label",                   this.getLabel())
                .string(        "href",                    this.getHref())
                .string(        "alt",                     this.getAlt())
                .list(          "user",                    this.users)
                .otherProps(this.getProperties())
                .properties(this.getProperties())
                .stringIfTrue(   "code",                    "\n" + ((this.code != null) ? this.code : "") + "\n", (this.code != null) && !this.code.isEmpty());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Command_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this.getTypeDef(), this.getName(), this.getSymbolicNames(), _current.getSymbolicNames());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),                      _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),                            _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "alt",                 this.getAlt(),                              _current.getAlt());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "href",                this.getHref(),                             _current.getHref());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "label",               this.getLabel(),                            _current.getLabel());
        DeltaUtil_mxJPO.calcListDelta(_mql,  "user",                this.users,                                 _current.users);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "code",                (this.code == null) ? "" :this.code.trim(), _current.code);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

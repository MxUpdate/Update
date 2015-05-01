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

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.userinterface.helper.ChildRefList_mxJPO;
import org.mxupdate.update.userinterface.helper.ChildRefList_mxJPO.WriteAppendChildSyntax;
import org.mxupdate.update.userinterface.menu.MenuDefParser_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.xml.sax.SAXException;

/**
 * The class is used to export and import / update menu configuration items.
 *
 * @author The MxUpdate Team
 */
public class Menu_mxJPO
    extends AbstractCommand_mxJPO<Menu_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for menus. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Menu_mxJPO.IGNORED_URLS.add("/commandRefList");
        Menu_mxJPO.IGNORED_URLS.add("/menuRefList");
    }

    /** Flag to store the information that the menu is a type tree menu. */
    private boolean treeMenu = false;
    /** All referenced children. */
    private final ChildRefList_mxJPO children = new ChildRefList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Menu_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new MenuDefParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * {@inheritDoc}
     * Also it is checked if the menu is assigned to the tree menu.
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException
    {
        super.parse(_paramCache);

        final String isMenuTreeStr = MqlBuilder_mxJPO.mql()
                .cmd("escape print menu ").arg(this.getName()).cmd(" select ").arg("parent[Tree]").cmd(" dump")
                .exec(_paramCache);
        if ("TRUE".equalsIgnoreCase(isMenuTreeStr))  {
            this.treeMenu = true;
        }
    }

    /**
     * Parses all menu related URL's. This means the names of all referenced
     * children menus and commands are parsed and stored in {@link #children}.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      related content of the URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Menu_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if (_url.startsWith("/commandRefList"))  {
            parsed = this.children.parse(_url.substring(15), _content);
        } else if (_url.startsWith("/menuRefList"))  {
            parsed = this.children.parse(_url.substring(12), _content);
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Sorts the child objects as defined.
     */
    @Override()
    protected void prepare()
    {
        this.children.prepare();

        super.prepare();
    }

    /**
     * Writes the update script for this menu.
     * The command specific information are:
     * <ul>
     * <li>description</li>
     * <li>hidden flag (only if <i>true</i>)</li>
     * <li>label</li>
     * <li>href</li>
     * <li>alt label</li>
     * <li>settings</li>
     * <li>properties</li>
     * <li>sub commands and menus</li>
     * </ul>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException if the TCL update code could not be written
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.writeHeader(_paramCache, _out);

        _out.append("mxUpdate menu \"${NAME}\"  {\n")
            .append("    description \"").append(StringUtil_mxJPO.convertUpdate(this.getDescription())).append("\"\n");
        if (this.isHidden())  {
            _out.append("    hidden\n");
        }
        if (this.treeMenu)  {
            _out.append("    treemenu\n");
        }
        _out.append("    label \"").append(StringUtil_mxJPO.convertUpdate(this.getLabel())).append("\"\n")
            .append("    href \"").append(StringUtil_mxJPO.convertUpdate(this.getHref())).append("\"\n")
            .append("    alt \"").append(StringUtil_mxJPO.convertUpdate(this.getAlt())).append("\"\n");
        this.getProperties().writeSettings(_paramCache, _out, "    ");

        this.children.write(_out);

        this.getProperties().writeProperties(_paramCache, _out, "    ");

        _out.append("}");
    }

    @Override
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Menu_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),   _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),         _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "alt",                 this.getAlt(),           _current.getAlt());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "href",                this.getHref(),          _current.getHref());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "label",               this.getLabel(),         _current.getLabel());

        this.children.calcDelta(_mql, WriteAppendChildSyntax.Add, _current.children);

        // type tree menu
        if (_current.treeMenu != this.treeMenu)  {
            _mql.pushPrefix("");
            if (this.treeMenu)  {
                _mql.newLine().cmd("escape mod menu ").arg("Tree").cmd(" add menu ").arg(_current.getName());
            } else  {
                _mql.newLine().cmd("escape mod menu ").arg("Tree").cmd(" remove menu ").arg(_current.getName());
            }
            _mql.popPrefix();
        }

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}

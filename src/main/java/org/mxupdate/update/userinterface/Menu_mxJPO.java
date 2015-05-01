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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.userinterface.menu.MenuDefParser_mxJPO;
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
    extends AbstractCommand_mxJPO
{
    /** Set of all ignored URLs from the XML definition for menus. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Menu_mxJPO.IGNORED_URLS.add("/commandRefList");
        Menu_mxJPO.IGNORED_URLS.add("/menuRefList");
    }

    /** Flag to store the information that the menu is a type tree menu. */
    private boolean treeMenu = false;
    /** Holds all children of this menu instance. */
    private final Stack<MenuChild> children = new Stack<MenuChild>();
    /** Holds all children of this menu instance correct sorted. */
    private final List<MenuChild> childrenSorted = new ArrayList<MenuChild>();

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
        } else if ("/commandRefList/commandRef".equals(_url))  {
            final MenuChild child = new MenuChild();
            child.type = "command";
            this.children.add(child);
            parsed = true;
        } else if ("/commandRefList/commandRef/name".equals(_url))  {
            this.children.peek().name = _content;
            parsed = true;
        } else if ("/commandRefList/commandRef/order".equals(_url))  {
            this.children.peek().order = Integer.parseInt(_content);
            parsed = true;

        } else if ("/menuRefList/menuRef".equals(_url))  {
            final MenuChild child = new MenuChild();
            child.type = "menu";
            this.children.add(child);
            parsed = true;
        } else if ("/menuRefList/menuRef/name".equals(_url))  {
            this.children.peek().name = _content;
            parsed = true;
        } else if ("/menuRefList/menuRef/order".equals(_url))  {
            this.children.peek().order = Integer.parseInt(_content);
            parsed = true;

        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Sorts the child objects as defined.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the preparation from derived class failed
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        super.prepare(_paramCache);

        // order childs
        final Map<Integer,MenuChild> tmpChilds = new TreeMap<Integer,MenuChild>();
        for (final MenuChild child : this.children)  {
            tmpChilds.put(child.order, child);
        }
        this.childrenSorted.addAll(tmpChilds.values());
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

        // output childs
        for (final MenuChild child : this.childrenSorted)  {
            _out.append("    ").append(child.type).append(" \"").append(StringUtil_mxJPO.convertUpdate(child.name)).append("\"\n");
        }

        this.getProperties().writeProperties(_paramCache, _out, "    ");

        _out.append("}");
    }

    /**
     * The method is called from the TCL update code to define the this
     * menu.
     *
     * @param _paramCache   parameter cache
     * @param _args         first index defines the use case (must be
     *                      &quot;updateAttribute&quot; that the attribute
     *                      is updated); second index the name of the attribute
     *                      to update
     * @throws Exception if the update of the dimension failed or for all other
     *                   use cases from super JPO call
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        // check if dimension is defined
        if ((_args.length == 4) && "mxUpdate".equals(_args[0]) && "menu".equals(_args[1])) {
// TODO: Exception Handling
            // check that command names are equal
            if (!this.getName().equals(_args[2]))  {
                throw new Exception("wrong menu '" + _args[1] + "' is set to update (currently menu '" + this.getName() + "' is updated!)");
            }

            final String code = _args[3].replaceAll("@0@0@", "'").replaceAll("@1@1@", "\\\"");

            final MenuDefParser_mxJPO parser = new MenuDefParser_mxJPO(new StringReader(code));
            final Menu_mxJPO menu = parser.parse(_paramCache, this.getTypeDef(), this.getName());

            final MultiLineMqlBuilder mql = MqlBuilder_mxJPO.multiLine("escape mod menu $1", this.getName());

            this.calcDelta(_paramCache, mql, menu);

            mql.exec(_paramCache);

        } else  {
            super.jpoCallExecute(_paramCache, _args);
        }
    }

    /**
     * Calculates the delta between this current menu definition and the
     * {@code _target} menu definition and appends the MQL append commands
     * to {@code _cmd}.
     *
     * @param _paramCache   parameter cache
     * @param _cmd          string builder to append the MQL commands
     * @param _target       target format definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Menu_mxJPO _target)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description", _target.getDescription(),   this.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      _target.isHidden(),         this.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "alt",         _target.getAlt(),           this.getAlt());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "href",        _target.getHref(),          this.getHref());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "label",       _target.getLabel(),         this.getLabel());

        // type tree menu
        if (this.treeMenu != _target.treeMenu)  {
            _mql.pushPrefix("");
            if (_target.treeMenu)  {
                _mql.newLine().cmd("escape mod menu ").arg("Tree").cmd(" add menu ").arg(this.getName());
            } else  {
                _mql.newLine().cmd("escape mod menu ").arg("Tree").cmd(" remove menu ").arg(this.getName());
            }
            _mql.popPrefix();
        }

        // children commands and menus
        final Iterator<MenuChild> iterThis   = this.childrenSorted.iterator();
        final Iterator<MenuChild> iterTarget = _target.childrenSorted.iterator();
        MenuChild childThis   = null;
        MenuChild childTarget = null;
        boolean equal = true;
        while (iterThis.hasNext() && iterTarget.hasNext())  {
            childThis   = iterThis.next();
            childTarget = iterTarget.next();
            if (!childThis.equals(childTarget))  {
                equal = false;
                break;
            }
        }
        // remove this childs if needed
        if (!equal)  {
            _mql.newLine().cmd("remove ").cmd(childThis.type).cmd(" ").arg(childThis.name);
        }
        while (iterThis.hasNext())  {
            childThis   = iterThis.next();
            _mql.newLine().cmd("remove ").cmd(childThis.type).cmd(" ").arg(childThis.name);
        }
        // assign targets if needed
        if (!equal)  {
            _mql.newLine().cmd("add ").cmd(childTarget.type).cmd(" ").arg(childTarget.name);
        }
        while (iterTarget.hasNext())  {
            childTarget = iterTarget.next();
            _mql.newLine().cmd("add ").cmd(childTarget.type).cmd(" ").arg(childTarget.name);
        }

        _target.getProperties().calcDelta(_mql, "", this.getProperties());
    }

    /**
     * Stores a reference to a child of a menu.
     */
    public static final class MenuChild
    {
        /** Type of the menu child. */
        private String type;
        /** Name of the menu child. */
        private String name;
        /** Order index of the menu child. */
        private Integer order;

        /**
         * Constructor.
         *
         * @param _type     type of menu child
         * @param _name     name of menu child
         * @param _order    order of menu child
         */
        public MenuChild(final String _type,
                         final String _name,
                         final int _order)
        {
            this.type  = _type;
            this.name  = _name;
            this.order = _order;
        }

        /**
         * Private constructor so that an instance could only be created within
         * the Menu_mxJPO class.
         */
        private MenuChild()
        {
        }

        /**
         * Checks if this child menu and the given {@code _toCompare} is equal.
         *
         * @param _toCompare    compare child menu
         * @return <i>true</i> if equal; otherwise <i>false</i>
         */
        boolean equals(final MenuChild _toCompare)
        {
            return (_toCompare != null) && this.type.equals(_toCompare.type) && this.name.equals(_toCompare.name);
        }
    }
}

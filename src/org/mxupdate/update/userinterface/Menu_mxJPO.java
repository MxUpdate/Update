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

package org.mxupdate.update.userinterface;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class Menu_mxJPO
        extends Command_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 3617033695673460587L;

    /**
     * Flag to store the information that the menu is a tree menu.
     *
     * @see #prepare(ParamCache)
     * @see #writeEnd(Appendable)
     */
    private boolean treeMenu = false;

    /**
     * Holds all children of this menu instance.
     *
     * @see MenuChild
     */
    private final Stack<MenuChild> childs = new Stack<MenuChild>();

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
     * Parses all menu related URL's. This means the names of all referenced
     * children menus and commands are parsed and stored in {@link #childs}.
     *
     * @param _url      URL to parse
     * @param _content  related content of the URL
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/commandRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/commandRefList/commandRef".equals(_url))  {
            final MenuChild child = new MenuChild();
            child.type = "command";
            this.childs.add(child);
        } else if ("/commandRefList/commandRef/name".equals(_url))  {
            this.childs.peek().name = _content;
        } else if ("/commandRefList/commandRef/order".equals(_url))  {
            this.childs.peek().order = Integer.parseInt(_content);

        } else if ("/menuRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/menuRefList/menuRef".equals(_url))  {
            final MenuChild child = new MenuChild();
            child.type = "menu";
            this.childs.add(child);
        } else if ("/menuRefList/menuRef/name".equals(_url))  {
            this.childs.peek().name = _content;
        } else if ("/menuRefList/menuRef/order".equals(_url))  {
            this.childs.peek().order = Integer.parseInt(_content);

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * @param _paramCache   parameter cache
     * @throws MatrixException if the preparation from derived class failed or
     *                         the check for tree menu used in
     *                         {@link #treeMenu} failed
     * @see #treeMenu
     */
    @Override
    protected void prepare(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("print menu \"").append(this.getName()).append("\" select parent[Tree] dump");
        if ("TRUE".equalsIgnoreCase(execMql(_paramCache.getContext(), cmd)))  {
            this.treeMenu = true;
        }
        super.prepare(_paramCache);
    }

    /**
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        super.writeObject(_paramCache, _out);

        // order childs
        final Map<Integer,MenuChild> tmpChilds = new TreeMap<Integer,MenuChild>();
        for (final MenuChild child : this.childs)  {
            tmpChilds.put(child.order, child);
        }

        // output childs
        for (final MenuChild child : tmpChilds.values())  {
            _out.append(" \\\n    add ").append(child.type).append(" \"")
                .append(convertTcl(child.name)).append("\"");
        }

    }

    /**
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written
     */
    @Override
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
            throws IOException
    {
        if (this.treeMenu)  {
            _out.append("\n\nmql mod menu \"Tree\" \\\n    add menu \"${NAME}\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this menu. Following steps are
     * done:
     * <ul>
     * <li>remove all child commands / menus</li>
     * <li>remove definition as tree menu</li>
     * </ul>
     * The description etc. is reseted in the command super class.
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
    @Override
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        // remove child commands / menus
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName()).append(" \"").append(this.getName()).append('\"');
        for (final MenuChild child : this.childs)  {
            preMQLCode.append(" remove ").append(child.type)
                      .append(" \"").append(child.name).append("\"");
        }
        preMQLCode.append(";\n");

        // remove information about tree menu...
        if (this.treeMenu)  {
            preMQLCode.append("mod menu Tree remove menu \"").append(this.getName()).append("\";\n");
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Stores a reference to a child of a menu.
     *
     * @see Menu_mxJPO#childs
     */
    private final class MenuChild
    {
        /**
         * Type of the menu child.
         */
        private String type;

        /**
         * Name of the menu child.
         */
        private String name;

        /**
         * Order index of the menu child.
         */
        private Integer order;

        /**
         * Private constructor so that an instance could only be created within
         * the Menu_mxJPO class.
         */
        private MenuChild()
        {
        }
    }
}
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

package net.sourceforge.mxupdate.update.userinterface;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 */
public class Menu_mxJPO
        extends net.sourceforge.mxupdate.update.userinterface.Command_mxJPO
{
    boolean treeMenu = false;

    final Stack<MenuChild> childs = new Stack<MenuChild>();

    public Menu_mxJPO()
    {
        super("menu");
    }

    @Override
    public void parse(final String _url,
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

    @Override
    public void prepare(final Context _context)
            throws MatrixException
    {
        MQLCommand mql = new MQLCommand();
        mql.executeCommand(_context, "print menu \"" + this.getName() + "\" select parent[Tree] dump");
        if ("TRUE".equalsIgnoreCase(mql.getResult().trim()))  {
            this.treeMenu = true;
        }
        super.prepare(_context);
    }

    @Override
    protected void writeObject(Writer _out) throws IOException
    {
        super.writeObject(_out);

        // order childs
        final Map<Integer,MenuChild> tmpChilds = new TreeMap<Integer,MenuChild>();
        for (final MenuChild child : this.childs)  {
            tmpChilds.put(child.order, child);
        }

        // output childs
        for (final MenuChild child : tmpChilds.values())  {
            _out.append(" \\\n    add ").append(child.type).append(" \"")
                .append(convert(child.name)).append("\"");
        }

        if (this.treeMenu)  {
            _out.append("\n\nmql mod menu \"Tree\" \\\n    add menu \"${NAME}\"");
        }
    }

    /**
     *
     */

    class MenuChild
    {
        String type = null;
        String name = null;
        Integer order = null;
    }
}
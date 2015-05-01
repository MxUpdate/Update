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

package org.mxupdate.update.userinterface.helper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;

import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;

/**
 * List of referenced objects.
 *
 * @author The MxUpdate Team
 */
public class ChildRefList_mxJPO
    extends TreeSet<ChildRefList_mxJPO.AbstractRef>
    implements UpdateList
{
    /** Because not needed, the default serialize version ID is defined. */
    private static final long serialVersionUID = 1L;
    /** Holds all children of this references list. */
    private final Stack<AbstractRef> childrenStack = new Stack<AbstractRef>();

    /**
     * Parses the references.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL
     * @return <i>true</i> if parsed; otherwise <i>false</i>
     */
    public boolean parse(final String _url,
                         final String _content)
    {
        final boolean parsed;
        if ("/channelRef".equals(_url))  {
            this.childrenStack.add(new ChannelRef());
            parsed = true;
        } else if ("/channelRef/name".equals(_url))  {
            this.childrenStack.peek().name = _content;
            parsed = true;
        } else if ("/channelRef/portalRow".equals(_url))  {
            this.childrenStack.peek().row = Integer.parseInt(_content);
            parsed = true;
        } else if ("/channelRef/portalColumn".equals(_url))  {
            this.childrenStack.peek().column = Integer.parseInt(_content);
            parsed = true;

        } else if ("/commandRef".equals(_url))  {
            this.childrenStack.add(new CommandRef());
            parsed = true;
        } else if ("/commandRef/name".equals(_url))  {
            this.childrenStack.peek().name = _content;
            parsed = true;
        } else if ("/commandRef/order".equals(_url))  {
            this.childrenStack.peek().column = Integer.parseInt(_content);
            parsed = true;

        } else if ("/menuRef".equals(_url))  {
            this.childrenStack.add(new MenuRef());
            parsed = true;
        } else if ("/menuRef/name".equals(_url))  {
            this.childrenStack.peek().name = _content;
            parsed = true;
        } else if ("/menuRef/order".equals(_url))  {
            this.childrenStack.peek().column = Integer.parseInt(_content);
            parsed = true;

        } else  {
            parsed = false;
        }

        return parsed;
    }

    /**
     * Prepares the properties by sorting all referenced children.
     */
    public void prepare()
    {
        this.addAll(this.childrenStack);
    }

    /**
     * Writes the MQL code to append all references.
     *
     * @param _out      appendable instance to the TCL update file
     * @throws IOException if the write to the TCL update file failed
     */
    @Deprecated()
    public void write(final Appendable _out)
        throws IOException
    {
        if (!this.isEmpty())  {
            int row = this.first().row;
            for (final AbstractRef ref : this)  {
                if (row != ref.row)  {
                    _out.append("    newrow\n");
                    row = ref.row;
                }
                _out.append("    ").append(ref.kind()).append(" \"").append(StringUtil_mxJPO.convertUpdate(ref.name)).append("\"\n");
            }
        }
    }

    /**
     * Writes all referenced children to the {@code _updateBuilder}. In the case
     * of different rows (for portals) the key newrow is written.
     *
     * @param _updateBuilder    update builder
     */
    @Override()
    public void write(final UpdateBuilder_mxJPO _updateBuilder)
    {
        if (!this.isEmpty())  {
            int row = this.first().row;
            for (final AbstractRef ref : this)  {
                if (row != ref.row)  {
                    _updateBuilder.stepStartNewLine().stepSingle("newrow").stepEndLine();
                    row = ref.row;
                }
                _updateBuilder.string(ref.kind(), ref.name);

            }
        }
     }

    /**
     * Calculates the delta between this current child references and the
     * {@code _current} references and appends the MQL append commands
     * to {@code _mql}.
     *
     * @param _mql      mql builder to append the MQL commands
     * @param _current  current format definition
     */
    public void calcDelta(final MultiLineMqlBuilder _mql,
                          final WriteAppendChildSyntax _syntax,
                          final ChildRefList_mxJPO _current)
    {
        // ignore equal definitions
        final Iterator<AbstractRef> iterCurrent = _current.iterator();
        final Iterator<AbstractRef> iterTarget  = this.iterator();
        AbstractRef childThis   = null;
        AbstractRef childTarget = null;
        AbstractRef prevChildTarget = null;
        boolean equal = true;
        while (iterCurrent.hasNext() && iterTarget.hasNext())  {
            childThis   = iterCurrent.next();
            childTarget = iterTarget.next();
            if ((childThis.row != childTarget.row) || !childThis.name.equals(childTarget.name) || !childThis.kind().equals(childTarget.kind()))  {
                equal = false;
                break;
            }
            prevChildTarget = childTarget;
        }
        // remove current children if needed
        if (!equal)  {
            _mql.newLine().cmd("remove ").cmd(childThis.kind()).cmd(" ").arg(childThis.name);
        }
        while (iterCurrent.hasNext())  {
            childThis   = iterCurrent.next();
            _mql.newLine().cmd("remove ").cmd(childThis.kind()).cmd(" ").arg(childThis.name);
        }
        // assign targets if needed
        switch (_syntax)  {
            case Add:
                if (!equal)  {
                    _mql.newLine().cmd("add ").cmd(childTarget.kind()).cmd(" ").arg(childTarget.name);
                }
                while (iterTarget.hasNext())  {
                    childTarget = iterTarget.next();
                    _mql.newLine().cmd("add ").cmd(childTarget.kind()).cmd(" ").arg(childTarget.name);
                }
                break;
            case Place:
                if (!equal)  {
                    _mql.newLine().cmd("place ").arg(childTarget.name).cmd(" after ").arg("");
                }
                while (iterTarget.hasNext())  {
                    childTarget = iterTarget.next();
                    _mql.newLine().cmd("place ").arg(childTarget.name).cmd(" after ").arg("");
                }
                break;
            case PlaceWithNewRow:
                if (!equal)  {
                    _mql.newLine().cmd("place ").arg(childTarget.name);
                    if ((prevChildTarget != null) && (childTarget.row != prevChildTarget.row))  {
                        _mql.cmd(" newrow");
                    }
                    _mql.cmd(" after ").arg("");
                    prevChildTarget = childTarget;
                }
                while (iterTarget.hasNext())  {
                    childTarget = iterTarget.next();
                    _mql.newLine().cmd("place ").arg(childTarget.name);
                    if ((prevChildTarget != null) && (childTarget.row != prevChildTarget.row))  {
                        _mql.cmd(" newrow");
                    }
                    _mql.cmd(" after ").arg("");
                    prevChildTarget = childTarget;
                }
                break;
        }
    }

    /**
     * Used to define how children are appended within the
     * {@link ChildRefList_mxJPO#calcDelta(MultiLineMqlBuilder, ChildRefList_mxJPO)
     * delta calculation}.
     */
    public enum WriteAppendChildSyntax
    {
        /** For menus, each child must be added. */
        Add,
        /** For channels, each child must be placed (with after '' as suffix). */
        Place,
        /** For portals, each child must be placed (with after '' as suffix) and needed new row tag. */
        PlaceWithNewRow
    }

    /**
     * Common definition of a children reference.
     */
    public static abstract class AbstractRef
        implements Comparable<AbstractRef>
    {
        /** Row (also order) and column of the reference. */
        int row, column;
        /** Name of the reference. */
        String name;

        /**
         * Returns the kind of the reference.
         *
         * @return kind of the reference
         */
        abstract String kind();

        @Override()
        public int compareTo(final AbstractRef _toCompare)
        {
            int ret = new Integer(this.row).compareTo(_toCompare.row);
            if (ret == 0)  {
                ret = new Integer(this.column).compareTo(_toCompare.column);
            }

            return ret;
        }
    }

    /**
     * References all kind of commands like command, menu or channels.
     */
    public static class ChannelRef
        extends AbstractRef
    {
        @Override()
        String kind()
        {
            return "channel";
        }
    }

    /**
     * References all kind of commands like command, menu or channels.
     */
    public static class CommandRef
        extends AbstractRef
    {
        @Override()
        String kind()
        {
            return "command";
        }
    }

    /**
     * References all kind of commands like command, menu or channels.
     */
    public static class MenuRef
        extends AbstractRef
    {
        @Override()
        String kind()
        {
            return "menu";
        }
    }
}

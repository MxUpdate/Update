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

package org.mxupdate.update.system;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateLine;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

import matrix.util.MatrixException;

/**
 * Handles the export and update of &quot;index&quot;.
 *
 * @author The MxUpdate Team
 */
abstract class AbstractIndexCI_mxJPO<CLASS extends AbstractIndexCI_mxJPO<CLASS>>
    extends AbstractAdminObject_mxJPO<CLASS>
{
    /** Set of all ignored URLs from the XML definition for packages. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        AbstractIndexCI_mxJPO.IGNORED_URLS.add("/attributeDefRefList");
    };

    /** Enabled and global flag. */
    private boolean enable = false;
    /** List of fields. */
    private final Stack<Field> fields = new Stack<>();

    /**
     * Constructor used to initialize the unique key configuration item.
     *
     * @param _mxClass  defines the related MX class enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractIndexCI_mxJPO(final EMxAdmin_mxJPO _mxClassDef,
                                    final String _mxName)
    {
        super(_mxClassDef, _mxName);
    }

    /**
     * Returns {@link #enable} flag.
     *
     * @return is enabled
     */
    protected boolean isEnable()
    {
        return this.enable;
    }

    /**
     * Returns the {@link #fields}.
     *
     * @return fields
     */
    protected Collection<Field> getFields()
    {
        return this.fields;
    }

    @Override
    public void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException, ParseException
    {
        super.parse(_paramCache);

        // evaluate enabled flag directly (because not included in the XML export)
        this.enable = Boolean.valueOf(MqlBuilderUtil_mxJPO.mql().cmd("escape print uniquekey ").arg(this.getName()).cmd(" select ").arg("enabled").cmd(" dump").exec(_paramCache.getContext()));
    }

    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (AbstractIndexCI_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;

        } else if ("/attributeDefRefList/attributeDefRef".equals(_url))  {
            this.fields.push(new Field());
            this.fields.peek().expression = _content;
            parsed = true;
        } else if ("/attributeDefRefList/attributeSize".equals(_url))  {
            this.fields.peek().size = Integer.valueOf(_content) + 4;
            parsed = true;

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }

        return parsed;
    }

    /**
     * Sorted the {@link #fields} and derived information.
     */
    @Override
    protected void prepare()
    {
        super.prepare();
        Collections.sort(this.fields);
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final CLASS _current)
        throws UpdateException_mxJPO
    {
        final AbstractIndexCI_mxJPO<CLASS> current = _current;

        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(  _mql,              "description",              this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "hidden",            false, this.isHidden(),        _current.isHidden());

        boolean enabled = current.enable;

        // remove fields
        for (final Field curField : current.fields)  {
            Field tarField = null;
            for (final Field tmpField : this.fields)  {
                if (curField.expression.equals(tmpField.expression))  {
                    tarField = tmpField;
                    break;
                }
            }
            if (tarField == null)  {
                if (enabled)  {
                    _paramCache.logTrace("    - disable unique key");
                    _mql.pushPrefix("escape disable uniquekey $1", this.getName()).newLine().popPrefix();
                    enabled = false;
                }
                _paramCache.logTrace("    - remove field " + curField.expression);
                _mql.newLine().cmd("remove field ").arg(curField.expression);
            }
        }
        // add / update fields
        for (final Field tarField : this.fields)  {
            Field curField = null;
            for (final Field tmpField : current.fields)  {
                if (tarField.expression.equals(tmpField.expression))  {
                    curField = tmpField;
                    break;
                }
            }
            if (curField == null)  {
                if (enabled)  {
                    _paramCache.logTrace("    - disable unique key");
                    _mql.pushPrefix("escape disable uniquekey $1", this.getName()).newLine().popPrefix();
                    enabled = false;
                }
                _paramCache.logTrace("    - add field " + tarField.expression);
                _mql.newLine().cmd("add field ").arg(tarField.expression);
                if (tarField.expression.startsWith("attribute["))  {
                    _mql.cmd(" size ").arg(String.valueOf(tarField.size));
                }
            } else if ((curField.size != tarField.size) && tarField.expression.startsWith("attribute["))  {
                _mql.newLine().cmd("modify field ").arg(tarField.expression).cmd(" size ").arg(String.valueOf(tarField.size));
            }
        }

        this.getProperties().calcDelta(_mql, "", _current.getProperties());

        if (this.enable && !enabled)  {
            _paramCache.logTrace("    - enable unique key");
            _mql.pushPrefix("escape enable uniquekey $1", this.getName()).newLine().popPrefix();
        } else if (!this.enable && enabled)  {
            _paramCache.logTrace("    - disable unique key");
            _mql.pushPrefix("escape disable uniquekey $1", this.getName()).newLine().popPrefix();
        }
    }

    /**
     * One field.
     */
    public static class Field
        implements Comparable<Field>, UpdateLine
    {
        /** Field expression. */
        private String expression;
        /** Field size. */
        private int size;

        @Override
        public int compareTo(final Field _toCompare)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.expression, _toCompare.expression);
            ret = CompareToUtil_mxJPO.compare(ret, this.size,       _toCompare.size);
            return ret;
        }

        @Override
        public void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            _updateBuilder.stepStartNewLine().stepSingle("field").stepString(this.expression);
            if (this.expression.startsWith("attribute["))  {
                _updateBuilder.stepSingle("size").stepSingle(String.valueOf(this.size));
            }
            _updateBuilder.stepEndLine();
        }
    }
}

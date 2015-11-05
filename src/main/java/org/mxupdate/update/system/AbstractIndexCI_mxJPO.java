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

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateLine;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MqlBuilder;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.util.StringUtils_mxJPO;

import matrix.util.MatrixException;

/**
 * Handles the export and update of &quot;system unqiue keys&quot;.
 * The handled properties are:
 * <ul>
 * <li>uuid</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #global} flag</li>
 * <li>{@link #enable} flag</li>
 * <li>for {@link #forType type} or {@link #forRelation relationship} with
 *     {@link #withInterface interface}</li>
 * <li>list of {@link #fields} with depending sizes</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class UniqueKeyCI_mxJPO
    extends AbstractAdminObject_mxJPO<UniqueKeyCI_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for packages. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        UniqueKeyCI_mxJPO.IGNORED_URLS.add("/attributeDefRefList");
        UniqueKeyCI_mxJPO.IGNORED_URLS.add("/typeRefList");
    };

    /** Enabled and global flag. */
    private boolean enable, global = false;
    /** Unique key is defined for relationship / type with interface. */
    private String forRelation, forType, withInterface;
    /** List of fields. */
    private final Stack<Field> fields = new Stack<>();

    /**
     * Initializes this system unique key configuration item.
     *
     * @param _mxName   name of package
     */
    public UniqueKeyCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.UniqueKey, _mxName);
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new UniqueKeyParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
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
        if (UniqueKeyCI_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;

        } else if ("/attributeDefRefList/attributeDefRef".equals(_url))  {
            this.fields.push(new Field());
            this.fields.peek().expression = _content;
            parsed = true;
        } else if ("/attributeDefRefList/attributeSize".equals(_url))  {
            this.fields.peek().size = Integer.valueOf(_content) + 4;
            parsed = true;

        } else if ("/global".equals(_url))  {
            this.global = true;
            parsed = true;
        } else if ("/interfaceTypeRef".equals(_url))  {
            this.withInterface = _content;
            parsed = true;
        } else if ("/relationshipDefRef".equals(_url))  {
            this.forRelation = _content;
            parsed = true;
        } else if ("/typeRefList/typeRef".equals(_url))  {
            this.forType = _content;
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
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag            | default | value                              | write?
                .stringNotNull( "uuid",                    this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",            this.getSymbolicNames())
                .string(        "description",             this.getDescription())
                .flag(          "hidden",           false, this.isHidden())
                .flag(          "enable",           false, this.enable)
                .flagIfTrue(    "global",           false, this.global,                         this.global)
                .stringNotNull( "for type",                this.forType)
                .stringNotNull( "for relationship",        this.forRelation)
                .stringNotNull( "with interface",          this.withInterface)
                .list(this.fields)
                .properties(this.getProperties());
    }

    @Override
    public void createOld(final ParameterCache_mxJPO _paramCache)
    {
    }

    @Override
    public void create(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final MqlBuilder mql = MqlBuilderUtil_mxJPO.mql().cmd("escape add uniquekey ").arg(this.getName());
        if (!StringUtils_mxJPO.isEmpty(this.forType))  {
            mql.cmd(" type ").arg(this.forType);
        } else if (!StringUtils_mxJPO.isEmpty(this.forRelation))  {
            mql.cmd(" relationship ").arg(this.forRelation);
        }
        if (!StringUtils_mxJPO.isEmpty(this.withInterface))  {
            mql.cmd(" interface ").arg(this.withInterface);
        }
        if (this.global)  {
            mql.cmd(" global");
        }
        mql.exec(_paramCache.getContext());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final UniqueKeyCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        int bck = 0;
        bck = CompareToUtil_mxJPO.compare(bck, this.forRelation,    _current.forRelation);
        bck = CompareToUtil_mxJPO.compare(bck, this.forType,        _current.forType);
        bck = CompareToUtil_mxJPO.compare(bck, this.withInterface,  _current.withInterface);
        bck = CompareToUtil_mxJPO.compare(bck, this.global,         _current.global);
        if (bck != 0)  {
            throw new UpdateException_mxJPO(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED, this.getName());
        }

        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(  _mql,              "description",              this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "hidden",            false, this.isHidden(),        _current.isHidden());

        boolean enabled = _current.enable;

        // remove fields
        for (final Field curField : _current.fields)  {
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
            for (final Field tmpField : _current.fields)  {
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

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

package org.mxupdate.update.util;

import java.text.MessageFormat;
import java.util.Collection;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;

/**
 * Builder used to generated update files.
 *
 * @author The MxUpdate Team
 */
public class UpdateBuilder_mxJPO
{
    /** Four spaces are used as prefix characters. */
    private final static int PREFIX_CHARS = 4;

    /** Parameter cache. */
    private final ParameterCache_mxJPO paramCache;
    /** Current needed amount of prefix'. */
    private int prefixAmount = 1;
    /** Generated string. */
    private final StringBuilder strg = new StringBuilder();

    /**
     * Initializes the update builder.
     *
     * @param _fileName     name of file
     * @param _paramCache   parameter cache
     */
    public UpdateBuilder_mxJPO(final String _fileName,
                               final ParameterCache_mxJPO _paramCache)
    {
        this.paramCache = _paramCache;
        if (_paramCache != null)  {
            this.strg.append(MessageFormat.format(_paramCache.getValueString(ValueKeys.ExportFileHeader), _fileName));
        }
    }

    /**
     * Starts the update builder for given type definition.
     *
     * @param _typeDef      type definition
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO start(final TypeDef_mxJPO _typeDef)
    {
        this.strg.append("mxUpdate ").append(_typeDef.getMxAdminName()).append(" \"${NAME}\" ");
        if (_typeDef.getMxBusType() != null)  {
            this.strg.append("\"${REVISION}\"  ");
        }
        this.strg.append("{\n");
        return this;
    }

    /**
     * Ends the update builder.
     *
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO end()
    {
        this.strg.append("}");
        return this;
    }

    /**
     * Appends given {@code _value} string surrounded with apostrophes.
     *
     * @param _tag      tag
     * @param _value    value
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO string(final String _tag,
                                      final String _value)
    {
        return this.stepStartNewLine().stepSingle(_tag).stepString(_value).stepEndLine();
    }

    /**
     * Appends given {@code _value} string surrounded with apostrophes if
     * {@code _write} is <i>true</i>.
     *
     * @param _tag      tag
     * @param _value    value
     * @param _write    flag must be <i>true</i> that the value is written
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO stringIfTrue(final String _tag,
                                            final String _value,
                                            final boolean _write)
    {
        if (_write)  {
            this.string(_tag, _value);
        }
        return this;
    }

    /**
     * Appends given {@code _code} string surrounded with apostrophes and new
     * lines.
     *
     * @param _tag      tag
     * @param _code     code
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO code(final String _tag,
                                    final String _code)
    {
        this.string(_tag, ((_code != null) && (_code.indexOf('\n') > 0)) ? ("\n" + _code + "\n") : _code);
        return this;
    }

    /**
     * Appends given {@code _code} string surrounded with apostrophes and new
     * lines if {@code _write} is <i>true</i>.
     *
     * @param _tag      tag
     * @param _code     code
     * @param _write    flag must be <i>true</i> that the code is written
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO codeIfTrue(final String _tag,
                                          final String _code,
                                          final boolean _write)
    {
        if (_write)  {
            this.code(_tag, _code);
        }
        return this;
    }

    /**
     * Appends given single {@code _value} w/o apostrophes.
     *
     * @param _tag      tag
     * @param _value    value
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO single(final String _tag,
                                      final String _value)
    {
        return this.stepStartNewLine().stepSingle(_tag).stepSingle(_value).stepEndLine();
    }

    /**
     * Appends given single {@code _value} if {@code _write} is <i>true</i>.
     *
     * @param _tag      tag
     * @param _value    value
     * @param _write    flag must be <i>true</i> that the value is written
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO singleIfTrue(final String _tag,
                                            final String _value,
                                            final boolean _write)
    {
        if (_write)  {
            this.single(_tag, _value);
        }
        return this;
    }

    /**
     * Appends flag {@code _tag} for given {@code _value}.
     *
     * @param _tag              tag
     * @param _defaultValue     default value for {@code _value} if
     *                          {@code _value} is {@code null}
     * @param _value            value
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO flag(final String _tag,
                                    final boolean _defaultValue,
                                    final Boolean _value)
    {
        final boolean value = (_value != null) ? _value : _defaultValue;

        return this.stepStartNewLine().stepSingle(value ? _tag : ("!" + _tag)).stepEndLine();
    }

    /**
     * Appends flag {@code _tag} for given {@code _value} if {@code _write} is
     * <i>true</i>.
     *
     * @param _tag          tag
     * @param _defaultValue default value for {@code _value} if {@code _value}
     *                      is {@code null}
     * @param _value        value
     * @param _write        flag must be <i>true</i> that the value is written
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO flagIfTrue(final String _tag,
                                          final boolean _defaultValue,
                                          final Boolean _value,
                                          final boolean _write)
    {

        if (_write)  {
            this.flag(_tag, _defaultValue, _value);
        }

        return this;
    }

    /**
     * Appends {@code _list} for {@code _tag}.
     *
     * @param _tag      tag
     * @param _list     list to append
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO list(final String _tag,
                                    final Collection<String> _list)
    {
        for (final Object value : _list)  {
            this.string(_tag, (String) value);
        }
        return this;
    }

    /**
     * Appends {@code _list} for {@code _tag} if {@code _write} is <i>true</i>.
     *
     * @param _tag      tag
     * @param _list     list to append
     * @param _write    flag must be <i>true</i> that the value is written
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO listIfTrue(final String _tag,
                                          final Collection<String> _list,
                                          final boolean _write)
    {
        if (_write)  {
            this.list(_tag, _list);
        }
        return this;
    }

    /**
     * Appends {@code _list} for {@code _tag} inside a single line.
     *
     * @param _tag      tag
     * @param _list     list to append
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO listOneLineSingle(final String _tag,
                                                 final Collection<String> _list)
    {
        this.stepStartNewLine().stepSingle(_tag);
        this.strg.append(" {");
        this.first = true;
        for (final String value : _list)  {
            this.stepSingle(value);
        }
        this.strg.append("}");
        this.stepEndLine();
        return this;
    }

    /**
     * Appends {@code _list} for {@code _tag} inside a single line if
     * {@code _write} is <i>true</i>.
     *
     * @param _tag      tag
     * @param _list     list to append
     * @param _write    flag must be <i>true</i> that the value is written
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO listOneLineSingleIfTrue(final String _tag,
                                                       final Collection<String> _list,
                                                       final boolean _write)
    {
        if (_write)  {
            this.listOneLineSingle(_tag, _list);
        }
        return this;
    }

    /**
     * Appends {@code _list} for {@code _tag} inside a single line.
     *
     * @param _tag      tag
     * @param _list     list to append
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO listOneLine(final String _tag,
                                           final Collection<String> _list)
    {
        this.stepStartNewLine().stepSingle(_tag);
        this.strg.append(" {");
        this.first = true;
        for (final String value : _list)  {
            this.stepString(value);
        }
        this.strg.append("}");
        this.stepEndLine();
        return this;
    }

    /**
     * Appends {@code _list} for {@code _tag} inside a single line if
     * {@code _write} is <i>true</i>.
     *
     * @param _tag      tag
     * @param _list     list to append
     * @param _write    flag must be <i>true</i> that the value is written
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO listOneLineIfTrue(final String _tag,
            final Collection<String> _list,
            final boolean _write)
    {
        if (_write)  {
            this.listOneLine(_tag, _list);
        }
        return this;
    }

    /**
     * Appends {@code _list}.
     *
     * @param _list     list to append
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO list(final Collection<? extends UpdateLine> _list)
    {
        for (final UpdateLine value : _list)  {
            value.write(this);
        }
        return this;
    }

    /**
     * Appends {@code _list}.
     *
     * @param _list     list to append
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO write(final UpdateList _list)
    {
        _list.write(this);
        return this;
    }

    /**
     * Appends all properties.
     *
     * @param _propList property list
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO properties(final AdminPropertyList_mxJPO _propList)
    {
        _propList.writeProperties(this.paramCache, this);
        return this;
    }

    /**
     * Appends all settings.
     *
     * @param _propList property list
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO otherProps(final AdminPropertyList_mxJPO _propList)
    {
        _propList.writeOtherProps(this.paramCache, this);
        return this;
    }

    /**
     * Starts a new child.
     *
     * @param _tag      tag
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO childStart(final String _tag)
    {
        this.stepStartNewLine().stepSingle(_tag).stepEndLineWithStartChild();
        return this;
    }

    /**
     * Ends a child.
     *
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO childEnd()
    {
        this.prefixAmount--;
        this.stepStartNewLine().stepSingle("}").stepEndLine();
        return this;
    }

    private boolean first = false;

    public UpdateBuilder_mxJPO stepStartNewLine()
    {
        this.strg.append(this.prefix());
        this.first = true;
        return this;
    }

    /**
     * Step a new string value.
     *
     * @param _value    string value
     * @return this update builder
     */
    public UpdateBuilder_mxJPO stepString(final String _value)
    {
        if (this.first)  {
            this.first = false;
        } else  {
            this.strg.append(' ');
        }
        this.strg.append('\"').append(StringUtil_mxJPO.convertUpdate(_value)).append('\"');
        return this;
    }

    /**
     * Step a new single value.
     *
     * @param _value    single value
     * @return this update builder
     */
    public UpdateBuilder_mxJPO stepSingle(final String _value)
    {
        if (this.first)  {
            this.first = false;
        } else  {
            this.strg.append(' ');
        }
        this.strg.append(_value);
        return this;
    }

    /**
     * End line with starting new child.
     *
     * @return this update builder
     */
    public UpdateBuilder_mxJPO stepEndLineWithStartChild()
    {
        this.strg.append(" {\n");
        this.prefixAmount++;
        return this;
    }

    public UpdateBuilder_mxJPO stepEndLine()
    {
        this.strg.append('\n');
        return this;
    }

    /**
     * Returns current used prefix string.
     *
     * @return current used prefix string
     */
    private String prefix()
    {
      return String.format("%" + (this.prefixAmount * UpdateBuilder_mxJPO.PREFIX_CHARS) + "s", "");
    }

    /**
     * Returns the {@link #paramCache parameter cache}.
     *
     * @return parameter cache
     */
    public ParameterCache_mxJPO getParamCache()
    {
        return this.paramCache;
    }

    /**
     * Returns the {@link #strg generated string}.
     *
     * @return string
     */
    @Override()
    public String toString()
    {
        return this.strg.toString();
    }

    /**
     * One line of the update.
     */
    public interface UpdateLine
    {
        void write(final UpdateBuilder_mxJPO _updateBuilder);
    }

    /**
     * List of elements which are written.
     */
    public interface UpdateList
    {
        void write(final UpdateBuilder_mxJPO _updateBuilder);
    }
}

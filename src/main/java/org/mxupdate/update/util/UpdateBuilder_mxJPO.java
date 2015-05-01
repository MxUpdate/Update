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

import java.util.Collection;

/**
 * Builder used to generated update files.
 *
 * @author The MxUpdate Team
 */
public class UpdateBuilder_mxJPO
{
    /** Four spaces are used as prefix characters. */
    private final static int PREFIX_CHARS = 4;

    private int prefixAmount = 1;

    /** Generated string. */
    private final StringBuilder strg = new StringBuilder();

    /**
     * Starts the update builder.
     *
     * @param _mxAdminType      MX admin type to update
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO start(final String _mxAdminType)
    {
        this.strg.append("mxUpdate ").append(_mxAdminType).append(" \"${NAME}\" {\n");
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
        this.strg.append(this.prefix()).append(_tag).append(" \"").append(StringUtil_mxJPO.convertUpdate(_value)).append("\"\n");
        return this;
    }

    /**
     * Appends given {@code _value} string surrounded with apostrophes if
     * {@code _value} is not {@code null}.
     *
     * @param _tag      tag
     * @param _value    value
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO stringIfNotEmpty(final String _tag,
                                               final String _value)
    {
        if ((_value != null) && !_value.isEmpty())  {
            this.strg.append(this.prefix()).append(_tag).append(" \"").append(StringUtil_mxJPO.convertUpdate(_value)).append("\"\n");
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
        this.strg.append(this.prefix()).append(_tag).append(' ').append(_value).append('\n');
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

        this.strg.append(this.prefix()).append(value ? "" : '!').append(_tag).append('\n');

        return this;
    }

    /**
     * Appends flag {@code _tag} for given {@code _value} if <i>true</i>.
     *
     * @param _tag              tag
     * @param _defaultValue     default value for {@code _value} if
     *                          {@code _value} is {@code null}
     * @param _value            value
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO flagIfTrue(final String _tag,
                                          final boolean _defaultValue,
                                          final Boolean _value)
    {
        final boolean value = (_value != null) ? _value : _defaultValue;

        if (value)  {
            this.strg.append(this.prefix()).append(_tag).append('\n');
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
        for (final String value : _list)  {
            this.string(_tag, value);
        }

        return this;
    }

    /**
     * Appends {@code _line} in update format to this definition.
     *
     * @param _line     line in update format
     * @return this update builder instance
     */
    public UpdateBuilder_mxJPO line(final CharSequence _line)
    {
        this.strg.append(this.prefix()).append(_line).append('\n');
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
        this.strg.append(this.prefix()).append(_tag).append(" {\n");
        this.prefixAmount++;
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
        this.strg.append(this.prefix()).append("}\n");
        return this;
    }

    public StringBuilder getStrg()
    {
        return this.strg;
    }

    /**
     * Returns current used prefix string.
     *
     * @return current used prefix string
     */
    public String prefix()
    {
      return String.format("%" + (this.prefixAmount * UpdateBuilder_mxJPO.PREFIX_CHARS) + "s", "");
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
}

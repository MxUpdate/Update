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

package org.mxupdate.update.user.workspace;

import java.io.IOException;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * User specific class to store the workspace object for one filter.
 *
 * @author The MxUpdate Team
 */
public class Filter_mxJPO
    extends AbstractVisualQueryWorkspaceObject_mxJPO
{
    /**
     * Direction of the connection to which the filter applies to. The default
     * value is the the filter applies to both (from and to) directions.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private Direction direction;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     */
    public Filter_mxJPO(final AbstractUser_mxJPO _user)
    {
        super(_user, "filter");
    }

    /**
     * <p>Parses all filter specific URL values. This includes:
     * <ul>
     * <li>{@link #direction}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if ("/traverseBackwards".equals(_url))  {
            if ((this.direction != null) && (this.direction == Direction.FROM))  {
                this.direction = Direction.BOTH;
            } else  {
                this.direction = Direction.TO;
            }
            parsed = true;
        } else if ("/traverseForewards".equals(_url))  {
            if ((this.direction != null) && (this.direction == Direction.TO))  {
                this.direction = Direction.BOTH;
            } else  {
                this.direction = Direction.FROM;
            }
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * <p>Writes all filter specific values to the TCL update file
     * <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #direction}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    @Override()
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
        throws IOException
    {
        super.write(_paramCache, _out);
        if (this.direction != null)  {
            _out.append(" \\\n    ").append(this.direction.mxValue);
        }
    }

    /**
     * Enumeration for the direction of connection with a filter definition.
     */
    public enum Direction
    {
        /**
         * To direction.
         */
        TO("to"),

        /**
         * From direction.
         */
        FROM("from"),

        /**
         * Both (to and from) directions.
         */
        BOTH("both");

        /**
         * Related value used from MX.
         */
        private final String mxValue;

        /**
         * Initializes the {@link #mxValue value within MX}.
         *
         * @param _mxValue  related value within MX
         */
        private Direction(final String _mxValue)
        {
            this.mxValue = _mxValue;
        }
    }
}

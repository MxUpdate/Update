/*
 * Copyright 2008-2014 The MxUpdate Team
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

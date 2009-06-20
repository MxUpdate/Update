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

package org.mxupdate.update.util;

/**
 * Exception class to throw errors within updates (if a check for an update
 * failed).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@SuppressWarnings("serial")
public class UpdateException_mxJPO
    extends Exception
{
    /**
     * <p>Enumeration for all known update errors. The codes itself are
     * &quot;speaking&quot; numbers:
     * <ul>
     * <li>the first digit defines data model (1), program (2), user (3) or
     *     user interface (4)</li>
     * <li>depending of the first digit, the second and third digit defines the
     *     kind of configuration item
     *     <ul>
     *     <li>106: dimension</li>
     *     </ul></li>
     * <li>the fourth and fifth digit defines the error itself</li>
     * </ul></p>
     *
     * <p><b>Example:</b><br/>
     * 10601: 1 for data model, 06 for dimension, 01 for the error</p>
     */
    public enum Error
    {
        /**
         * The multiplier of a unit of a dimension is tried to update which
         * means that potentially some data could be loosed.
         *
         * @see org.mxupdate.update.datamodel.Dimension_mxJPO
         */
        DIMENSION_UPDATE_MULTIPLIER(10601,
                "dimension multiplier could not updated (data will be changed potentially)"),

        /**
         * The offset of a unit of a dimension is tried to update which means
         * that potentially some data could be loosed.
         *
         * @see org.mxupdate.update.datamodel.Dimension_mxJPO
         */
        DIMENSION_UPDATE_OFFSET(10602,
                "dimension offset could not updated (data will be changed potentially)"),

        /**
         * The default unit of a dimension is tried to update which means
         * that potentially some data could be loosed. FYI: MX itself also
         * tries to prevent this if a default unit is already defined, but it
         * could be that this case does only happens e.g. in non development
         * systems....
         *
         * @see org.mxupdate.update.datamodel.Dimension_mxJPO
         */
        DIMENSION_UPDATE_DEFAULTUNIT(10603,
                "dimension default unit could not updated (data will be changed potentially)"),

        /**
         * An unit of a dimension is tried to remove which means that
         * potentially some data could be loosed. FYI: MX itself also
         * tries to prevent this if a unit is used, but it could be that this
         * case does only happens e.g. in non development systems....
         *
         * @see org.mxupdate.update.datamodel.Dimension_mxJPO
         */
        DIMENSION_UPDATE_REMOVEUNIT(10604,
                "removing an unit from a dimension is not allowed (data will be changed potentially)");

        /**
         * Error code of this error enumeration.
         */
        private final int code;

        /**
         * Default English error message for this error enumeration.
         */
        private final String text;

        /**
         * Constructor for the error enumeration to initialize the error
         * {@link #code} and error {@link #text}.
         *
         * @param _code     error code
         * @param _text     error message
         */
        Error(final int _code,
              final String _text)
        {
            this.code = _code;
            this.text = _text;
        }

        /**
         * Returns the error code of this error enumeration.
         *
         * @return error code
         * @see #code
         */
        public int getCode()
        {
            return this.code;
        }

        /**
         * Returns the error message of this error enumeration.
         *
         * @return error message as English text
         * @see #text
         */
        public String getText()
        {
            return this.text;
        }
    }

    /**
     * Related error enumerator of this update exception.
     */
    private final Error error;

    /**
     * Constructor to initialize this exception. The exception message will
     * be a concatenation of the {@link Error#code} and {@link Error#text}.
     *
     * @param _error    error enumeration instance
     * @see #error
     */
    public UpdateException_mxJPO(final Error _error)
    {
        super("UpdateError #" + _error.getCode() + ": " + _error.getText());
        this.error = _error;
    }

    /**
     *
     * @return related error enumeration instance for which this exception is
     *         thrown
     * @see #error
     */
    public Error getError()
    {
        return this.error;
    }
}

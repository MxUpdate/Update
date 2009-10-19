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

import java.text.MessageFormat;

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
     * &quot;speaking&quot; numbers.</p>
     * <p>For classes implementing specific exports / updates for an
     * administration object, following algorithm is used:
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
     * <p>Exceptions from utility classes (package
     *    org.mxupdate.update.util) starts with 90. The next digit
     *    defines the class where the exception is thrown:
     * <ul>
     * <li>1: {@link org.mxupdate.update.util.AbstractParser_mxJPO}</li>
     * <li>2: {@link org.mxupdate.update.util.AdminProperty_mxJPO}</li>
     * <li>3: {@link org.mxupdate.update.util.JPOCaller_mxJPO}</li>
     * <li>4: {@link org.mxupdate.update.util.MqlUtil_mxJPO}</li>
     * <li>5: {@link org.mxupdate.update.util.ParameterCache_mxJPO}</li>
     * <li>6: {@link org.mxupdate.update.util.StringUtil_mxJPO}</li>
     * </ul>
     * </p>
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
                "removing an unit from a dimension is not allowed (data will be changed potentially)"),

        /**
         * <p>A wrong parameter was given the the called TCL procedure
         * <code>testParents</code> which defines the derived interfaces.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>wrong parameter</li>
         * </ol>
         * </p>
         *
         * @see org.mxupdate.update.datamodel.Interface_mxJPO
         */
        DM_INTERFACE_UPDATE_UKNOWN_PARAMETER(10901, "Unknown parameter {0} defined."),

        /**
         * <p>The name of the interface which calls the TCL procedure
         * <code>testParents</code> and which is defined within the call as
         * parameter is not equal.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>administration type (should be interface)</li>
         * <li>name of the interface which calls the TCL update procedure</li>
         * <li>name of the interface which is defined as parameter for the TCL
         *     update procedure</li>
         * </ol>
         * </p>
         *
         * @see org.mxupdate.update.datamodel.Interface_mxJPO
         */
        DM_INTERFACE_UPDATE_WRONG_NAME(10902,
                "{0} ''{1}'' was called to update via update script, but {0} ''{1}'' was called in the TCL procedure."),

        /**
         * <p>An interface is already derived from another interface, but
         * within the update this derived interface must be removed. This could
         * end in potentially losing data and so this action is not allowed.</p>
         *
         * <p>Parameters:<br/>
         * <ol>
         * <li>administration type (should be interface)</li>
         * <li>interface which is tried to update</li>
         * <li>current derived interface which must be removed (but is not
         *     allowed)</li>
         * </ol>
         * </p>
         *
         * @see org.mxupdate.update.datamodel.Interface_mxJPO
         */
        DM_INTERFACE_UPDATE_REMOVING_PARENT(10903,
                "Current parent {0} ''{2}'' must be removed from {0} ''{1}''. This is not allowed!"),

        /**
         * If the name of a configuration item could not be extracted from a
         * file name.
         *
         * @see org.mxupdate.update.util.StringUtil_mxJPO#convertFromFileName(String)
         */
        UTIL_STRINGUTIL_CONVERT_FROM_FILENAME(90602,
                "the file name is not correct defined and could not be converted back to a configuration item name");

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
     * @param _error        error enumeration instance
     * @param _arguments    arguments for the exception text used to format the
     *                      message
     * @see #error
     */
    public UpdateException_mxJPO(final Error _error,
                                 final Object... _arguments)
    {
        super("UpdateError #" + _error.getCode() + ": " +
                MessageFormat.format(_error.getText(), _arguments));
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

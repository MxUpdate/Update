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

/**
 * Exception class to throw errors within updates (if a check for an update
 * failed).
 *
 * @author The MxUpdate Team
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
     *     user interface (4), integration (5) and abstract (6)</li>
     * <li>depending of the first digit, the second and third digit defines the
     *     kind of configuration item
     *     <ul>
     *     <li>106: {@link org.mxupdate.update.datamodel.Dimension_mxJPO dimension}</li>
     *     <li>109: {@link org.mxupdate.update.datamodel.Interface_mxJPO interface}</li>
     *     <li>111: {@link org.mxupdate.update.datamodel.Policy_mxJPO policy}</li>
     *     <li>114: {@link org.mxupdate.update.datamodel.Relationship_mxJPO relationship}</li>
     *     <li>116: {@link org.mxupdate.update.datamodel.Rule_mxJPO rule}</li>
     *     <li>118: {@link org.mxupdate.update.datamodel.Type_mxJPO type}</li>
     *     <li>120: {@link org.mxupdate.update.datamodel.AttributeCI_mxJPO numeric attributes}</li>
     *     <li>121: {@link org.mxupdate.update.datamodel.AbstractDMWithAttributes_mxJPO data model with attributes}</li>
     *     <li>303: {@link org.mxupdate.update.user.Group_mxJPO group}</li>
     *     <li>309: {@link org.mxupdate.update.user.PersonAdmin_mxJPO admin person}</li>
     *     <li>312: {@link org.mxupdate.update.user.Role_mxJPO role}</li>
     *     <li>315: {@link org.mxupdate.update.user.Group_mxJPO group}</li>
     *     <li>601: {@link org.mxupdate.update.AbstractObject_mxJPO abstract object}</li>
     *     <li>602: {@link org.mxupdate.update.AbstractAdminObject_mxJPO abstract administration object}</li>
     *     <li>603: {@link org.mxupdate.update.AbstractPropertyObject_mxJPO abstract property object}</li>
     *     <li>604: {@link org.mxupdate.update.BusObject_mxJPO business object}</li>
     *     </ul></li>
     * <li>the fourth and fifth digit defines the error itself</li>
     * </ul></p>
     * <p>Exceptions from utility classes (package
     *    org.mxupdate.update.util) starts with 90. The next digit
     *    defines the class where the exception is thrown:
     * <ul>
     * <li>901: {@link org.mxupdate.update.util.AbstractParser_mxJPO}</li>
     * <li>902: {@link org.mxupdate.update.util.AdminProperty_mxJPO}</li>
     * <li>903: {@link org.mxupdate.update.util.JPOCaller_mxJPO}</li>
     * <li>904: {@link org.mxupdate.update.util.MqlUtil_mxJPO}</li>
     * <li>905: {@link org.mxupdate.update.util.ParameterCache_mxJPO}</li>
     * <li>906: {@link org.mxupdate.update.util.StringUtil_mxJPO}</li>
     * </ul>
     * </p>
     *
     * <p><b>Example:</b><br/>
     * 10601: 1 for data model, 06 for dimension, 01 for the error</p>
     */
    public enum ErrorKey
    {
        /**
         * JPO caller method is called without any argument.
         */
        JPOCALLER_JPO_CALL_METHOD_NOT_DEFINED(60301,
                "jpo call is executed without any arguments"),

        /**
         * JPO caller method is called with wrong name for the method.
         */
        JPOCALLER_JPO_CALL_METHOD_UNKNOWN(60302,
                "unknown jpo call execute {0}"),

        /**
         * The multiplier of a unit of a dimension is tried to update which
         * means that potentially some data could be loosed.
         */
        DIMENSION_UPDATE_MULTIPLIER(10601,
                "dimension multiplier could not updated (data will be changed potentially)"),

        /**
         * The offset of a unit of a dimension is tried to update which means
         * that potentially some data could be loosed.
         */
        DIMENSION_UPDATE_OFFSET(10602,
                "dimension offset could not updated (data will be changed potentially)"),

        /**
         * The default unit of a dimension is tried to update which means
         * that potentially some data could be loosed. FYI: MX itself also
         * tries to prevent this if a default unit is already defined, but it
         * could be that this case does only happens e.g. in non development
         * systems....
         */
        DIMENSION_UPDATE_DEFAULTUNIT(10603,
                "dimension default unit could not updated (data will be changed potentially)"),

        /**
         * An unit of a dimension is tried to remove which means that
         * potentially some data could be loosed. FYI: MX itself also
         * tries to prevent this if a unit is used, but it could be that this
         * case does only happens e.g. in non development systems....
         */
        DIMENSION_UPDATE_REMOVEUNIT(10604,
                "removing an unit from a dimension is not allowed (data will be changed potentially)"),

        /**
         * <p>The given dimension of the attribute should be changed to new
         * dimension, but potentially data could be lost.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>attribute name</li>
         * <li>existing dimension</li>
         * <li>new dimension</li>
         * </ol>
         * </p>
         */
        ABSTRACTATTRIBUTE_UPDATE_DIMENSION_UPDATED(12001,
                "The existing dimension ''{1}'' for Attribute ''{0}'' must be changed to new dimension ''{2}'', but not possible because some information could be lost!"),

        /**
         * <p>The attribute contains defined range value flag which must be
         * removed, but potentially data could be lost.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>attribute name</li>
         * </ol>
         * </p>
         */
        ABSTRACTATTRIBUTE_UPDATE_RANGEVALUEFLAG_UPDATED(12002,
                "Attribute ''{0}'' is defined with range values and must be removed, but not possible because some information could be lost!"),

        /**
         * <p>The attribute contains defined multiple value flag which must be
         * removed, but potentially data could be lost.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>attribute name</li>
         * </ol>
         * </p>
         */
        ABSTRACTATTRIBUTE_UPDATE_MULTIVALUEFLAG_UPDATED(12003,
                "Attribute ''{0}'' is defined with multiple values and must be removed, but not possible because some information could be lost!"),

        /**
         * <p>The given attribute is not defined anymore but assigned to the
         * interface object. The attribute is not automatically removed
         * because otherwise potentially data could be lost.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>attribute name</li>
         * <li>interface which is tried to update</li>
         * </ol>
         * </p>
         */
        DM_INTERFACE_REMOVE_ATTRIBUTE(10901,
                "Current attribute ''{0}'' is defined to be removed from interface ''{1}'', but not allowed (because this could potentially meant to loose data)!"),

        /**
         * <p>An interface is already derived from another interface, but
         * within the update this derived interface must be removed. This could
         * end in potentially losing data and so this action is not allowed.</p>
         *
         * <p>Parameters:<br/>
         * <ol>
         * <li>current derived interface which must be removed (but is not
         *     allowed)</li>
         * <li>interface which is tried to update</li>
         * </ol>
         * </p>
         *
         * @see org.mxupdate.update.datamodel.Interface_mxJPO
         */
        DM_INTERFACE_REMOVE_PARENT(10902,
                "Current parent interface ''{0}'' must be removed from interface ''{1}'', but not allowed (because this could potentially meant to loose data)!"),

        /**
         * <p>The delimiter of an existing policy is not equal to the new
         * defined.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>administration type (should be policy)</li>
         * <li>name of the policy</li>
         * <li>current delimiter</li>
         * <li>new delimiter (from the CI file)</li>
         * </ol>
         * </p>
         *
         * @see org.mxupdate.update.datamodel.Policy_mxJPO
         */
        DM_POLICY_UPDATE_DELIMITER(11101,
                "The existing delimiter ''{2}'' of {0} ''{1}'' can be not updated to new delimiter ''{3}'' (limitation of MX)."),

        /**
         * Kind of a relationship can not be changed if the current kind is not
         * basic.
         *
         * <p>Parameters:
         * <ol>
         * <li>name of the relationship</li>
         * <li>current kind</li>
         * <li>new kind (from the CI file)</li>
         * </ol>
         * </p>
         */
        DM_RELATION_NOT_BASIC_KIND(11402,
                "The new kind ''{2}'' can not be set for relationship ''{0}'', because kind ''{1}'' is already set. Kinds of relationship can be only set for ''basic'' relationships."),

        /**
         * Derived of a relationship can not be changed because potentially some
         * data can be lost.
         *
         * <p>Parameters:
         * <ol>
         * <li>name of the relationship</li>
         * <li>current derived relationship</li>
         * <li>new derived relationship (from the CI file)</li>
         * </ol>
         * </p>
         */
        DM_RELATION_UPDATE_DERIVED(11403,
                "The new derived ''{2}'' can not be set for relationship ''{0}'', because derived ''{1}'' is already set and potentially some data can be lost."),

        /**
         * <p>The given attribute is not defined anymore but assigned to the
         * interface object. The attribute is not automatically removed
         * because otherwise potentially data could be lost.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>attribute name</li>
         * <li>relationship which is tried to update</li>
         * </ol>
         * </p>
         */
        DM_RELATION_REMOVE_ATTRIBUTE(11401,
                "Current attribute ''{0}'' is defined to be removed from relationship ''{1}'', but not allowed (because this could potentially meant to loose data)!"),

        /**
         * <p>The given attribute is not defined anymore but assigned to the
         * type object. The attribute is not automatically removed
         * because otherwise potentially data could be lost.</p>
         *
         * <p>Parameters:
         * <ol>
         * <li>attribute name</li>
         * <li>type which is tried to update</li>
         * </ol>
         * </p>
         */
        DM_TYPE_REMOVE_ATTRIBUTE(11801,
                "Current attribute ''{0}'' is defined to be removed from type ''{1}'', but not allowed (because this could potentially meant to loose data)!"),

        /**
         * Kind of a type can not be changed if the current kind is not basic.
         *
         * <p>Parameters:
         * <ol>
         * <li>name of the relationship</li>
         * <li>current kind</li>
         * <li>new kind (from the CI file)</li>
         * </ol>
         * </p>
         */
        DM_TYPE_NOT_BASIC_KIND(11802,
                "The new kind ''{2}'' can not be set for type ''{0}'', because kind ''{1}'' is already set. Kinds of type can be only set for ''basic'' types."),

        /**
         * Derived of a type can not be changed because potentially some
         * data can be lost.
         *
         * <p>Parameters:
         * <ol>
         * <li>name of the type</li>
         * <li>current derived type name</li>
         * <li>new derived type name (from the CI file)</li>
         * </ol>
         * </p>
         */
        DM_TYPE_UPDATE_DERIVED(11803,
                "The new derived ''{2}'' can not be set for type ''{0}'', because derived ''{1}'' is already set and potentially some data can be lost."),

        /**
         * Kind of a role can not be changed if the current kind is not role.
         *
         * <p>Parameters:
         * <ol>
         * <li>name of the role</li>
         * <li>current kind</li>
         * <li>new kind (from the CI file)</li>
         * </ol>
         * </p>
         */
        USER_ROLE_NOT_ROLE_KIND(31201,
                "The new kind ''{2}'' can not be set for role ''{0}'', because kind ''{1}'' is already set. Kinds of role can be only set for ''Role''."),

        /**
         * If the name of a configuration item could not be extracted from a
         * file name.
         *
         * @see org.mxupdate.update.util.StringUtil_mxJPO#convertFromFileName(String)
         */
        UTIL_STRINGUTIL_CONVERT_FROM_FILENAME(90602,
                "the file name is not correct defined and could not be converted back to a configuration item name");

        /** Error code of this error enumeration. */
        private final int code;
        /** Default English error message for this error enumeration. */
        private final String text;

        /**
         * Constructor for the error enumeration to initialize the error
         * {@link #code} and error {@link #text}.
         *
         * @param _code     error code
         * @param _text     error message
         */
        ErrorKey(final int _code,
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
    private final ErrorKey error;

    /**
     * Constructor to initialize this exception. The exception message will
     * be a concatenation of the {@link ErrorKey#code} and {@link ErrorKey#text}.
     *
     * @param _error        error enumeration instance
     * @param _arguments    arguments for the exception text used to format the
     *                      message
     * @see #error
     */
    public UpdateException_mxJPO(final ErrorKey _error,
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
    public ErrorKey getError()
    {
        return this.error;
    }
}

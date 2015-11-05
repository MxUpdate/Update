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

package org.mxupdate.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JPO utility to convert Java to JPO code.
 *
 * <b>Attention!</b> The static variables are defined to separate the string
 * definitions. Otherwise MX interprets them!
 *
 * @author The MxUpdate Team
 */
public class JPOUtil_mxJPO
{
    /** String with name suffix (used also from the extract routine from Matrix). */
    public static final String JPO_NAME_SUFFIX_EXTENDSION = JPOUtil_mxJPO.JPO_NAME_SUFFIX + ".java";
    /** String length of {@link #JPO_NAME_SUFFIX_EXTENDSION}. */
    public static final int JPO_NAME_SUFFIX_EXTENDSION_LENGTH = JPOUtil_mxJPO.JPO_NAME_SUFFIX_EXTENDSION.length();

    /** String with name suffix (used also from the extract routine from Matrix). */
    private static final String JPO_NAME_SUFFIX = "_" + "mxJPO";

    /** Regular expression pattern used to remove package statements. */
    private static final String CONVERT_PATTERN_PACKAGE = "^package( |\\t).*$(\n|\r\n)?";
    /** Regular expression pattern to fetch mx_JPO statements in the Java source code. */
    private static final String CONVERT_PATTERN_MXJPO = "(\\.|\\w)*" + JPOUtil_mxJPO.JPO_NAME_SUFFIX + "\\w*";
    /** Regular expression pattern to replace _mxJPO endings of Java classes. */
    private static final String CONVERT_REPLACE_MXJPO = JPOUtil_mxJPO.JPO_NAME_SUFFIX + ".*";
    /** Token for the Java class to convert. */
    private static final String TOKEN_CLASSNAME = "$" + "{CLASSNAME}";
    /** Start token for referenced Java classes. */
    private static final String TOKEN_CLASS_START = "$" + "{CLASS:";
    /** End token for referenced Java classes. */
    private static final String TOKEN_CLASS_END = "}";

    /**
     * Private constructor to avoid initalize of this class.
     */
    private JPOUtil_mxJPO()
    {
    }

    /**
     * Converts given {@code _javaCode} to JPO code.
     *
     * @param _backslashUpgraded    are the backslashes upgraded? (no means
     *                              that backslashes are escaped by double
     *                              backslashes)
     * @param _progName             name of program to update
     * @param _javaCode             java code to convert
     * @return JPO code
     */
    public static String convertJavaToJPOCode(final boolean _backslashUpgraded,
                                              final String _progName,
                                              final String _javaCode)
    {
        // program name in the java code is always w/o package!
        final String progName = _progName.replaceAll(".*\\.", "");

        // remove package definition
        final StringBuilder code1 = new StringBuilder();
        final Pattern r1 = Pattern.compile(JPOUtil_mxJPO.CONVERT_PATTERN_PACKAGE, Pattern.MULTILINE);
        final Matcher m1 = r1.matcher(_javaCode);
        int start1 = 0;
        if (m1.find())  {
            code1.append(_javaCode.substring(start1, m1.start()));
            start1 = m1.end();
        }
        code1.append(_javaCode.substring(start1));

        // convert mxJPO class names
        final StringBuilder code2 = new StringBuilder();
        final Pattern r2 = Pattern.compile(JPOUtil_mxJPO.CONVERT_PATTERN_MXJPO);
        final Matcher m2 = r2.matcher(code1);
        int start2 = 0;
        while (m2.find())  {
            code2.append(code1.substring(start2, m2.start()));
            final String jpoName = m2.group().replaceAll(JPOUtil_mxJPO.CONVERT_REPLACE_MXJPO, "");
            if (progName.equals(jpoName))  {
                code2.append(JPOUtil_mxJPO.TOKEN_CLASSNAME);
            } else  {
                code2.append(JPOUtil_mxJPO.TOKEN_CLASS_START).append(jpoName).append(JPOUtil_mxJPO.TOKEN_CLASS_END);
            }
            start2 = m2.end();
        }
        code2.append(code1.substring(start2));

        return _backslashUpgraded
                ? code2.toString()
                // hint! the 8 backslashes are in the text two backslashes!
                : code2.toString().replaceAll("\\\\\\\\", "\\\\\\\\\\\\\\\\");
    }

    /**
     * Writes given JPO to given path for given name. The JPO code is first
     * converted, because Matrix uses keywords which must be replaced to have
     * real Java code. The conversion works like the original extract method,
     * but only converts the given JPOs and not depending JPOs.
     *
     * @param _backslashUpgraded    are the backslashes upgraded? (no means
     *                              that backslashes are escaped by double
     *                              backslashes)
     * @param _progName             name of the JPO program
     * @param _jpoCode              JPO code
     * @return
     */
    public static String convertJPOToJavaCode(final boolean _backslashUpgraded,
                                              final String _progName,
                                              final String _jpoCode)
    {
        final StringBuilder ret = new StringBuilder();

        // define package name (if points within JPO name)
        final int idx = _progName.lastIndexOf('.');
        if (idx > 0)  {
            ret.append("package ").append(_progName.substring(0, idx)).append(";\n");
        }

        // replace class names and references to other JPOs
        final String name = _progName + JPOUtil_mxJPO.JPO_NAME_SUFFIX;
        final String code = _jpoCode
                                .replaceAll("\\" + "$\\{CLASSNAME\\}", name.replaceAll(".*\\.", ""))
                                .replaceAll("(?<=\\"+ "$\\{CLASS\\:[0-9a-zA-Z_.]{0,200})\\}", JPOUtil_mxJPO.JPO_NAME_SUFFIX)
                                .replaceAll("\\" + "$\\{CLASS\\:", "")
                                .trim();

        // for old MX all backslashes are doubled...
        if (_backslashUpgraded)  {
            ret.append(code);
        } else  {
            // hint! the 8 backslashes are in the text two backslashes!
            ret.append(code.replaceAll("\\\\\\\\", "\\\\"));
        }
        return ret.toString();
    }
}

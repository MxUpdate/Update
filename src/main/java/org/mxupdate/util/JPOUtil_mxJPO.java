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
    /** Regular expression pattern used to remove package statements. */
    private static final String CONVERT_PATTERN_PACKAGE = "^package( |\\t).*$(\n|\r\n)?";
    /** Regular expression pattern to fetch mx_JPO statements in the Java source code. */
    private static final String CONVERT_PATTERN_MXJPO = "(\\.|\\w)*_m" + "xJPO\\w*";
    /** Regular expression pattern to replace _mxJPO endings of Java classes. */
    private static final String CONVERT_REPLACE_MXJPO = "_mx"+"JPO.*";
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
     * @param _javaCode     java code to convert
     * @return JPO code
     */
    public static String convertJavaToJPOCode(final String _progName,
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

        return code2.toString();
    }
}

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

package org.mxupdate.action;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.util.Base64Utils_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;

/**
 * Prints out the MxUpdate license information.
 *
 * @author The MxUpdate Team
 */
public class LicenseAction_mxJPO
{
    /** Start tag of the license key. */
    private static String LICENSE_BEGIN = "<!-- BEGIN LICENSE -->";
    /** Length of the start tag. */
    private static int LICENSE_BEGIN_LENGTH = LicenseAction_mxJPO.LICENSE_BEGIN.length();
    /** End tag of the license key. */
    private static String LICENSE_END = "<!-- END LICENSE -->";

    /** Parameter cache. */
    private final ParameterCache_mxJPO paramCache;

    /**
     * Initializes the action.
     *
     * @param _paramCache   parameter cache
     */
    public LicenseAction_mxJPO(final ParameterCache_mxJPO _paramCache)
    {
        this.paramCache = _paramCache;
    }

    /**
     * Executes the action.
     *
     * @throws Exception if execute failed
     */
    public void execute()
        throws Exception
    {
        final int lengthLine = this.paramCache.getValueInteger(ValueKeys.ActionLicenseLengthLine);
        final String textAll = this.paramCache.getValueString(ValueKeys.ActionLicenseText);
        final String textSep = this.paramCache.getValueString(ValueKeys.ActionLicenseTextSeparator);

        // extract license text
        final int idxBegin = textAll.indexOf(LicenseAction_mxJPO.LICENSE_BEGIN);
        final int idxEnd   = textAll.indexOf(LicenseAction_mxJPO.LICENSE_END, idxBegin);
        final String licenseInfo = textAll.substring(idxBegin + LicenseAction_mxJPO.LICENSE_BEGIN_LENGTH, idxEnd);

        // fetch public key of the signature
        final PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(Base64Utils_mxJPO.decode(this.paramCache.getValueString(ValueKeys.ActionLicensePublicKey))));

        // validate signature
        final Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        final Properties props = new Properties();
        props.load(new StringReader(licenseInfo.toString()));
        final byte[] licenseSignature = Base64Utils_mxJPO.decode(props.getProperty("Signature").toString().replaceAll(" ", ""));
        props.remove("```");
        props.remove("Signature");
        final SortedMap<Object,Object> myProps = new TreeMap<>(props);
        for (final Entry<Object,Object> entry : myProps.entrySet())  {
            signature.update(entry.getKey().toString().getBytes("UTF8"),   0, entry.getKey().toString().getBytes("UTF8").length);
            signature.update(entry.getValue().toString().getBytes("UTF8"), 0, entry.getValue().toString().getBytes("UTF8").length);
        }
        final boolean signatureIsValid = signature.verify(licenseSignature);
        final String textSign;
        if (signatureIsValid)  {
            textSign = this.paramCache.getValueString(ValueKeys.ActionLicenseSignatureIsValid);
        } else  {
            textSign = this.paramCache.getValueString(ValueKeys.ActionLicenseSignatureIsNotValid);
        }

        // check version
        final String applName = this.paramCache.getValueString(ValueKeys.RegisterApplicationName);
        final String progName = this.paramCache.getValueString(ValueKeys.RegisterApplicationProg);
        final String curVers = MqlBuilderUtil_mxJPO.mql()
                .cmd("escape print program ").arg(progName).cmd(" ").cmd("select ").arg("property[appVersion" + applName + "].value").cmd(" dump")
                .exec(this.paramCache.getContext())
                .replaceAll("\\-", ".")
                .replaceAll("\\.[0-9]$", "");
        final String expVers = props.getProperty("Version").replaceAll("[a-zA-Z ]", "");
        final boolean versIsValid = expVers.equals(curVers);
        final String textVers;
        if (versIsValid && signatureIsValid)  {
            textVers = this.paramCache.getValueString(ValueKeys.ActionLicenseVersionIsValid);
        } else  {
            textVers = this.paramCache.getValueString(ValueKeys.ActionLicenseVersionIsNotValid);
        }

        // replace umlaut's, sz and paragraph's (which can not be printed with ASCII)
        final String text = (textSep + "\n" + textSign + "\n" + textVers + "\n" + textSep + "\n" + textAll)
                .replaceAll("\\u00A7", "Par.")
                .replaceAll("\\u00C4", "Ae").replaceAll("\\u00E4", "ae")
                .replaceAll("\\u00D6", "Oe").replaceAll("\\u00F6", "oe")
                .replaceAll("\\u00DC", "Ue").replaceAll("\\u00FC", "ue")
                .replaceAll("\\u1E9E", "Ss").replaceAll("\\u00DF", "ss");

        // split license text into lines / extract license information
        boolean isLicenseInfo = false;
        for (final String textLineTmp : text.split("\n"))  {
            if ("<!-- BEGIN LICENSE -->".equals(textLineTmp))  {
                isLicenseInfo = true;
            } else if ("<!-- END LICENSE -->".equals(textLineTmp))  {
                isLicenseInfo = false;
            } else  if (!isLicenseInfo)  {
                StringBuilder line = new StringBuilder();
                final String textLine;
                if (textLineTmp.startsWith("##"))  {
                    textLine = textLineTmp.substring(2).trim().toUpperCase();
                } else if (textLineTmp.startsWith(" "))  {
                    textLine = textLineTmp.trim();
                    line.append(String.format("%1$" + (textLine.length() - textLineTmp.length() + 1) + "s", " "));
                } else  {
                    textLine = textLineTmp;
                }
                for (final String desc : textLine.split(" "))  {
                    if ((line.length() + desc.length()) >= lengthLine)  {
                        this.paramCache.logInfo(line.toString());
                        line = new StringBuilder();
                        if (textLine.matches("^[0-9]*\\. .*$"))  {
                            line.append("   ");
                        }
                        line.append(desc);
                    } else if (line.length() > 0)  {
                        line.append(' ').append(desc);
                    } else  {
                        line.append(desc);
                    }
                }
                this.paramCache.logInfo(line.toString());
            }
        }
    }
}

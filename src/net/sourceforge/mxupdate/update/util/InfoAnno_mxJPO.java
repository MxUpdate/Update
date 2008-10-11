/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Internal Matrix administration type name and (if required) suffix.
 *
 * @author Tim Moxter
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InfoAnno_mxJPO
{
    /**
     * Name of the administration type.
     */
    public String adminType() default "";

    /**
     * Suffix of the administration type (if required, e.g. for web tables). If
     * not defined the suffix is a zero length string.
     */
    public String adminTypeSuffix() default "";

    /**
     * Name of the business type.
     */
    public String busType() default "";

    /**
     * Prefix of the file name.
     */
    public String filePrefix();

    /**
     * Suffix of the file name including the file extension.
     */
    public String fileSuffix();

    /**
     * Used sub directory for the export.
     */
    public String filePath();

    /**
     * String used for logging purpose.
     */
    public String description();
}

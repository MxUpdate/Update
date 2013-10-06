Short Installation Guide
========================
Download and uncompress the MxUpdate Update deployment tool. E.g. the tool is
unpacked into directory C:\project\MxUpdate\.

Start MQL.

Define the MQL environment variable MXUPDATE_PATH to the path where the 
MxInstall.mql file is located. E.g.

    set env MXUPDATE_PATH C:\project\MxUpdate\

If some additional properties must be defined, the MQL environment variable
MXUPDATE_MAPPING_FILE could be defined. The value is the path of the file which
defines the additional properties. E.g.

    set env MXUPDATE_MAPPING_FILE C:\project\MyProjectSettings.properties

Run MQL script MxInstall.mql.

    run C:\project\MxUpdate\MxInstall.mql

Then the MxUpdate Update deployment tool is installed and all MxUpdate JPOs are
compiled. The tool could be tested:

    exec prog MxUpdate --help

The short help for all parameters of MxUpdate is shown.

More Information
================
For more information see http://www.mxupdate.org.
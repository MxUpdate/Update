################################################################################
# POLICY:
# ~~~~~~~
# MxUpdate_Test
#
# SYMBOLIC NAME:
# ~~~~~~~~~~~~~~
# policy_MxUpdate_Test
#
# DESCRIPTION:
# ~~~~~~~~~~~~
#
#
# AUTHOR:
# ~~~~~~~
#
#
################################################################################

updatePolicy "${NAME}" {
  description ""
  type {all}
  format {generic}
  defaultformat "generic"
  sequence "1,2,3,..."
  store ""
  hidden "false"
  state "Pending"  {
    registeredName "state_Pending"
    revision "true"
    version "true"
    promote "true"
    checkouthistory "true"
    owner {read modify delete}
    public {read show}
    action "" input ""
    check "" input ""
  }
  state "Submitted"  {
    registeredName "state_Submitted"
    revision "true"
    version "true"
    promote "true"
    checkouthistory "true"
    owner {read modify checkout checkin}
    public {read show}
    action "" input ""
    check "" input ""
    signature "Reject" {
      branch "Rejected"
      approve {Employee}
      ignore {Employee}
      reject {Employee}
      filter ""
    }
    signature "Review" {
      branch "Review"
      approve {Employee}
      ignore {Employee}
      reject {Employee}
      filter ""
    }
  }
  state "Review"  {
    registeredName "state_Review"
    revision "true"
    version "true"
    promote "true"
    checkouthistory "true"
    owner {read modify checkout}
    public {read show}
    action "" input ""
    check "" input ""
  }
  state "Approved"  {
    registeredName "state_Approved"
    revision "true"
    version "true"
    promote "true"
    checkouthistory "true"
    owner {read modify checkout checkin}
    public {read show}
    action "" input ""
    check "" input ""
    signature "creator" {
      branch ""
      approve {creator}
      ignore {}
      reject {}
      filter ""
    }
  }
  state "Rejected"  {
    revision "true"
    version "true"
    promote "true"
    checkouthistory "true"
    owner {read modify show}
    public {read show}
    action "" input ""
    check "" input ""
  }
}

package comy.action.admin

import xitrum.Action
import comy.action.user.{Index => UserIndex}

class Logout extends Action {
  override def postback {
    resetSession
    flash("You have logged out.")
    jsRedirectTo[UserIndex]
  }
}

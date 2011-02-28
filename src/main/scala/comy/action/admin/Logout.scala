package comy.action.admin

import xitrum.action.Action
import comy.action.user.{Index => UserIndex}

class Logout extends Action {
  override def postback {
    session.reset
    flash("You have logged out.")
    jsRedirectTo[UserIndex]
  }
}

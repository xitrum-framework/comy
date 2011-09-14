package comy.action.admin

import xitrum.Action

import comy.action.AppAction
import comy.action.user.{Index => UserIndex}

class Logout extends AppAction {
  override def postback {
    resetSession
    flash(t("You have logged out."))
    jsRedirectTo[UserIndex]
  }
}

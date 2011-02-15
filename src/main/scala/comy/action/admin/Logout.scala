package comy.action.admin

import xitrum._
import comy.action.user.{Index => UserIndex}

@POST2("/admin/logout")
class Logout extends Action {
  def execute {
    session.reset
    flash("You have logged out.")
    jsRedirectTo[UserIndex]
  }
}

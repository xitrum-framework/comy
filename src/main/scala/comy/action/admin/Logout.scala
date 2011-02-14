package comy.action.admin

import xt._

import comy.action.Application
import comy.action.user.{Index => UserIndex}

@POST("/admin/logout")
class Logout extends Application with Postback {
  def execute {}

  def postback {
    session.reset
    flash("You have logged out.")
    jsRedirectTo[UserIndex]
  }
}

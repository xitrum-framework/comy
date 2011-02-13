package comy.action

import xt._

@POST("/admin/logout")
class AdminLogout extends Application with Postback {
  def execute {}

  def postback {
    session.reset
    flash("You have logged out.")
    jsRedirectTo(urlFor[UserIndex])
  }
}

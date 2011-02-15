package comy.action.admin

import xitrum._

@POST2("/admin/login")
class DoLogin extends Action {
  def execute {
    val username = param("username")
    if (username == "xxx") {
      session.reset
      session("username") = username
      flash("You have successfully logged in.")
      jsRedirectTo[Index]
    } else {
      jsRenderUpdate("error", <p class="error">Could not login.</p>)
    }
  }
}

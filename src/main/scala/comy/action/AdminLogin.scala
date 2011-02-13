package comy.action

import xt._

@GET("/admin/login")
class AdminLogin extends Application with Postback {
  def execute {
    renderView(
      <form postback="submit">
        <div id="error"></div>

        <label>Username:</label>
        <input type="text" name="username" />
        <input type="submit" value="Password »" />
      </form>
    )
  }

  def postback {
    val username = param("username")
    if (username == "xxx") {
      session.reset
      session("username") = username
      flash("You have successfully logged in.")
      jsRedirectTo(urlFor[AdminIndex])
    } else {
      jsRenderUpdate("error", <p class="error">Could not login.</p>)
    }
  }
}

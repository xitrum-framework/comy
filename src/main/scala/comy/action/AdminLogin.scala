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
    paramo("username") match {
      // Logout, see Application.scala
      case None =>
        session.reset
        jsRedirectTo(urlFor[UserIndex])

      // Login, see above
      case Some(username) =>
        if (username == "xxx") {
          session("username") = username
          jsRedirectTo(urlFor[AdminIndex])
        } else {
          jsRenderUpdate("error", <p class="error">Could not login.</p>)
        }
    }
  }
}

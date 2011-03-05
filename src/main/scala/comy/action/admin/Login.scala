package comy.action.admin

import xitrum.action.annotation._
import xitrum.action.validation._

import comy.action.Application

@GET("/admin/login")
class Login extends Application {
  override def execute {
    renderView(
      <form postback="submit" action={urlForPostbackThis}>
        <div id="error"></div>

        <label>Username:</label> {<input type="text" name="username" /> :: Validate(Required)}
        <br />
        <input type="submit" value="Password »" />
      </form>
    )
  }

  override def postback {
    val username = param("username")
    if (username == "xxx") {
      session.reset
      session("username") = username
      flash("You have successfully logged in.")
      jsRedirectTo[Index]
    } else {
      jsRenderHtml(jsById("error"), <p class="error">Could not login.</p>)
    }
  }
}

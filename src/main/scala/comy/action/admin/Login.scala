package comy.action.admin

import xitrum.annotation._
import xitrum.validation._

import comy.action.{AppAction, SVar}

@GET("/admin/login")
class Login extends AppAction {
  override def execute {
    renderView(
      <form data-postback="submit" action={urlForPostbackThis}>
        <div id="error"></div>

        <label>{t("Username")}:</label>
        {<input type="text" name="username" /> :: Required}
        <br />

        <input type="submit" value={t("Password »")} />
      </form>
    )
  }

  override def postback {
    val username = param("username")
    if (username == "xxx") {  // TODO
      resetSession
      SVar.username.set(username)
      flash(t("You have successfully logged in."))
      jsRedirectTo[Index]
    } else {
      jsRenderFormat("$('#error').html(%s)", jsEscape(<p class="error">{t("Could not login.")}</p>))
    }
  }
}

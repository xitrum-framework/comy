package comy.controller

object Admins extends Admins

class Admins extends AppController {
  pathPrefix = "admin"

  beforeFilter(except = Seq(login, doLogin)) {
    val ret = SVar.username.isDefined
    if (!ret) {
      flash(t("Please login."))
      redirectTo(login)
    }
    ret
  }

  def index = GET {
    respondInlineView(<p>Admin page</p>)
  }

  def login = GET("login") {
    respondView()
  }

  def doLogin = POST("login") {
    val username = param("username")
    if (username == "xxx") {  // TODO
      resetSession()
      SVar.username.set(username)
      flash(t("You have successfully logged in."))
      jsRedirectTo(index)
    } else {
      jsRespond("$('#error').html(%s)".format(jsEscape(<p class="error">{t("Could not login.")}</p>)))
    }
  }

  def logout = POST("logout") {
    resetSession()
    flash(t("You have logged out."))
    jsRedirectTo(Users.index)
  }
}

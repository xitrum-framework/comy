package comy.action

import xitrum.Action
import xitrum.annotation.{GET, POST}

trait AdminAuth {
  this: Action =>

  beforeFilter {
    if (SVar.username.isEmpty) {
      flash(t("Please login."))
      redirectTo[AdminLogin]()
    }
  }
}

@GET("admin")
class AdminIndex extends AppAction with AdminAuth {
  def execute() {
    respondInlineView(<p>Admin page</p>)
  }
}

@GET("admin/login")
class AdminLogin extends AppAction {
  def execute() {
    respondView()
  }
}

@POST("admin/login")
class AdminDoLogin extends AppAction {
  def execute() {
    val username = param("username")
    if (username == "xxx") {  // TODO
      session.clear()
      SVar.username.set(username)
      flash(t("You have successfully logged in."))
      jsRedirectTo[AdminIndex]()
    } else {
      jsRespond("$('#error').html('%s')".format(jsEscape(<p class="error">{t("Could not login.")}</p>)))
    }
  }
}

@POST("logout")
class AdminLogout extends AppAction {
  def execute() {
    session.clear()
    flash(t("You have logged out."))
    jsRedirectTo[UserIndex]()
  }
}

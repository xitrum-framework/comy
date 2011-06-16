package comy.action.admin

import xitrum.action.annotation.GET

import comy.action.svUsername
import comy.action.Application

@GET("/admin")
class Index extends Application {
  beforeFilters("authenticate") = () => {
    val ret = svUsername.isDefined
    if (!ret) {
      flash(t("Please login."))
      redirectTo[Login]
    }
    ret
  }

  override def execute {
    renderView(<p>Admin page</p>)
  }
}

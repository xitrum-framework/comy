package comy.action.admin

import xt._
import comy.action.Application

@GET("/admin")
class Index extends Application {
  beforeFilters("authenticate") = () => {
    val ret = session("username").isDefined
    if (!ret) redirectTo[Login]
    ret
  }

  def execute {
    renderView(<p>Admin page</p>)
  }
}

package comy.action.admin

import xt._
import comy.action.Application

@GET("/admin")
class Index extends Application {
  def execute {
    renderView(<p>Admin page</p>)
  }
}

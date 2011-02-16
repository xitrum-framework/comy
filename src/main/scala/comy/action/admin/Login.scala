package comy.action.admin

import xitrum.action.annotation._
import xitrum.action.validation._

import comy.action.Application

@GET("/admin/login")
class Login extends Application {
  def execute {
    renderView(
      <form post2="submit" action={urlFor[DoLogin]}>
        <div id="error"></div>

        <label>Username:</label> {<input type="text" name="username" />.validate(new Required)}
        <br />
        <input type="submit" value="Password »" />
      </form>
    )
  }
}

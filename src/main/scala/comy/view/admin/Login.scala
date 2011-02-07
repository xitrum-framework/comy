package comy.view.admin

import xt._

object Login extends View {
  def render(controller: Controller) = {
    <form action="/admin/login" method="post">
      <label>Username:</label>
      <input type="text" name="username" />
      <input type="submit" value="Password »" />
    </form>
  }
}

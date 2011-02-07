package comy.controller

import xt._
import comy.view.layout.{Application => Layout}

class Application extends Controller {
  override def layout = Some(Layout)
}

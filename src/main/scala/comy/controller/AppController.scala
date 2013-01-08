package comy.controller

import xitrum.Controller

trait AppController extends SetLanguage {
  override def layout = renderViewNoLayout(classOf[AppController])
}

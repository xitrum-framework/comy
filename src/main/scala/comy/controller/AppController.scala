package comy.controller

import xitrum.Controller

trait AppController extends SetLanguage {
  override def layout = renderScalate(classOf[AppController])
}

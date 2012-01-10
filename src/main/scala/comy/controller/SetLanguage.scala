package comy.controller

import xitrum.Controller

trait SetLanguage extends Controller {
  beforeFilter {
    setLanguage("ja")
    true
  }
}

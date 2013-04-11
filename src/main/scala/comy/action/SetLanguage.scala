package comy.action

import xitrum.Action

trait SetLanguage extends Action {
  beforeFilter {
    setLanguage("ja")
    true
  }
}

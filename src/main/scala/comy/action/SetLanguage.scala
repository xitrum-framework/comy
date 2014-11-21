package comy.action

import xitrum.Action

trait SetLanguage extends Action {
  beforeFilter {
    language = "ja"
  }
}

package comy.action

import xitrum.Action

trait SetLanguage extends Action {
  beforeFilters("setLanguage") = () => {
    setLanguage("ja")
    true
  }
}

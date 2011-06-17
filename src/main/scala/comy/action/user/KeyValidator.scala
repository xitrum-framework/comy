package comy.action.user

import xitrum.Action
import xitrum.validation.Validator

object KeyValidator extends Validator {
  def render(action: Action, name: String, name2: String) {
    import action._

    jsAddToView("""
      $.validator.addMethod(
        "comyKey",
        function(value, element) {
          return value.match(/^[a-zA-Z0-9_\-]*$/);
        },
        "Invalid."
      );
    """)

    val js = jsChain(
      jsByName(name2),
      jsCall("rules", "\"add\"", "{comyKey: true}")
    )
    jsAddToView(js)
  }

  def validate(action: Action, name: String, name2: String): Boolean = {
    true
  }
}

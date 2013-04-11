package comy.action

trait AppAction extends SetLanguage {
  override def layout = renderViewNoLayout(classOf[AppAction])
}

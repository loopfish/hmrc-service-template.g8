package wiring

import javax.inject.Inject

import uk.gov.hmrc.play.http.hooks.HttpHook
import uk.gov.hmrc.play.http.ws._

class WSVerbs @Inject() extends WSHttp {
  override val hooks: Seq[HttpHook] = NoneRequired
}
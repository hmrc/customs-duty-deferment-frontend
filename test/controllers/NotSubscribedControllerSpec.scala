package controllers

import play.api.Application
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import util.SpecBase

class NotSubscribedControllerSpec extends SpecBase {
  "onPageLoad" should {
    "return OK" in {
      val app: Application = application().build()
      val request = FakeRequest(GET, routes.NotSubscribedController.onPageLoad().url)
      running(app) {
        val result = route(app, request).value
        status(result) mustBe OK
      }
    }
  }
}

/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package controllers.contactDetails

import config.AppConfig
import models.{AccountStatusOpen, CDSAccountStatus, CDSAccountStatusId, DefermentAccountAvailable, DutyDefermentAccountLink}
import org.mockito.Mockito.when
import play.api.Application
import play.api.inject.bind
import play.api.mvc.{AnyContent, AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AccountLinkCacheService, ContactDetailsCacheService}
import util.{SpecBase, TestContactDetails}
import viewmodels.ContactDetailsViewModel
import views.html.contact_details.show

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ShowContactDetailsControllerSpec extends SpecBase with TestContactDetails {

  "show" must {
    "redirect to session expired view when no accountLink found" in new Setup {
      when(mockAccountLinkCacheService.get(any)).thenReturn(Future.successful(None))

      running(app) {
        val result: Future[Result] = route(app, showRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe ""
      }
    }

    "return OK and the correct view for a GET, with a correctly encrypted DAN" in new Setup {
      when(mockAccountLinkCacheService.get(any))
        .thenReturn(Future.successful(Some(dutyDefermentAccountLink)))

      when(mockContactDetailsCacheService.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))

      running(app) {
        val result: Future[Result] = route(app, showRequest).value
        status(result) mustBe OK
        contentAsString(result) mustEqual view(validContactDetailsViewModel, validStatus, "someLinkId")
      }
    }
  }

  trait Setup {
    val mockContactDetailsCacheService: ContactDetailsCacheService = mock[ContactDetailsCacheService]
    val mockAccountLinkCacheService: AccountLinkCacheService = mock[AccountLinkCacheService]

    val dutyDefermentAccountLink: DutyDefermentAccountLink = DutyDefermentAccountLink(
      dan = validDan,
      linkId = "someLinkId",
      status = AccountStatusOpen,
      statusId = validStatus
    )

    val validContactDetailsViewModel: ContactDetailsViewModel = ContactDetailsViewModel(
      validDan,
      validAccountContactDetails,
      _ => Some("United Kingdom")
    )


    val app: Application = application()
      .overrides(
        bind[ContactDetailsCacheService].toInstance(mockContactDetailsCacheService),
        bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService)
      ).build()

    val view: show = app.injector.instanceOf[show]
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

    val showRequest: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest(GET, routes.ShowContactDetailsController.show().url)
    val startSessionRequest: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest(GET, routes.ShowContactDetailsController.startSession("someLinkId").url)
  }
}

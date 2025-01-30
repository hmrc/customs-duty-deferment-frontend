/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import controllers.routes
import util.SpecBase

class NavigatorSpec extends SpecBase {

  "backLinkUrlForServiceUnavailablePage" should {

    "return correct urls for the provided input" in new Setup {

      navigatorOb.backLinkUrlForServiceUnavailablePage("id_not_defined") mustBe empty

      navigatorOb.backLinkUrlForServiceUnavailablePage(
        navigatorOb.dutyDefermentStatementPageId,
        someLinkId
      ) mustBe Some(
        routes.AccountController.showAccountDetails(someLinkId).url
      )

      navigatorOb.backLinkUrlForServiceUnavailablePage(
        navigatorOb.dutyDefermentStatementNAPageId,
        someLinkId2
      ) mustBe Some(
        routes.AccountController.statementsUnavailablePage(someLinkId2).url
      )
    }
  }

  trait Setup {
    val navigatorOb = new Navigator()
  }
}

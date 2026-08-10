/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.inheritancetaxonpensions.services

import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.http.HeaderCarrier
import models.{IhtpOverviewReport, IhtpOverviewResponse, IhtpOverviewSuccess}
import uk.gov.hmrc.inheritancetaxonpensions.repositories.UserAnswersRepository
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers._
import utils.TestValues
import org.scalatest.freespec.AnyFreeSpec
import org.mockito.Mockito._
import uk.gov.hmrc.inheritancetaxonpensions.connectors.IhtpReportConnector
import org.scalatest.matchers.must.Matchers

import scala.concurrent.Future

class ReportRetrievalServiceSpec
    extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterEach
    with TestValues {

  override def beforeEach(): Unit = reset(mockUserAnswersRepository, mockIhtpReportConnector)

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  private val mockUserAnswersRepository: UserAnswersRepository = mock[UserAnswersRepository]
  private val mockIhtpReportConnector: IhtpReportConnector = mock[IhtpReportConnector]
  private val service = new ReportRetrievalService(mockUserAnswersRepository, mockIhtpReportConnector)

  "getOverview" - {
    "returns all reports that are coming from the connector in the response" in {

      when(mockIhtpReportConnector.getOverview(any(), any(), any(), any())(any()))
        .thenReturn(
          Future.successful(
            Right(
              IhtpOverviewResponse(IhtpOverviewSuccess(testOverviewResponse.as[Seq[IhtpOverviewReport]]))
            )
          )
        )

      val result = service.getOverview(testPstr, testDateFrom, testDateTo, None).futureValue

      result match {
        case Left(value) => fail("unexpected")
        case Right(value) =>
          value.success.ihtpOverview must have size 3
      }
    }
  }
}

package io.github.jlprat.akka.http.workshop.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.github.jlprat.akka.http.workshop.actors.Model.{Author, Book}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by @jlprat on 24/10/2018.
  */
class CatalogRouteExampleTest extends FlatSpec with ScalatestRouteTest with Matchers {


  val book = Book("1234567", "The art of Doe", 321, Author("Jane Doe"))
  val catalogRef: ActorRef[CatalogBehavior.Command] = system.spawn(CatalogBehavior.catalogBehavior, "Catalog")

  class Fixture extends CatalogRouteExample.CatalogRoute {

    override val catalogBehavior: ActorRef[CatalogBehavior.Command] = catalogRef
  }

  "CatalogManagerRoutes" should "add books with given quantity if below 1000" in new Fixture {
    Put("/catalog/book/100", book) ~> catalogRoute ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe "OK"
    }
  }

  it should "fail to add books with given quantity if over 1000" in new Fixture {
    Put("/catalog/book/20000", book) ~> catalogRoute ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe "Too many books to print"
    }
  }

}

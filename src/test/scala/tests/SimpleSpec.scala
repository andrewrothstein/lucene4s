package tests

import com.outr.lucene4s._
import org.scalatest.{Matchers, WordSpec}

class SimpleSpec extends WordSpec with Matchers {
  val lucene = new Lucene()

  "Simple Spec" should {
    "create a simple document" in {
      lucene.doc().field("name", "John Doe").index()
    }
    "query for the index" in {
      val paged = lucene.query("name").search("john")
      paged.total should be(1)
      val results = paged.results
      results.length should be(1)
      results(0).string("name") should be("John Doe")
    }
    "add a few more documents" in {
      lucene.doc().field("name", "Jane Doe").index()
      lucene.doc().field("name", "Andrew Anderson").index()
      lucene.doc().field("name", "Billy Bob").index()
      lucene.doc().field("name", "Carly Charles").index()
      lucene.flush()
    }
    "query using pagination" in {
      val page1 = lucene.query("name").limit(2).search("*:*")
      page1.total should be(5)
      val results1 = page1.results
      page1.pageIndex should be(0)
      page1.pages should be(3)
      results1.length should be(2)
      results1(0).string("name") should be("John Doe")
      results1(1).string("name") should be("Jane Doe")
      page1.hasPreviousPage should be(false)

      val page2 = page1.nextPage().get
      page2.total should be(5)
      val results2 = page2.results
      page2.pageIndex should be(1)
      page2.pages should be(3)
      results2.length should be(2)
      results2(0).string("name") should be("Andrew Anderson")
      results2(1).string("name") should be("Billy Bob")

      val page3 = page2.nextPage().get
      page3.total should be(5)
      val results3 = page3.results
      page3.pageIndex should be(2)
      page3.pages should be(3)
      results3.length should be(1)
      results3(0).string("name") should be("Carly Charles")
      page3.hasNextPage should be(false)
    }
    "dispose" in {
      lucene.dispose()
    }
  }
}
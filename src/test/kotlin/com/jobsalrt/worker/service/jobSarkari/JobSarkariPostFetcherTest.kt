package com.jobsalrt.worker.service.jobSarkari

import com.jobsalrt.worker.builder.BasicDetailsBuilder
import com.jobsalrt.worker.builder.DetailsBuilder
import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.service.postService.PostService
import com.jobsalrt.worker.service.postService.RawPostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.jsoup.Jsoup
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class JobSarkariPostFetcherTest {
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val postService = mockk<PostService>()
    private val rawPostService = mockk<RawPostService>()
    private val jobUrlService = mockk<JobUrlService>()
    private val jobSarkariPostFetcher =
        JobSarkariPostFetcher(webClientWrapper, postService, rawPostService, jobUrlService)
    private val document = Jsoup.parse(
        """<body>
  <div class='noprint noprint-21' style='margin: 8px auto; text-align: center; clear: both;'>
    <div id="page" class="site grid-container container hfeed">
      <div id="content" class="site-content">
        <div id="primary" class="content-area">
          <main id="main" class="site-main">
            <article id="post-165278" class="post-165278 job type-job status-publish has-post-thumbnail hentry form_type-online-forms-only state-delhi expired-active qualification-10th-pass qualification-12th-pass qualification-graduate qualification-other-qualification qualification-post-graduate company-delhi-subordinate-services-selection-board" itemtype="https://schema.org/CreativeWork" itemscope>
              <div class="inside-article">
                <header class="entry-header">
                  <div><h1 class="singleh1 grad">DSSSB 7236 Various Post Online Form 2021 Recruitment</h1></div>        
                </header>
                <div class="entry-content " itemprop="text">
                  <div class="post-title" id="post-title1" >
                    <div class="job_card " >
                      <p class="dashicons-feedback dashicons-before">
                        <span class="option_name_l" >Form Type : </span><span class="sec_detail">Online  </span>
                      </p>
                      <p class="dashicons-calendar dashicons-before">
                        <span class="option_name_l" >Last Date To Apply : </span><span class="sec_detail last_date">24/06/2021</span>
                      </p>
                      <p class="dashicons-welcome-learn-more dashicons-before">
                        <span class="option_name_l" >Total Vacancies : </span><span class="sec_detail">7236</span>
                      </p>
                      <p class="dashicons-bank dashicons-before">
                        <span class="option_name_l" >Company : </span><span class="sec_detail"> DSSSB (<a href="https://www.jobsarkari.com/company/delhi-subordinate-services-selection-board/" rel="tag">Delhi Subordinate Services Selection Board</a>)</span>
                      </p>
                      <p class="dashicons-location dashicons-before">
                        <span class="option_name_l" >Location : </span><span class="sec_detail"><a href="https://www.jobsarkari.com/jobs-by-location/">Delhi</a></span>
                      </p>
                      <p class="dashicons-book-alt dashicons-before">
                        <span class="option_name_l" >Qualification Required :</span><span class="sec_detail"> 10th Pass,  12th Pass,  Graduation,  Other Qualifications,  Post Graduate </span>
                      </p>
                    </div>
                  </div>
                  <div class="job_card">
                    <table class="data-table dattatable table table-sm" id="table1" >
                      <h2 class="job_card_heading grad">Important Dates</h2>
                      <tbody>
                        <tr class="border">
                          <td>Application Begin</td>
                          <td>25/05/2021</td>
                        </tr>
                        <tr class="border">
                          <td>Last Date to Apply Online</td>
                          <td><span style="color:red">24/06/2021</span></td>
                        </tr>
                        <tr class="border">
                          <td>Last Date for Fees Payment</td>
                          <td>24/06/2021</td>
                        </tr>
                        <tr class="border">
                          <td>Admit Card Download</td>
                          <td>Update Soon</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div class="job_card">
                    <table class="data-table dattatable table table-sm" id="table2" >
                      <h2 class="job_card_heading grad">Application Fee Details</h2>
                      <thead >
                        <tr>
                          <th class="font-weight-bold  bg-primary text-light   ">Category</th>
                          <th class="font-weight-bold  bg-primary text-light   ">Amount</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr class="border">
                          <td>General / OBC / EWS</td>
                          <td>Rs. 100/-</td>
                        </tr>
                        <tr class="border">
                          <td>SC / ST / PH</td>
                          <td>No Fee</td>
                        </tr>
                        <tr class="border">
                          <td>All Category Female</td>
                          <td>No Fee</td>
                        </tr>
                        <tr class="border">
                          <td><b>Mode of Payment</b></td>
                          <td><span style="color:blue">Candidate Have to Pay Application Fee Through Debit Card, Credit Card or Net Banking OR SBI E Challan.</span></td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div class="job_card">
                    <table class="data-table dattatable table table-sm" id="table3" >
                      <h2 class="job_card_heading grad">Vacancy Details</h2>
                      <thead >
                        <tr>
                          <th class="font-weight-bold  bg-primary text-light   ">Post Name</th>
                          <th class="font-weight-bold  bg-primary text-light   ">No. of Post</th>
                          <th class="font-weight-bold  bg-primary text-light   ">Post Code</th>
                          <th class="font-weight-bold  bg-primary text-light   ">Qualification</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr class="border">
                          <td>TGT Trained Graduate Teacher</td>
                          <td>6258</td>
                          <td>33/21 to 41/21</td>
                          <td>Bachelor Degree in Related Subject.</td>
                        </tr>
                        <tr class="border">
                          <td>Assistant Teacher Nursery</td>
                          <td>74</td>
                          <td>43/21</td>
                          <td>10+2 Intermediate Exam with NTT Training / B.Ed Exam Passed.</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div class="job_card">
                    <table class="data-table dattatable table table-sm" id="table4" >
                      <h2 class="job_card_heading grad">Age Limit Details</h2>
                      <thead >
                        <tr>
                          <th class="font-weight-bold  bg-primary text-light   ">Post Name</th>
                          <th class="font-weight-bold  bg-primary text-light   ">Age Limit</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr class="border">
                          <td>Trained Graduate Teacher TGT</td>
                          <td>Below 32 Years.</td>
                        </tr>
                        <tr class="border">
                          <td>Junior Secretarial Assistant LDC</td>
                          <td>18-27 Years.</td>
                        </tr>
                        <tr class="border">
                          <td>Patwari</td>
                          <td>21-27 Years.</td>
                        </tr>
                        <tr class="border">
                          <td>Other Post</td>
                          <td>Below 30 Years.</td>
                        </tr>
                        <tr class="border">
                          <td><b>Relaxation as per Govt. Rule</b></td>
                          <td></td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div class="job_card">
                    <table class="data-table dattatable table table-sm" id="table5" >
                      <h2 class="job_card_heading grad">Selection Process</h2>
                      <tbody>
                        <tr class="border">
                          <td>1. Written Exam.</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div class="job_card">
                    <table class="data-table dattatable table table-sm" id="table5" >
                      <h2 class="job_card_heading grad">Other details</h2>
                      <tbody>
                        <tr class="border">
                          <td>1. Written Exam.</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div class="job_card">
                    <table class="data-table dattatable table table-sm" id="table6" >
                      <h2 class="job_card_heading grad">How to Apply</h2>
                      <tbody>
                        <tr class="border">
                          <td>1. Candidate Can Apply through Online Mode.</td>
                        </tr>
                        <tr class="border">
                          <td>2. Click On the Apply Online Link given in Important Link Section.</td>
                        </tr>
                        <tr class="border">
                          <td>3. Read Instructions then Proceed to the online application.</td>
                        </tr>
                        <tr class="border">
                          <td>4. Fill All the Mandatory Details viz. Name, Father’s Name, Date of Birth, e-mail Address, Mobile Number, and Qualification, etc in the Application form.</td>
                        </tr>
                        <tr class="border">
                          <td>5. Follow the instructions and complete the registration process step-by-step for getting a Registration Number &amp; Password.</td>
                        </tr>
                        <tr class="border">
                          <td>6. Upload the Scanned Copy of Documents in the Prescribed Size.</td>
                        </tr>
                        <tr class="border">
                          <td>7. Take Printout of your Application form for Future Reference.</td>
                        </tr>
                        <tr class="border">
                          <td>8. Online Application Can be Submitted on or Before <span style="color:red">24th June 2021.</span></td>
                        </tr>
                      </tbody>
                    </table>
                  </div>              
                  <span id="linksecction"></span>
                  <div class="job_card">
                    <h2 class="job_card_heading grad">Important Links </h2>
                    <table class="table table-sm table-bordered w-100 linkstable mb-0">
                      <tr>
                        <td class="w-50"><b>Apply Online</td>
                        <td>
                          <a class="insidelinkaa" href="https://dsssbonline.nic.in/" target="_blank" data-clickedon="Apply Online">                     Click Here </a>
                        </td>
                      </tr>
                      <tr>
                        <td class="w-50"><b>Download Notification</td>
                        <td>
                          <a class="insidelinkaa" href="https://dsssb.delhi.gov.in/sites/default/files/All-PDF/Advt_02-21.pdf" target="_blank" data-clickedon="Download Notification">                     Click Here </a>
                        </td>
                      </tr>
                      <tr>
                        <td class="w-50"><b>Official Website</td>
                        <td>
                          <a class="insidelinkaa" href="https://dsssbonline.nic.in/" target="_blank" data-clickedon="Official Website">                     Click Here </a>
                        </td>
                      </tr>
                    </table>
                  </div>
                </div>
            </article>
          </main>
        </div>
      </div>
    </div>
  </div>
</body>"""
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should get all the dates in dates sections`() {
        val expected = DetailsBuilder(
            body = listOf(
                listOf("Application Begin", "25/05/2021"),
                listOf("Last Date to Apply Online", "24/06/2021"),
                listOf("Last Date for Fees Payment", "24/06/2021"),
                listOf("Admit Card Download", "Update Soon"),
            )
        ).build()

        jobSarkariPostFetcher.getDates(document) shouldBe expected
    }

    @Test
    fun `should get fee details`() {
        val expected = DetailsBuilder(
            header = listOf("Category", "Amount"),
            body = listOf(
                listOf("General / OBC / EWS", "Rs. 100/-"),
                listOf("SC / ST / PH", "No Fee"),
                listOf("All Category Female", "No Fee"),
                listOf(
                    "Mode of Payment",
                    "Candidate Have to Pay Application Fee Through Debit Card, Credit Card or Net Banking OR SBI E Challan."
                )
            )
        ).build()

        jobSarkariPostFetcher.getFeeDetails(document) shouldBe expected
    }

    @Test
    fun `should get vacancy details`() {
        val expected = DetailsBuilder(
            header = listOf("Post Name", "No. of Post", "Post Code", "Qualification"),
            body = listOf(
                listOf("TGT Trained Graduate Teacher", "6258", "33/21 to 41/21", "Bachelor Degree in Related Subject."),
                listOf(
                    "Assistant Teacher Nursery",
                    "74",
                    "43/21",
                    "10+2 Intermediate Exam with NTT Training / B.Ed Exam Passed."
                )
            )
        ).build()

        jobSarkariPostFetcher.getVacancyDetails(document) shouldBe expected
    }

    @Test
    fun `should get age limit details`() {
        val expected = DetailsBuilder(
            header = listOf("Post Name", "Age Limit"),
            body = listOf(
                listOf("Trained Graduate Teacher TGT", "Below 32 Years."),
                listOf("Junior Secretarial Assistant LDC", "18-27 Years."),
                listOf("Patwari", "21-27 Years."),
                listOf("Other Post", "Below 30 Years."),
                listOf("Relaxation as per Govt. Rule", ""),
            )
        ).build()

        jobSarkariPostFetcher.getAgeLimitDetails(document) shouldBe expected
    }

    @Test
    fun `should get important links details`() {
        val expected = DetailsBuilder(
            body = listOf(
                listOf("Apply Online", "https://dsssbonline.nic.in/"),
                listOf(
                    "Download Notification",
                    "https://dsssb.delhi.gov.in/sites/default/files/All-PDF/Advt_02-21.pdf"
                ),
                listOf("Official Website", "https://dsssbonline.nic.in/")
            )
        ).build()

        jobSarkariPostFetcher.getImportantLinks(document) shouldBe expected
    }

    @Test
    fun `should get other details`() {
        val expected = mapOf(
            "Other details" to DetailsBuilder(body = listOf(listOf("1. Written Exam."))).build()
        )
        jobSarkariPostFetcher.getOtherDetails(document) shouldBe expected
    }

    @Test
    fun `should get basic details`() {
        val expected = BasicDetailsBuilder(
            name = "DSSSB 7236 Various Post Online Form 2021 Recruitment",
            formType = FormType.ONLINE,
            lastDate = LocalDate.of(2021, 6, 24),
            totalVacancies = 7236,
            location = "Delhi",
            company = "DSSSB (Delhi Subordinate Services Selection Board)",
            qualification = "10th Pass, 12th Pass, Graduation, Other Qualifications, Post Graduate",
            url = "anonymous"
        ).build()
        val basicDetails = jobSarkariPostFetcher.getBasicDetails(document)
        basicDetails.url = "anonymous"
        basicDetails shouldBe expected
    }


    @Test
    fun `should get selection process details`() {
        val expected = listOf("1. Written Exam.")
        jobSarkariPostFetcher.getSelectionProcessDetails(document) shouldBe expected
    }

    @Test
    fun `should get how to apply details`() {
        val expected = listOf(
            "1. Candidate Can Apply through Online Mode.",
            "2. Click On the Apply Online Link given in Important Link Section.",
            "3. Read Instructions then Proceed to the online application.",
            "4. Fill All the Mandatory Details viz. Name, Father’s Name, Date of Birth, e-mail Address, Mobile Number, and Qualification, etc in the Application form.",
            "5. Follow the instructions and complete the registration process step-by-step for getting a Registration Number & Password.",
            "6. Upload the Scanned Copy of Documents in the Prescribed Size.",
            "7. Take Printout of your Application form for Future Reference.",
            "8. Online Application Can be Submitted on or Before 24th June 2021.",
        )
        jobSarkariPostFetcher.getHowToApplyDetails(document) shouldBe expected
    }


    @Test
    fun `should get all the dates as null if no dates section is available`() {
        jobSarkariPostFetcher.getDates(Jsoup.parse("")) shouldBe null
    }

    @Test
    fun `should get fee details as null if no section is present`() {
        jobSarkariPostFetcher.getFeeDetails(Jsoup.parse("")) shouldBe null
    }

    @Test
    fun `should get vacancy details as null`() {
        val html = """<div class="job_card"><h2>Vacancy Details</h2><div class="table"></div></div>"""
        val document = Jsoup.parse(html)
        jobSarkariPostFetcher.getVacancyDetails(document) shouldBe null
    }

    @Test
    fun `should get age limit details as null`() {
        jobSarkariPostFetcher.getAgeLimitDetails(Jsoup.parse("")) shouldBe null
    }

    @Test
    fun `should get important links details as null`() {
        val html = """<div class="job_card"><h2>Important Links</h2><div class="table"></div></div>"""
        jobSarkariPostFetcher.getImportantLinks(Jsoup.parse(html)) shouldBe null
    }

    @Test
    fun `should get other details as empty map`() {
        jobSarkariPostFetcher.getOtherDetails(Jsoup.parse("")) shouldBe emptyMap()
    }

    @Test
    fun `should get basic details as default`() {
        val expected = BasicDetailsBuilder(
            name = "",
            formType = FormType.OFFLINE,
            url = "anonymous"
        ).build()
        val basicDetails = jobSarkariPostFetcher.getBasicDetails(Jsoup.parse(""))
        basicDetails.url = "anonymous"
        basicDetails shouldBe expected
    }


    @Test
    fun `should get selection process details as null`() {
        jobSarkariPostFetcher.getSelectionProcessDetails(Jsoup.parse("")) shouldBe null
    }

    @Test
    fun `should get how to apply details as null`() {
        jobSarkariPostFetcher.getHowToApplyDetails(Jsoup.parse("")) shouldBe null
    }


    @Test
    fun `should parse the html and trim unnecessary html`() {
        val expected = """<div class="job_card "> 
 <p class="dashicons-feedback dashicons-before"> <span class="option_name_l">Form Type : </span><span class="sec_detail">Online </span> </p> 
 <p class="dashicons-calendar dashicons-before"> <span class="option_name_l">Last Date To Apply : </span><span class="sec_detail last_date">24/06/2021</span> </p> 
 <p class="dashicons-welcome-learn-more dashicons-before"> <span class="option_name_l">Total Vacancies : </span><span class="sec_detail">7236</span> </p> 
 <p class="dashicons-bank dashicons-before"> <span class="option_name_l">Company : </span><span class="sec_detail"> DSSSB (<a href="https://www.jobsarkari.com/company/delhi-subordinate-services-selection-board/" rel="tag">Delhi Subordinate Services Selection Board</a>)</span> </p> 
 <p class="dashicons-location dashicons-before"> <span class="option_name_l">Location : </span><span class="sec_detail"><a href="https://www.jobsarkari.com/jobs-by-location/">Delhi</a></span> </p> 
 <p class="dashicons-book-alt dashicons-before"> <span class="option_name_l">Qualification Required :</span><span class="sec_detail"> 10th Pass, 12th Pass, Graduation, Other Qualifications, Post Graduate </span> </p> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">Important Dates</h2>
 <table class="data-table dattatable table table-sm" id="table1">  
  <tbody> 
   <tr class="border"> 
    <td>Application Begin</td> 
    <td>25/05/2021</td> 
   </tr> 
   <tr class="border"> 
    <td>Last Date to Apply Online</td> 
    <td><span style="color:red">24/06/2021</span></td> 
   </tr> 
   <tr class="border"> 
    <td>Last Date for Fees Payment</td> 
    <td>24/06/2021</td> 
   </tr> 
   <tr class="border"> 
    <td>Admit Card Download</td> 
    <td>Update Soon</td> 
   </tr> 
  </tbody> 
 </table> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">Application Fee Details</h2>
 <table class="data-table dattatable table table-sm" id="table2">  
  <thead> 
   <tr> 
    <th class="font-weight-bold  bg-primary text-light   ">Category</th> 
    <th class="font-weight-bold  bg-primary text-light   ">Amount</th> 
   </tr> 
  </thead> 
  <tbody> 
   <tr class="border"> 
    <td>General / OBC / EWS</td> 
    <td>Rs. 100/-</td> 
   </tr> 
   <tr class="border"> 
    <td>SC / ST / PH</td> 
    <td>No Fee</td> 
   </tr> 
   <tr class="border"> 
    <td>All Category Female</td> 
    <td>No Fee</td> 
   </tr> 
   <tr class="border"> 
    <td><b>Mode of Payment</b></td> 
    <td><span style="color:blue">Candidate Have to Pay Application Fee Through Debit Card, Credit Card or Net Banking OR SBI E Challan.</span></td> 
   </tr> 
  </tbody> 
 </table> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">Vacancy Details</h2>
 <table class="data-table dattatable table table-sm" id="table3">  
  <thead> 
   <tr> 
    <th class="font-weight-bold  bg-primary text-light   ">Post Name</th> 
    <th class="font-weight-bold  bg-primary text-light   ">No. of Post</th> 
    <th class="font-weight-bold  bg-primary text-light   ">Post Code</th> 
    <th class="font-weight-bold  bg-primary text-light   ">Qualification</th> 
   </tr> 
  </thead> 
  <tbody> 
   <tr class="border"> 
    <td>TGT Trained Graduate Teacher</td> 
    <td>6258</td> 
    <td>33/21 to 41/21</td> 
    <td>Bachelor Degree in Related Subject.</td> 
   </tr> 
   <tr class="border"> 
    <td>Assistant Teacher Nursery</td> 
    <td>74</td> 
    <td>43/21</td> 
    <td>10+2 Intermediate Exam with NTT Training / B.Ed Exam Passed.</td> 
   </tr> 
  </tbody> 
 </table> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">Age Limit Details</h2>
 <table class="data-table dattatable table table-sm" id="table4">  
  <thead> 
   <tr> 
    <th class="font-weight-bold  bg-primary text-light   ">Post Name</th> 
    <th class="font-weight-bold  bg-primary text-light   ">Age Limit</th> 
   </tr> 
  </thead> 
  <tbody> 
   <tr class="border"> 
    <td>Trained Graduate Teacher TGT</td> 
    <td>Below 32 Years.</td> 
   </tr> 
   <tr class="border"> 
    <td>Junior Secretarial Assistant LDC</td> 
    <td>18-27 Years.</td> 
   </tr> 
   <tr class="border"> 
    <td>Patwari</td> 
    <td>21-27 Years.</td> 
   </tr> 
   <tr class="border"> 
    <td>Other Post</td> 
    <td>Below 30 Years.</td> 
   </tr> 
   <tr class="border"> 
    <td><b>Relaxation as per Govt. Rule</b></td> 
    <td></td> 
   </tr> 
  </tbody> 
 </table> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">Selection Process</h2>
 <table class="data-table dattatable table table-sm" id="table5">  
  <tbody> 
   <tr class="border"> 
    <td>1. Written Exam.</td> 
   </tr> 
  </tbody> 
 </table> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">Other details</h2>
 <table class="data-table dattatable table table-sm" id="table5">  
  <tbody> 
   <tr class="border"> 
    <td>1. Written Exam.</td> 
   </tr> 
  </tbody> 
 </table> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">How to Apply</h2>
 <table class="data-table dattatable table table-sm" id="table6">  
  <tbody> 
   <tr class="border"> 
    <td>1. Candidate Can Apply through Online Mode.</td> 
   </tr> 
   <tr class="border"> 
    <td>2. Click On the Apply Online Link given in Important Link Section.</td> 
   </tr> 
   <tr class="border"> 
    <td>3. Read Instructions then Proceed to the online application.</td> 
   </tr> 
   <tr class="border"> 
    <td>4. Fill All the Mandatory Details viz. Name, Father’s Name, Date of Birth, e-mail Address, Mobile Number, and Qualification, etc in the Application form.</td> 
   </tr> 
   <tr class="border"> 
    <td>5. Follow the instructions and complete the registration process step-by-step for getting a Registration Number &amp; Password.</td> 
   </tr> 
   <tr class="border"> 
    <td>6. Upload the Scanned Copy of Documents in the Prescribed Size.</td> 
   </tr> 
   <tr class="border"> 
    <td>7. Take Printout of your Application form for Future Reference.</td> 
   </tr> 
   <tr class="border"> 
    <td>8. Online Application Can be Submitted on or Before <span style="color:red">24th June 2021.</span></td> 
   </tr> 
  </tbody> 
 </table> 
</div>
<div class="job_card"> 
 <h2 class="job_card_heading grad">Important Links </h2> 
 <table class="table table-sm table-bordered w-100 linkstable mb-0"> 
  <tbody>
   <tr> 
    <td class="w-50"><b>Apply Online</b></td> 
    <td> <a class="insidelinkaa" href="https://dsssbonline.nic.in/" target="_blank" data-clickedon="Apply Online"> Click Here </a> </td> 
   </tr> 
   <tr> 
    <td class="w-50"><b>Download Notification</b></td> 
    <td> <a class="insidelinkaa" href="https://dsssb.delhi.gov.in/sites/default/files/All-PDF/Advt_02-21.pdf" target="_blank" data-clickedon="Download Notification"> Click Here </a> </td> 
   </tr> 
   <tr> 
    <td class="w-50"><b>Official Website</b></td> 
    <td> <a class="insidelinkaa" href="https://dsssbonline.nic.in/" target="_blank" data-clickedon="Official Website"> Click Here </a> </td> 
   </tr> 
  </tbody>
 </table> 
</div>"""

        jobSarkariPostFetcher.parseHtml(document) shouldBe expected
    }
}

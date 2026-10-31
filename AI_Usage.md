# AI Usage and Engineering Review Record

## 1. Purpose

This document records how AI was used as an engineering assistant while implementing the Course Waitlist assessment.

The objective was to implement a bounded course-registration workflow, review the proposed implementation, investigate edge cases, verify behavior with automated tests and API requests, and document the design and limitations.

AI suggestions were treated as proposals to review and verify, not as proof that the implementation was correct.

**Project:** Course Waitlist  
**Package:** `com.org.course_registration`  
**Technology:** Java 17, Spring Boot, Maven, JUnit 5, Spring MockMvc  
**Storage:** In-memory Java collections

## 2. Summary of AI-Assisted Activities

The following table summarizes the main engineering activities discussed during development. Prompt descriptions are paraphrased summaries unless explicitly identified as quoted text.

| Activity | AI contribution | Engineering decision |
|---|---|---|
| Enrollment and waitlist design | Suggested a small service, course model, and collection-based state management | Accepted the bounded service approach |
| Prerequisite and FIFO rules | Helped reason through eligibility checks, duplicate prevention, and promotion behavior | Implemented and tested the stated rules |
| Ineligible learners | Discussed how to handle an ineligible learner at the front of the queue | Adopted skip-and-retain behavior |
| REST API | Helped structure controller endpoints, DTOs, and result responses | Implemented the REST API |
| Automated testing | Suggested edge cases and controller tests | Added tests and investigated assertion failures |
| Eligibility updates | Helped refine eligibility updates for learners already enrolled or waitlisted | Edited the implementation to restrict updates to existing participants |
| Documentation and scope | Helped organize the README, design specification, and AI usage record | Prepared separate documentation deliverables |
| Database and frontend scope | Explained that the assessment did not require additional infrastructure or a frontend | Did not add MySQL, H2, or HTML pages |

## 3. Prompt and Decision Log

### Activity 1: Understanding the assessment and defining scope

**Prompt/interaction summary**

Asked for help implementing the supplied Course Waitlist assessment and understanding its acceptance criteria, scope boundaries, and deliverables.

**Relevant AI response summary**

The implementation should focus on a single course offering with:

- Enrollment and dropping.
- Prerequisite eligibility.
- Capacity enforcement.
- Duplicate prevention.
- FIFO waitlist promotion.
- Clear handling of invalid requests.
- Focused automated tests.
- A design specification, setup instructions, limitations, and an AI-use record.

The assessment explicitly limits unnecessary expansion of the system.

**Decision: Accepted**

Used the bounded-service approach rather than building a complete registration platform.

**Files and decisions informed**

- Overall project structure.
- `EnrollmentService.java`
- `CourseOffering.java`
- `EnrollmentController.java`
- Project documentation.

**Verification**

Compared the implementation scope against the supplied assessment requirements and reviewed the resulting API and tests.

### Activity 2: Choosing the data model and enrollment logic

**Prompt/interaction summary**

Asked for help implementing course enrollment, capacity checks, duplicate prevention, dropping, and FIFO waitlist promotion.

**Relevant AI response summary**

A small in-memory implementation was sufficient for the bounded feature. The suggested design used a course model and a service to enforce business rules.

The implementation uses:

- `LinkedHashSet<String>` for enrolled learners.
- `ArrayDeque<String>` for the waitlist.
- `HashMap<String, Boolean>` for prerequisite eligibility.

**Decision: Accepted, with implementation review**

Used these structures because they support the required enrollment and queue operations without introducing a database.

**Files informed**

- `CourseOffering.java`
- `EnrollmentService.java`
- `EnrollmentResult.java`
- `DropResult.java`

**Verification**

Ran service tests for available capacity, full capacity, duplicate requests, prerequisite failures, FIFO promotion, and drop behavior.

### Activity 3: Defining the ineligible-learner policy

**Prompt/interaction summary**

Reviewed the assessment's requirement to define what happens when an ineligible learner reaches the front of the waitlist.

**Relevant AI response summary**

Two policy choices were considered conceptually: leave an ineligible learner blocking the queue, or skip that learner while searching for the next eligible learner.

The selected implementation skips an ineligible learner during a promotion attempt but retains that learner in the waitlist, preserving the relative order of skipped learners.

**Decision: Accepted**

Adopted the skip-and-retain policy and documented it as a business rule.

**Files informed**

- `EnrollmentService.java`
- `CourseOffering.java`
- `EnrollmentServiceTest.java`
- `DESIGN.md`

**Verification**

Added or reviewed a test for skipping an ineligible learner and checking that the eligible learner is promoted while the skipped learner remains waiting in the correct relative order.

### Activity 4: Refining eligibility updates

**Prompt/interaction summary**

Reviewed whether prerequisite eligibility could be updated for learners who were already enrolled or waiting, rather than accepting updates for arbitrary learner IDs.

**Relevant AI response summary**

The proposed validation checks whether the learner is already enrolled or present on the waitlist before updating the eligibility map.

**Decision: Edited and accepted**

Updated `EnrollmentService.updateEligibility(...)` to:

1. Reject a null or blank learner ID.
2. Check whether the learner is enrolled.
3. Check whether the learner is waitlisted.
4. Reject the update if the learner is in neither state.
5. Update eligibility only for an existing participant.

**File informed**

- `EnrollmentService.java`

**Verification**

Reviewed the method's state checks and reran the test suite. A dedicated test specifically for every eligibility-update branch should be added if those branches are not already covered.

### Activity 5: Exposing the service through REST endpoints

**Prompt/interaction summary**

Asked how to expose enrollment, dropping, and course status through Spring Boot REST endpoints.

**Relevant AI response summary**

A controller can delegate business operations to the service and use DTOs for incoming requests and status responses.

The implemented endpoints are:

- `POST /api/courses/JAVA101/enrollments`
- `DELETE /api/courses/JAVA101/enrollments/{learnerId}`
- `GET /api/courses/JAVA101/enrollments/status`

The enrollment request contains `learnerId` and `prerequisiteSatisfied`. Result enums communicate outcomes such as `ENROLLED`, `WAITLISTED`, `INELIGIBLE`, and `INVALID_REQUEST`.

**Decision: Accepted**

Implemented the controller and DTO approach.

**Files informed**

- `EnrollmentController.java`
- `EnrollmentRequest.java`
- `CourseStatusResponse.java`
- `EnrollmentControllerTest.java`

**Verification**

Used API requests to check enrollment, duplicate enrollment, prerequisite failure, dropping, and course status. Added MockMvc tests for enrollment, status retrieval, invalid learner IDs, and unknown learners.

### Activity 6: Investigating a test assertion failure

**Prompt/interaction summary**

Investigated why a controller test expecting the text `ENROLLED` failed when the actual response body contained a JSON string.

**Observed issue**

The controller returned the enum result serialized as a JSON string, including quotation marks in the HTTP response body.

**Relevant AI response summary**

The test assertion needed to match the actual JSON representation rather than treating the response as unquoted plain text.

**Decision: Edited**

Changed the assertion from a plain string comparison to a JSON-aware assertion matching `"ENROLLED"` as a JSON string.

**File informed**

- `EnrollmentControllerTest.java`

**Verification**

Reran the controller tests and confirmed that the corrected assertion passed.

This was an example of checking the actual application response and adjusting the test to match the API contract rather than changing working application behavior merely to satisfy an incorrect assertion.

### Activity 7: Reviewing edge cases and test coverage

**Prompt/interaction summary**

Asked how to verify the edge cases and then confirmed that the observed API behavior was working correctly.

**Relevant AI response summary**

Testing should cover more than the successful enrollment path. It should verify duplicate handling, queue order, eligibility failures, invalid operations, and the state after unsuccessful operations.

**Decision: Accepted and reviewed**

The service and controller tests cover the principal required scenarios.

**Verification evidence**

The complete Maven test run reported:

```text
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
```

The reported suite consisted of service tests, controller tests, and the Spring Boot application test.

The user also manually checked REST API behavior, including enrollment, waitlisting, dropping, promotion, duplicate enrollment, prerequisite failure, and status retrieval.

The passing test result is evidence for the tested cases; it does not establish that every possible state transition or concurrency scenario has been tested.

### Activity 8: Reviewing database and frontend requirements

**Prompt/interaction summary**

Asked whether the assessment required MySQL, then clarified whether even an H2 database was needed. Also asked where Markdown and HTML files should be placed.

**Relevant AI response summary**

The assessment permits an in-memory store when no starter project requires persistence and explicitly says not to add a new database unless required. It also says not to build a frontend.

**Decision: Not adopted**

Did not add MySQL, H2, or HTML pages. Kept the implementation focused on the backend workflow using Java collections.

**Files and decisions informed**

- Project architecture.
- `README.md`
- `DESIGN.md`

**Verification**

Checked the decision against the supplied assessment's scope boundaries. The README documents that state is lost when the application restarts.

### Activity 9: Preparing the documentation and submission

**Prompt/interaction summary**

Asked where the Markdown files should be placed, whether the project covered the assessment, and how to refine the README and AI usage record.

**Relevant AI response summary**

The suggested documentation structure separates:

- `README.md`: overview, API endpoints, setup, test commands, and limitations.
- `DESIGN.md`: service boundary, data structures, invariants, waitlist policy, concurrency, and persistence trade-offs.
- `AI_USAGE.md`: prompt summaries, engineering decisions, accepted or edited suggestions, and verification evidence.

**Decision: Accepted**

Adopted the separate-document approach and kept these files in the project root alongside `pom.xml`.

**Verification**

Reviewed the proposed documentation against the assessment's deliverables. The final files should be checked against the actual source code before submission.

## 4. Suggestions Not Adopted or Rejected

The following scope decisions are confirmed by the development discussion:

| Item | Decision | Reason |
|---|---|---|
| MySQL database | Not adopted | Not required by the assessment |
| H2 database | Not adopted | In-memory Java collections are sufficient for the current scope |
| HTML/frontend | Not adopted | Explicitly outside the assessment scope |
| Full course catalog | Not implemented | The task is limited to one course offering |
| Payment and identity integrations | Not implemented | Outside the stated requirements |

These are documented scope decisions. The available conversation history does not establish a separate AI-generated code proposal that was explicitly rejected, so no such rejection is claimed here.

## 5. Independent Review and Verification Approach

AI-assisted suggestions were reviewed through the following activities:

1. **Requirements review:** Compared the proposed design with the assessment's acceptance criteria and scope boundaries.
2. **Code review:** Examined the service, model, controller, DTOs, and result types used by the implementation.
3. **Edge-case review:** Considered duplicate requests, prerequisite failures, queue order, invalid IDs, and repeated drops.
4. **Response inspection:** Investigated an assertion mismatch by checking the actual JSON response.
5. **Automated verification:** Ran the complete Maven test suite.
6. **Manual API verification:** Checked representative REST requests and responses.
7. **Documentation review:** Prepared documentation for assumptions, setup, design trade-offs, and limitations.

## 6. Limitations and Remaining Verification Risks

- The application uses in-memory state; data is lost on restart.
- Only one course offering is supported.
- Prerequisite eligibility is supplied by the API caller rather than checked against an external system.
- `synchronized` service methods serialize operations within a single application instance but do not coordinate multiple instances.
- A dedicated concurrency test has not been confirmed in the available test record.
- Eligibility-update behavior should have focused tests for enrolled, waitlisted, and unknown learners if those cases are not already covered.
- The complete test suite passing does not replace review of the business rules and state transitions.

These are known scope limitations or verification items, not claims that the implementation has failed.

## 7. Final Verification Evidence

The latest reported full test run was:

```text
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
```

The command used to execute the tests is:

```bash
./mvnw test
```

The test result and manual API checks provide evidence that the implemented scenarios work as expected. The source code, tests, and documentation should be submitted together.

## 8. Reflection

AI helped accelerate design discussions, identify edge cases, structure the REST API and tests, troubleshoot an assertion mismatch, and organize documentation. The engineering work also involved making scope decisions, reviewing proposed behavior, checking actual responses, and running automated tests.

The final implementation should be evaluated against the assessment requirements and the verification evidence, rather than assuming that AI-generated suggestions are correct by default.

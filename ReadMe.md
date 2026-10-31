# Course Waitlist

## 1. Overview

A Spring Boot REST API implementing enrollment, dropping, prerequisite eligibility checks, and FIFO waitlist promotion for a single course offering.

This project was developed as part of the **Focused AI-Assisted Engineering Assessment: Course Waitlist**. The focus is on correct enrollment state, capacity management, waitlist behavior, automated testing, and review of AI-assisted engineering decisions.

## 2. Technology Stack

- Java 17
- Spring Boot
- Maven
- JUnit 5
- Spring MockMvc
- In-memory data structures

## 3. Scope

The application supports one course offering:

- Course ID: `JAVA101`
- Capacity: `2` learners

The capacity is configured in the service implementation for demonstration purposes.

The implementation includes enrollment, dropping, prerequisite checks, duplicate prevention, waitlist management, and promotion when a seat becomes available.

No frontend, course catalog, payment functionality, external identity integration, or database has been added.

## 4. Business Rules and Assumptions

1. A learner must satisfy the prerequisite to enroll or join the waitlist.
2. A learner can be enrolled only once.
3. An eligible learner is enrolled immediately when a seat is available.
4. When the course is full, an eligible learner is added to the end of the waitlist.
5. Duplicate enrollment requests do not create duplicate enrollment or waitlist records.
6. Dropping an enrolled learner frees a seat and initiates waitlist promotion.
7. Promotion follows FIFO order among eligible learners.
8. If an ineligible learner reaches the front of the queue, the learner is skipped for that promotion attempt and retained on the waitlist in the original relative order.
9. An ineligible learner cannot be enrolled or promoted.
10. Invalid requests and unsuccessful drops return explicit results without intentionally modifying enrollment or capacity state.

Prerequisite eligibility is supplied by the API caller. It is not independently verified against an external prerequisite system.

## 5. REST API

Base path:

`/api/courses/JAVA101/enrollments`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/courses/JAVA101/enrollments` | Enroll or waitlist a learner |
| DELETE | `/api/courses/JAVA101/enrollments/{learnerId}` | Drop an enrolled learner |
| GET | `/api/courses/JAVA101/enrollments/status` | View enrollment and waitlist state |

### Example enrollment request

```json
{
  "learnerId": "A",
  "prerequisiteSatisfied": true
}
```

### Example results

Enrollment requests can return:

- `ENROLLED`
- `WAITLISTED`
- `ALREADY_REGISTERED`
- `INELIGIBLE`
- `INVALID_REQUEST`

Drop requests can return:

- `DROPPED`
- `NOT_ENROLLED`
- `INVALID_REQUEST`

Invalid requests return HTTP 400. The API returns the result as a JSON string.

### Example status response

```json
{
  "courseId": "JAVA101",
  "capacity": 2,
  "enrolledLearners": ["A", "B"],
  "waitlist": ["C"]
}
```

## 6. Design Overview

The implementation separates the REST API, service logic, data model, and request/response DTOs.

- **EnrollmentController:** Exposes the REST endpoints and maps requests to service operations.
- **EnrollmentService:** Implements enrollment, dropping, eligibility updates, and waitlist promotion.
- **CourseOffering:** Maintains the course capacity, enrolled learners, and waitlist.
- **DTOs:** Represent incoming enrollment requests and course status responses.

### Data structures

- `LinkedHashSet` maintains enrolled learners and prevents duplicate entries.
- `ArrayDeque` represents the FIFO waitlist.
- `HashMap` stores prerequisite eligibility for learners.

State-changing service methods use Java's `synchronized` mechanism to serialize operations within a single application instance.

The application uses in-memory storage, so state is lost when the application restarts.

## 7. Automated Testing and Verification

The test suite covers the core enrollment and waitlist scenarios, including:

- Enrollment when capacity is available.
- Waitlisting when the course is full.
- Duplicate enrollment prevention.
- FIFO waitlist promotion.
- Prerequisite failure.
- Skipping ineligible learners during promotion while preserving queue order.
- Invalid and repeated drop requests.
- Duplicate waitlist prevention.
- REST API enrollment, status retrieval, and error/result responses.

### Test execution

Run the complete test suite from the project root:

```bash
./mvnw test
```

### Verification result

The complete test run reported:

```text
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
```

REST API behavior was also checked manually during development.

## 8. AI-Assisted Engineering

AI was used as an engineering assistant during implementation and review, including discussions about enrollment logic, waitlist edge cases, REST endpoints, automated tests, and project documentation.

Suggestions were reviewed against the assessment requirements, and the implementation was tested rather than relying on AI-generated code alone.

The detailed prompt summaries, accepted or edited suggestions, implementation decisions, and verification evidence are recorded separately in [`AI_USAGE.md`](AI_USAGE.md).

## 9. Setup and Run Instructions

### Prerequisites

- Java 17 or a compatible configured Java runtime.
- Git, if cloning the repository.
- The Maven Wrapper included in the project.

### Run the application

```bash
./mvnw spring-boot:run
```

The application starts using the Spring Boot configuration in the project.

### Run automated tests

```bash
./mvnw test
```

### Stop the application

Use `Ctrl+C` in the terminal running the application.

## 10. Limitations and Follow-up Risks

- Only one course offering is supported.
- Capacity is configured in the service implementation.
- Enrollment, waitlist, and eligibility state are stored in memory and reset on restart.
- Prerequisite eligibility is supplied by the caller.
- Synchronization protects operations within a single application instance; it does not coordinate state across multiple application instances.
- The implementation does not include persistent transactions, authentication, authorization, or integration with external course systems.

These limitations are intentional for the bounded assessment scope.

## 11. Assessment Deliverables

- Runnable Spring Boot application.
- Focused automated tests.
- Design specification in `DESIGN.md`.
- AI prompt and usage record in `AI_USAGE.md`.
- Setup instructions, test command, and documented limitations in this README.

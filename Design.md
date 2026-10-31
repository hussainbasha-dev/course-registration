# Design Specification

## Service Boundary
EnrollmentService implements enrollment, dropping, eligibility
tracking, and waitlist promotion. EnrollmentController exposes
REST endpoints.

## Data Structures
- LinkedHashSet: enrolled learners, with duplicate prevention
  and insertion order.
- ArrayDeque: FIFO waitlist.
- HashMap: learner prerequisite eligibility.

## Invariants
- Enrolled learner count must not exceed course capacity.
- A learner must not be enrolled more than once.
- Only eligible learners can be enrolled or promoted.
- Waitlist order is preserved when ineligible learners are skipped.

## Concurrency
Service operations use synchronized methods to serialize
state-changing operations within this application instance.

## Persistence
The implementation uses in-memory storage. State is lost when
the application restarts.
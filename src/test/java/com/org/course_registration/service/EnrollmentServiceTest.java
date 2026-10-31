package com.org.course_registration.service;

import com.org.course_registration.model.CourseOffering;
import com.org.course_registration.model.DropResult;
import com.org.course_registration.model.EnrollmentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceTest {

    private EnrollmentService service;

    @BeforeEach
    void setUp() {
        service = new EnrollmentService();
    }

    @Test
    void shouldEnrollWhenCapacityIsAvailable() {
        EnrollmentResult result =
                service.enroll("A", true);

        assertEquals(EnrollmentResult.ENROLLED, result);
        assertTrue(
                service.getCourseOffering()
                        .getEnrolledLearners().contains("A"));
    }

    @Test
    void shouldWaitlistWhenCourseIsFull() {
        service.enroll("A", true);
        service.enroll("B", true);

        EnrollmentResult result =
                service.enroll("C", true);

        assertEquals(EnrollmentResult.WAITLISTED, result);
        assertTrue(
                service.getCourseOffering()
                        .getWaitlist().contains("C"));
    }

    @Test
    void shouldPreventDuplicateEnrollmentRequests() {
        service.enroll("A", true);
        service.enroll("B", true);

        EnrollmentResult result =
                service.enroll("A", true);

        assertEquals(
                EnrollmentResult.ALREADY_REGISTERED, result);

        assertEquals(
                2,
                service.getCourseOffering()
                        .getEnrolledLearners().size());
    }

    @Test
    void shouldPromoteEarliestEligibleWaitlistedLearner() {
        service.enroll("A", true);
        service.enroll("B", true);
        service.enroll("C", true);
        service.enroll("D", true);

        service.drop("A");

        CourseOffering course = service.getCourseOffering();

        assertTrue(course.getEnrolledLearners().contains("C"));
        assertFalse(course.getEnrolledLearners().contains("D"));
        assertEquals(
                java.util.List.of("D"),
                java.util.List.copyOf(course.getWaitlist()));
    }

    @Test
    void shouldRejectLearnerWhoFailsPrerequisite() {
        EnrollmentResult result =
                service.enroll("A", false);

        assertEquals(EnrollmentResult.INELIGIBLE, result);
        assertFalse(
                service.getCourseOffering()
                        .getEnrolledLearners().contains("A"));
    }

    @Test
    void shouldSkipIneligibleWaitlistedLearnerAndPreserveOrder() {
        service.enroll("A", true);
        service.enroll("B", true);
        service.enroll("C", true);
        service.enroll("D", true);
        service.enroll("E", true);

        // C becomes ineligible after joining the waitlist.
        assertTrue(service.updateEligibility("C", false));

        service.drop("A");

        CourseOffering course = service.getCourseOffering();

        assertTrue(course.getEnrolledLearners().contains("D"));
        assertFalse(course.getEnrolledLearners().contains("C"));
        assertEquals(
                java.util.List.of("C", "E"),
                java.util.List.copyOf(course.getWaitlist()));
    }

    @Test
    void shouldRejectInvalidAndRepeatedDrops() {
        assertEquals(
                DropResult.INVALID_REQUEST,
                service.drop(" "));

        service.enroll("A", true);

        assertEquals(DropResult.DROPPED, service.drop("A"));
        assertEquals(
                DropResult.NOT_ENROLLED,
                service.drop("A"));
    }

    @Test
    void shouldRejectDuplicateWaitlistRequests() {
        service.enroll("A", true);
        service.enroll("B", true);
        service.enroll("C", true);

        EnrollmentResult result =
                service.enroll("C", true);

        assertEquals(
                EnrollmentResult.ALREADY_REGISTERED, result);

        assertEquals(
                1,
                service.getCourseOffering()
                        .getWaitlist().stream()
                        .filter("C"::equals)
                        .count());
    }
}

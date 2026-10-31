package com.org.course_registration.dto;

public record EnrollmentRequest(
        String learnerId,
        boolean prerequisiteSatisfied
) {
}
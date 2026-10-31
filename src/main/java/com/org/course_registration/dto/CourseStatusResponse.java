package com.org.course_registration.dto;

import java.util.List;

public record CourseStatusResponse(
        String courseId,
        int capacity,
        List<String> enrolledLearners,
        List<String> waitlist
) {}
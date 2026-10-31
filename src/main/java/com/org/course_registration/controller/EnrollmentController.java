package com.org.course_registration.controller;

import com.org.course_registration.dto.CourseStatusResponse;
import com.org.course_registration.dto.EnrollmentRequest;
import com.org.course_registration.model.DropResult;
import com.org.course_registration.model.EnrollmentResult;
import com.org.course_registration.service.EnrollmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/JAVA101/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResult> enroll(
            @RequestBody EnrollmentRequest request) {

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        EnrollmentResult result = enrollmentService.enroll(
                request.learnerId(),
                request.prerequisiteSatisfied()
        );

        if (result == EnrollmentResult.INVALID_REQUEST) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{learnerId}")
    public ResponseEntity<DropResult> drop(
            @PathVariable String learnerId) {

        DropResult result = enrollmentService.drop(learnerId);

        if (result == DropResult.INVALID_REQUEST) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }


    @GetMapping("/status")
    public CourseStatusResponse getStatus() {
        var course = enrollmentService.getCourseOffering();

        return new CourseStatusResponse(
                course.getCourseId(),
                course.getCapacity(),
                List.copyOf(course.getEnrolledLearners()),
                List.copyOf(course.getWaitlist())
        );
    }
}
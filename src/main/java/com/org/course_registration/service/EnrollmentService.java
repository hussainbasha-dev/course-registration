
package com.org.course_registration.service;

import com.org.course_registration.model.CourseOffering;
import com.org.course_registration.model.DropResult;
import org.springframework.stereotype.Service;

import com.org.course_registration.model.EnrollmentResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnrollmentService {

    private final CourseOffering courseOffering =
            new CourseOffering("JAVA101", 2);

    private final Map<String, Boolean> eligibility =
            new HashMap<>();

    public synchronized EnrollmentResult enroll(
            String learnerId,
            boolean prerequisiteSatisfied) {

        if (learnerId == null || learnerId.isBlank()) {
            return EnrollmentResult.INVALID_REQUEST;
        }

        if (courseOffering.isRegistered(learnerId)) {
            return EnrollmentResult.ALREADY_REGISTERED;
        }

        eligibility.put(learnerId, prerequisiteSatisfied);

        if (!prerequisiteSatisfied) {
            return EnrollmentResult.INELIGIBLE;
        }

        if (courseOffering.hasAvailableSeat()) {
            courseOffering.enrollLearner(learnerId);
            return EnrollmentResult.ENROLLED;
        }

        courseOffering.addToWaitlist(learnerId);
        return EnrollmentResult.WAITLISTED;
    }

    public synchronized DropResult drop(String learnerId) {

        if (learnerId == null || learnerId.isBlank()) {
            return DropResult.INVALID_REQUEST;
        }

        if (!courseOffering.dropLearner(learnerId)) {
            return DropResult.NOT_ENROLLED;
        }

        promoteNextEligibleLearner();

        return DropResult.DROPPED;
    }

    public synchronized boolean updateEligibility(
            String learnerId,
            boolean prerequisiteSatisfied) {

        if (learnerId == null || learnerId.isBlank()) {
            return false;
        }

        boolean isEnrolled = courseOffering.isRegistered(learnerId);
        boolean isWaitlisted = courseOffering.getWaitlist()
                .contains(learnerId);

        if (!isEnrolled && !isWaitlisted) {
            return false;
        }

        eligibility.put(learnerId, prerequisiteSatisfied);
        return true;
    }

    private void promoteNextEligibleLearner() {

        List<String> skippedLearners = new ArrayList<>();

        while (!courseOffering.getWaitlist().isEmpty()) {

            String learnerId = courseOffering.pollWaitlist();

            if (Boolean.TRUE.equals(eligibility.get(learnerId))) {

                courseOffering.enrollLearner(learnerId);
                break;

            } else {
                skippedLearners.add(learnerId);
            }
        }

        // Restore skipped learners in their original FIFO order.
        for (int i = skippedLearners.size() - 1; i >= 0; i--) {
            courseOffering.addToWaitlistFront(
                    skippedLearners.get(i));
        }
    }

    public CourseOffering getCourseOffering() {
        return courseOffering;
    }
}

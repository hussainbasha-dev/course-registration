package com.org.course_registration.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.Deque;
import java.util.Objects;

public class CourseOffering {

    private final String courseId;
    private final int capacity;

    private final Set<String> enrolledLearners =
            new LinkedHashSet<>();

    private final Deque<String> waitlist =
            new ArrayDeque<>();

    public CourseOffering(String courseId, int capacity) {
        if (courseId == null || courseId.isBlank()) {
            throw new IllegalArgumentException(
                    "Course ID cannot be empty");
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Capacity must be greater than zero");
        }

        this.courseId = courseId;
        this.capacity = capacity;
    }

    public String getCourseId() {
        return courseId;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<String> getEnrolledLearners() {
        return Collections.unmodifiableSet(enrolledLearners);
    }

    public Deque<String> getWaitlist() {
        return new ArrayDeque<>(waitlist);
    }

    public boolean hasAvailableSeat() {
        return enrolledLearners.size() < capacity;
    }

    public boolean isRegistered(String learnerId) {
        return enrolledLearners.contains(learnerId)
                || waitlist.contains(learnerId);
    }

    public boolean enrollLearner(String learnerId) {
        if (!hasAvailableSeat()) {
            return false;
        }

        return enrolledLearners.add(learnerId);
    }

    public boolean addToWaitlist(String learnerId) {
        if (isRegistered(learnerId)) {
            return false;
        }

        waitlist.addLast(learnerId);
        return true;
    }

    public boolean addToWaitlistFront(String learnerId) {
        if (isRegistered(learnerId)) {
            return false;
        }

        waitlist.addFirst(learnerId);
        return true;
    }

    public boolean dropLearner(String learnerId) {
        return enrolledLearners.remove(learnerId);
    }

    public String pollWaitlist() {
        return waitlist.pollFirst();
    }
}

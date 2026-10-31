package com.org.course_registration.controller;

import com.org.course_registration.dto.EnrollmentRequest;
import com.org.course_registration.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldEnrollLearnerSuccessfully() throws Exception {
        mockMvc.perform(post("/api/courses/JAVA101/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "learnerId": "TEST-A",
                      "prerequisiteSatisfied": true
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().json("\"ENROLLED\""));
    }

    @Test
    void shouldReturnCourseStatus() throws Exception {
        mockMvc.perform(get("/api/courses/JAVA101/enrollments/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("JAVA101"))
                .andExpect(jsonPath("$.capacity").value(2));
    }


    @Test
    void shouldRejectBlankLearnerId() throws Exception {
        mockMvc.perform(post("/api/courses/JAVA101/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "learnerId": "",
                      "prerequisiteSatisfied": true
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("\"INVALID_REQUEST\""));
    }


    @Test
    void shouldReturnNotEnrolledForUnknownLearner() throws Exception {
        mockMvc.perform(delete(
                        "/api/courses/JAVA101/enrollments/UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(content().json("\"NOT_ENROLLED\""));
    }
}
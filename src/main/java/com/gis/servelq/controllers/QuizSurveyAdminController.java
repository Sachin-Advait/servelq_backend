package com.gis.servelq.controllers;

import com.gis.servelq.dto.*;
import com.gis.servelq.models.QuizSurveyModel;
import com.gis.servelq.models.ResponseModel;
import com.gis.servelq.services.QuizSurveyAdminService;
import com.gis.servelq.services.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/serveiq/api/admin/quiz-survey")
@RequiredArgsConstructor
public class QuizSurveyAdminController {

    private final QuizSurveyAdminService adminService;
    private final ResultService resultService;

    /* ---------------- CREATE ---------------- */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<QuizSurveyModel>> create(@RequestBody QuizSurveyModel model) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Quiz/Survey created successfully",
                adminService.create(model))
        );
    }

    /* ---------------- UPDATE ---------------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<QuizSurveyModel>> update(
            @PathVariable UUID id,
            @RequestBody QuizSurveyModel model
    ) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Quiz/Survey updated successfully",
                adminService.update(id, model))
        );
    }

    /* ---------------- DELETE ---------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable UUID id) {
        adminService.delete(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Quiz/Survey deleted", null));
    }

    /* ---------------- LIST ALL ---------------- */
    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<QuizSurveyModel>>> getAll() {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "All quizzes & surveys fetched",
                adminService.getAll())
        );
    }

    /* ---------------- MANUAL ANNOUNCE ---------------- */
    @PostMapping("/{id}/announce")
    public ResponseEntity<ApiResponseDTO<Void>> announce(@PathVariable UUID id) {
        adminService.manualAnnounce(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Announcement sent", null));
    }

    /* ---------------- VIEW RESPONSES ---------------- */
    @GetMapping("/{id}/responses")
    public ResponseEntity<ApiResponseDTO<List<ResponseModel>>> getResponses(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Responses fetched",
                adminService.getResponses(id))
        );
    }

    /* ---------------- USER QUIZ RESULT ---------------- */
    @GetMapping("/quiz-result/{quizSurveyId}")
    public ResponseEntity<ApiResponseDTO<QuizResultDTO>> getQuizResultByUserId(
            @PathVariable UUID quizSurveyId,
            @RequestParam String userId
    ) {
        QuizResultDTO response = resultService.getQuizResultByUserId(quizSurveyId, userId);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Quiz result retrieved successfully",
                response)
        );
    }

    /* ---------------- ADMIN QUIZ RESULTS ---------------- */
    @GetMapping("/admin-quiz-result/{quizSurveyId}")
    public ResponseEntity<ApiResponseDTO<List<QuizResultAdminDTO>>> getQuizResultsAdmin(
            @PathVariable UUID quizSurveyId) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                        true,
                        "Quiz results retrieved successfully",
                        resultService.getQuizResultsAdmin(quizSurveyId)
                )
        );
    }

    /* =====================================================
      📊 INSIGHTS & ANALYTICS
      ===================================================== */
    @GetMapping("/quiz-insights/{quizSurveyId}")
    public ResponseEntity<ApiResponseDTO<QuizInsightsDTO>> getQuizInsights(
            @PathVariable UUID quizSurveyId) {

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Quiz insights retrieved successfully",
                adminService.getQuizInsights(quizSurveyId))
        );
    }

    @GetMapping("/completion-stats/{quizSurveyId}")
    public ResponseEntity<ApiResponseDTO<QuizCompletionStatsDTO>> getQuizCompletionStats(
            @PathVariable UUID quizSurveyId) {

        return ResponseEntity.ok(new ApiResponseDTO<>(true,
                "Quiz completion stats retrieved successfully",
                adminService.getQuizCompletionStats(quizSurveyId))
        );
    }
}

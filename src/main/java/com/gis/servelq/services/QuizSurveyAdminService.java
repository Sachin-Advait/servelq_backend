package com.gis.servelq.services;

import com.gis.servelq.controllers.QuizSurveySocketController;
import com.gis.servelq.dto.*;
import com.gis.servelq.models.AnnouncementMode;
import com.gis.servelq.models.QuizSurveyModel;
import com.gis.servelq.models.ResponseModel;
import com.gis.servelq.repository.QuizSurveyRepository;
import com.gis.servelq.repository.ResponseRepo;
import com.gis.servelq.services.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizSurveyAdminService {

    private final QuizSurveyRepository quizSurveyRepo;
    private final ResponseRepo responseRepo;
    private final QuizSurveySocketController socketController;
    private final FCMService fcmService;

    /* ---------------- CREATE ---------------- */
    public QuizSurveyModel create(QuizSurveyModel model) {
        model.setIsAnnounced(
                model.getAnnouncementMode() == AnnouncementMode.IMMEDIATE
        );

        QuizSurveyModel saved = quizSurveyRepo.save(model);

        if (saved.getIsAnnounced()) {
            pushNotifications(saved);
        }

        return saved;
    }

    /* ---------------- UPDATE ---------------- */
    public QuizSurveyModel update(UUID id, QuizSurveyModel updated) {

        QuizSurveyModel existing = quizSurveyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        if (updated.getTitle() != null)
            existing.setTitle(updated.getTitle());

        if (updated.getType() != null)
            existing.setType(updated.getType());

        if (updated.getDefinitionJson() != null)
            existing.setDefinitionJson(updated.getDefinitionJson());

        if (updated.getAnswerKey() != null)
            existing.setAnswerKey(updated.getAnswerKey());

        if (updated.getMaxScore() != null)
            existing.setMaxScore(updated.getMaxScore());

        if (updated.getQuizDuration() != null)
            existing.setQuizDuration(updated.getQuizDuration());

        if (updated.getQuizTotalDuration() != null)
            existing.setQuizTotalDuration(updated.getQuizTotalDuration());

        if (updated.getIsMandatory() != null)
            existing.setIsMandatory(updated.getIsMandatory());

        if (updated.getMaxRetake() != null)
            existing.setMaxRetake(updated.getMaxRetake());

        if (updated.getVisibilityType() != null)
            existing.setVisibilityType(updated.getVisibilityType());

        if (updated.getTargetedUsers() != null)
            existing.setTargetedUsers(updated.getTargetedUsers());

        if (updated.getUserDataDisplayFields() != null)
            existing.setUserDataDisplayFields(updated.getUserDataDisplayFields());

        if (updated.getAnnouncementMode() != null)
            existing.setAnnouncementMode(updated.getAnnouncementMode());

        if (updated.getScheduledTime() != null)
            existing.setScheduledTime(updated.getScheduledTime());

        if (updated.getStatus() != null)
            existing.setStatus(updated.getStatus());

        return quizSurveyRepo.save(existing);
    }


    /* ---------------- DELETE ---------------- */
    public void delete(UUID id) {
        quizSurveyRepo.deleteById(id);
    }

    /* ---------------- LIST ALL ---------------- */
    public List<QuizSurveyModel> getAll() {
        return quizSurveyRepo.findAll();
    }

    /* ---------------- MANUAL ANNOUNCE ---------------- */
    public void manualAnnounce(UUID quizId) {

        QuizSurveyModel quiz =
                quizSurveyRepo.findById(quizId)
                        .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        if (quiz.getIsAnnounced()) {
            return;
        }

        quiz.setIsAnnounced(true);
        quizSurveyRepo.save(quiz);

        pushNotifications(quiz);
    }

    /* ---------------- RESPONSES ---------------- */
    public List<ResponseModel> getResponses(UUID quizId) {
        return responseRepo.findByQuizSurveyId(quizId);
    }

    /* ---------------- COMMON PUSH ---------------- */
    private void pushNotifications(QuizSurveyModel quiz) {

        socketController.pushNewSurvey(
                quiz.getId().toString(),
                quiz.getIsMandatory(),
                new ArrayList<>(quiz.getTargetedUsers())
        );

        fcmService.notifyQuizSurveyAssigned(quiz);
    }
//
//    public QuizInsightsDTO getQuizInsights(UUID quizSurveyId) {
//        // aggregate score, attempts, avg score, top scorers
//        return QuizInsightsDTO.builder().build();
//    }
//
//    public QuizCompletionStatsDTO getQuizCompletionStats(UUID quizSurveyId) {
//        // total assigned vs attempted vs completed
//        return QuizCompletionStatsDTO.builder().build();
//    }
//
//    public SurveyResponseStatsDTO getSurveyInsightStats(UUID surveyId) {
//        return SurveyResponseStatsDTO.builder().build();
//    }
//
//    public SurveyActivityStatsDTO getSurveyActivityStats(UUID surveyId) {
//        return SurveyActivityStatsDTO.builder().build();
//    }
//
//    public SatisfactionInsightResponse getSatisfactionInsights(UUID surveyId) {
//        return SatisfactionInsightResponse.builder().build();
//    }
//

    public QuizInsightsDTO getQuizInsights(UUID quizSurveyId) {

        QuizSurveyModel quiz =
                quizSurveyRepo.findById(quizSurveyId)
                        .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<ResponseModel> responses =
                responseRepo.findByQuizSurveyId(quizSurveyId);

        if (responses.isEmpty()) {
            return QuizInsightsDTO.builder()
                    .title(quiz.getTitle())
                    .averageScore(0)
                    .passRate(0)
                    .failRate(0)
                    .build();
        }

        int maxScore = quiz.getMaxScore();
        double passMark = maxScore * 0.5;

        double avgScore =
                responses.stream()
                        .filter(r -> r.getScore() != null)
                        .mapToInt(ResponseModel::getScore)
                        .average()
                        .orElse(0);

        long passed =
                responses.stream()
                        .filter(r -> r.getScore() != null && r.getScore() >= passMark)
                        .count();

        long failed = responses.size() - passed;

        ResponseModel top =
                responses.stream()
                        .filter(r -> r.getScore() != null)
                        .max((a, b) -> a.getScore().compareTo(b.getScore()))
                        .orElse(null);

        ResponseModel lowest =
                responses.stream()
                        .filter(r -> r.getScore() != null)
                        .min((a, b) -> a.getScore().compareTo(b.getScore()))
                        .orElse(null);

        return QuizInsightsDTO.builder()
                .title(quiz.getTitle())
                .averageScore(avgScore)
                .passRate((passed * 100.0) / responses.size())
                .failRate((failed * 100.0) / responses.size())
                .topScorer(
                        top == null ? null :
                                QuizInsightsDTO.ScorerDTO.builder()
                                        .name(top.getUsername())
                                        .score(top.getScore())
                                        .build()
                )
                .lowestScorer(
                        lowest == null ? null :
                                QuizInsightsDTO.ScorerDTO.builder()
                                        .name(lowest.getUsername())
                                        .score(lowest.getScore())
                                        .build()
                )
                .mostIncorrectQuestions(List.of()) // optional enhancement
                .build();
    }


    public QuizCompletionStatsDTO getQuizCompletionStats(UUID quizSurveyId) {

        QuizSurveyModel quiz =
                quizSurveyRepo.findById(quizSurveyId)
                        .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int totalAssigned =
                quiz.getTargetedUsers() == null ? 0 : quiz.getTargetedUsers().size();

        int totalCompleted =
                (int) responseRepo.findByQuizSurveyId(quizSurveyId)
                        .stream()
                        .map(ResponseModel::getUserId)
                        .distinct()
                        .count();


        int totalNotCompleted = totalAssigned - totalCompleted;

        double completionRate =
                totalAssigned == 0
                        ? 0
                        : (totalCompleted * 100.0) / totalAssigned;

        return new QuizCompletionStatsDTO(
                totalAssigned,
                totalCompleted,
                Math.max(totalNotCompleted, 0),
                completionRate
        );
    }


    public SurveyResponseStatsDTO getSurveyInsightStats(UUID surveyId) {

        QuizSurveyModel survey =
                quizSurveyRepo.findById(surveyId)
                        .orElseThrow(() -> new RuntimeException("Survey not found"));

        int totalInvited =
                survey.getTargetedUsers() == null ? 0 : survey.getTargetedUsers().size();

        int totalResponded =
                responseRepo.findByQuizSurveyId(surveyId).size();

        double responseRate =
                totalInvited == 0
                        ? 0
                        : (totalResponded * 100.0) / totalInvited;

        return SurveyResponseStatsDTO.builder()
                .title(survey.getTitle())
                .overall(
                        SurveyResponseStatsDTO.OverallStatsDTO.builder()
                                .totalInvited(totalInvited)
                                .totalResponded(totalResponded)
                                .overallResponseRate(responseRate)
                                .build()
                )
                .build();
    }



    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public SatisfactionInsightResponse getSatisfactionInsights(UUID surveyId) {

        QuizSurveyModel survey =
                quizSurveyRepo.findById(surveyId)
                        .orElseThrow(() -> new RuntimeException("Survey not found"));

        List<ResponseModel> responses =
                responseRepo.findByQuizSurveyId(surveyId);

        if (responses.isEmpty()) {
            return SatisfactionInsightResponse.builder()
                    .title(survey.getTitle())
                    .averageSatisfactionBySurveyType(Map.of())
                    .scoreDistributionPerQuestion(List.of())
                    .build();
        }

        /* ---------- AVERAGE SATISFACTION ---------- */

        Map<String, List<Integer>> valuesByType = new HashMap<>();

        for (ResponseModel r : responses) {
            Map<String, Object> answers = r.getAnswers();
            if (answers == null) continue;

            for (Object v : answers.values()) {
                if (v instanceof Number n) {
                    valuesByType
                            .computeIfAbsent(survey.getType(), k -> new ArrayList<>())
                            .add(n.intValue());
                }
            }
        }

        Map<String, Double> avgBySurveyType = new HashMap<>();
        valuesByType.forEach((type, scores) ->
                avgBySurveyType.put(type, round(
                        scores.stream().mapToInt(Integer::intValue).average().orElse(0)
                ))
        );

        /* ---------- QUESTION DISTRIBUTION ---------- */

        Map<String, Map<Integer, Integer>> questionDistribution = new HashMap<>();

        for (ResponseModel r : responses) {
            Map<String, Object> answers = r.getAnswers();
            if (answers == null) continue;

            answers.forEach((question, value) -> {
                if (value instanceof Number n) {
                    questionDistribution
                            .computeIfAbsent(question, q -> new HashMap<>())
                            .merge(n.intValue(), 1, Integer::sum);
                }
            });
        }

        List<SatisfactionInsightResponse.QuestionDistribution> distributions =
                questionDistribution.entrySet()
                        .stream()
                        .map(e -> SatisfactionInsightResponse.QuestionDistribution.builder()
                                .question(e.getKey())
                                .distribution(e.getValue())
                                .build())
                        .toList();

        return SatisfactionInsightResponse.builder()
                .title(survey.getTitle())
                .averageSatisfactionBySurveyType(avgBySurveyType)
                .scoreDistributionPerQuestion(distributions)
                .build();
    }






}

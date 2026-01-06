package com.gis.servelq.services;

import com.gis.servelq.controllers.QuizSurveySocketController;
import com.gis.servelq.models.AnnouncementMode;
import com.gis.servelq.models.QuizSurveyModel;
import com.gis.servelq.models.ResponseModel;
import com.gis.servelq.repository.QuizSurveyRepository;
import com.gis.servelq.repository.ResponseRepo;
import com.gis.servelq.services.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        QuizSurveyModel existing =
                quizSurveyRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        existing.setTitle(updated.getTitle());
        existing.setType(updated.getType());
        existing.setDefinitionJson(updated.getDefinitionJson());
        existing.setAnswerKey(updated.getAnswerKey());
        existing.setMaxScore(updated.getMaxScore());
        existing.setQuizDuration(updated.getQuizDuration());
        existing.setQuizTotalDuration(updated.getQuizTotalDuration());
        existing.setIsMandatory(updated.getIsMandatory());
        existing.setMaxRetake(updated.getMaxRetake());
        existing.setVisibilityType(updated.getVisibilityType());
        existing.setTargetedUsers(updated.getTargetedUsers());
        existing.setUserDataDisplayFields(updated.getUserDataDisplayFields());
        existing.setAnnouncementMode(updated.getAnnouncementMode());
        existing.setScheduledTime(updated.getScheduledTime());

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

}

package com.gis.servelq.services;

import com.gis.servelq.dto.QuizResultAdminDTO;
import com.gis.servelq.dto.QuizResultDTO;
import com.gis.servelq.models.QuizSurveyModel;
import com.gis.servelq.models.ResponseModel;
import com.gis.servelq.models.SurveyDefinition;
import com.gis.servelq.repository.QuizSurveyRepository;
import com.gis.servelq.repository.ResponseRepo;
import com.gis.servelq.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ResultService {

    private final QuizSurveyRepository quizSurveyRepo;
    private final ResponseRepo responseRepo;
    private final UserRepository userRepository;

    // -------------------- QUIZ RESULTS --------------------
    public List<QuizResultAdminDTO> getQuizResultsAdmin(UUID quizSurveyId) {
        QuizSurveyModel quizSurvey = quizSurveyRepo.findById(quizSurveyId)
                .orElseThrow(() -> new RuntimeException("Quiz survey not found"));
        SurveyDefinition definition = quizSurvey.getDefinitionJson();
        Map<String, Object> answerKey = quizSurvey.getAnswerKey();

        List<ResponseModel> responses = responseRepo.findByQuizSurveyId(quizSurveyId);

        return responses.stream()
                .collect(Collectors.groupingBy(ResponseModel::getUserId))
                .entrySet()
                .stream()
                .map(entry -> {
                    String userId = entry.getKey();
                    List<QuizResultDTO> attemptsDTO = entry.getValue().stream()
                            .map(resp -> mapQuizResponseToDTO(resp, definition, answerKey))
                            .toList();
                    return QuizResultAdminDTO.builder()
                            .id(userId)
                            .username(entry.getValue().get(0).getUsername())
                            .attempts(attemptsDTO)
                            .build();
                })
                .toList();
    }

    public QuizResultDTO getQuizResultByUserId(UUID quizSurveyId, String userId) {

        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Invalid userId"));
        QuizSurveyModel quizSurvey = quizSurveyRepo.findById(quizSurveyId)
                .orElseThrow(() -> new RuntimeException("Quiz survey not found"));

        if (!quizSurvey.getIsAnnounced()) {
            throw new IllegalStateException("Results are not announced yet.");
        }

        Map<String, Object> answerKey = quizSurvey.getAnswerKey();

        ResponseModel highestScoreResp = responseRepo
                .findByQuizSurveyIdAndUserId(quizSurveyId, userId)
                .stream()
                .max(Comparator.comparing(ResponseModel::getScore, Comparator.nullsLast(Integer::compareTo)))
                .orElseThrow(() -> new IllegalArgumentException("No responses found."));

        return mapQuizResponseToDTO(highestScoreResp, quizSurvey.getDefinitionJson(), answerKey);
    }

    private QuizResultDTO mapQuizResponseToDTO(ResponseModel resp, SurveyDefinition definition, Map<String, Object> answerKey) {
        Map<String, Object> selectedAnswers = resp.getAnswers();
        Map<String, QuizResultDTO.QuestionAnswerDTO> formattedAnswers = new LinkedHashMap<>();

        for (SurveyDefinition.Page page : definition.getPages()) {
            for (SurveyDefinition.Element q : page.getElements()) {
                String questionText = q.getTitle();
                String questionId = q.getName();

                List<QuizResultDTO.OptionDTO> options = Optional.ofNullable(q.getChoices())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(choice -> {
                            boolean correct = false;
                            Object ans = answerKey.get(questionId);
                            if (ans instanceof String s) correct = choice.equals(s);
                            else if (ans instanceof List<?> list) correct = list.contains(choice);
                            return QuizResultDTO.OptionDTO.builder().text(choice).isCorrect(correct).build();
                        }).toList();

                Object selected = selectedAnswers.get(questionId);
                Object selectedOpt = formatSelectedOptions(selected);

                formattedAnswers.put(questionText, QuizResultDTO.QuestionAnswerDTO.builder()
                        .choices(options)
                        .type(q.getType())
                        .correctAnswer(Objects.equals(q.getType(), "text") && q.getCorrectAnswer() != null
                                ? q.getCorrectAnswer().toString() : null)
                        .selectedOptions(selectedOpt)
                        .arabicTitle(q.getArabicTitle())
                        .mark(q.getMarks())
                        .build());
            }
        }

        return QuizResultDTO.builder()
                .id(resp.getId())
                .username(resp.getUsername())
                .score(resp.getScore())
                .maxScore(resp.getMaxScore())
                .submittedAt(resp.getSubmittedAt())
                .answers(formattedAnswers)
                .finishTime(resp.getFinishTimeMs())
                .build();
    }

    private Object formatSelectedOptions(Object selected) {
        if (selected instanceof List<?> list) {
            if (list.size() == 1) return list.get(0).toString();
            else return list.stream().map(String::valueOf).toList();
        } else return selected != null ? selected.toString() : null;
    }
}


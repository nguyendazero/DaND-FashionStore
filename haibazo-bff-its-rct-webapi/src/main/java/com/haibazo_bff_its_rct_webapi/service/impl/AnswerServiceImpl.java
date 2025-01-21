package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAnswerRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAnswerResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Answer;
import com.haibazo_bff_its_rct_webapi.model.Question;
import com.haibazo_bff_its_rct_webapi.model.User;
import com.haibazo_bff_its_rct_webapi.repository.AnswerRepository;
import com.haibazo_bff_its_rct_webapi.repository.QuestionRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.AnswerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctAnswerResponse> add(Long questionId, AddAnswerRequest request) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId.toString()));

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", "1"));

        Answer answer = new Answer();
        answer.setContent(request.getContent());
        answer.setQuestion(question);
        answer.setUser(user);

        Answer savedAnswer = answerRepository.save(answer);

        ItsRctAnswerResponse response = new ItsRctAnswerResponse(
                savedAnswer.getId(),
                savedAnswer.getContent(),
                savedAnswer.getQuestion().getId(),
                savedAnswer.getUser().getId(),
                savedAnswer.getCreatedAt(),
                savedAnswer.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctAnswerResponse> getById(Long id) {

        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id.toString()));

        ItsRctAnswerResponse response = new ItsRctAnswerResponse(
                answer.getId(),
                answer.getContent(),
                answer.getQuestion().getId(),
                answer.getUser().getId(),
                answer.getCreatedAt(),
                answer.getUpdatedAt()
        );
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctAnswerResponse>> getByQuestionId(Long questionId) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId.toString()));

        List<Answer> answers = answerRepository.findAllByQuestion(question);
        List<ItsRctAnswerResponse> answerResponses = answers.stream().map(answer -> new ItsRctAnswerResponse(
                answer.getId(),
                answer.getContent(),
                answer.getQuestion().getId(),
                answer.getUser().getId(),
                answer.getCreatedAt(),
                answer.getUpdatedAt()
        )).toList();
        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), answerResponses);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id) {

        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id.toString()));

        answerRepository.delete(answer);
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted answer with id = " + answer.getId());
    }
}

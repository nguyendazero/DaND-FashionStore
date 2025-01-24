package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddAnswerRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctAnswerResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ErrorPermissionException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.Answer;
import com.haibazo_bff_its_rct_webapi.model.Question;
import com.haibazo_bff_its_rct_webapi.repository.AnswerRepository;
import com.haibazo_bff_its_rct_webapi.repository.QuestionRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.service.AnswerService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
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
    private final TokenUtil tokenUtil;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctAnswerResponse> add(Long questionId, AddAnswerRequest request, String authorizationHeader) {
        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra quyền hạn
        if (userResponse == null || !userResponse.getRole().contains("ROLE_ADMIN")) {
            throw new ErrorPermissionException();
        }

        // Lấy câu hỏi
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId.toString()));

        // Tạo và lưu câu trả lời
        Answer answer = new Answer();
        answer.setContent(request.getContent());
        answer.setQuestion(question);
        answer.setUser(userRepository.findById(userResponse.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userResponse.getId().toString())));

        Answer savedAnswer = answerRepository.save(answer);

        // Tạo phản hồi
        ItsRctAnswerResponse response = new ItsRctAnswerResponse(
                savedAnswer.getId(),
                savedAnswer.getContent(),
                savedAnswer.getQuestion().getId(),
                savedAnswer.getUser().getId(),
                savedAnswer.getCreatedAt(),
                savedAnswer.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
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
    public APICustomize<String> delete(Long id, String authorizationHeader) {
        // Lấy JWT từ header và xác thực người dùng
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = (token != null)
                ? tokenUtil.getUserByHaibazoAccountId(tokenUtil.getHaibazoAccountIdFromToken(token))
                : null;

        // Kiểm tra quyền hạn
        if (userResponse == null || !userResponse.getRole().contains("ROLE_ADMIN")) {
            throw new ErrorPermissionException();
        }
        
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id.toString()));
        
        answerRepository.delete(answer);
        
        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(),
                "Successfully deleted answer with id = " + answer.getId());
    }
}

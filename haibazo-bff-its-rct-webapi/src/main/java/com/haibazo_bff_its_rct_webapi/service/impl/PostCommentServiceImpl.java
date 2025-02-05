package com.haibazo_bff_its_rct_webapi.service.impl;

import com.haibazo_bff_its_rct_webapi.dto.APICustomize;
import com.haibazo_bff_its_rct_webapi.dto.request.AddPostCommentRequest;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctPostCommentResponse;
import com.haibazo_bff_its_rct_webapi.dto.response.ItsRctUserResponse;
import com.haibazo_bff_its_rct_webapi.enums.ApiError;
import com.haibazo_bff_its_rct_webapi.exception.ErrorPermissionException;
import com.haibazo_bff_its_rct_webapi.exception.ResourceNotFoundException;
import com.haibazo_bff_its_rct_webapi.model.*;
import com.haibazo_bff_its_rct_webapi.repository.PostCommentRepository;
import com.haibazo_bff_its_rct_webapi.repository.PostRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserRepository;
import com.haibazo_bff_its_rct_webapi.repository.UserTempRepository;
import com.haibazo_bff_its_rct_webapi.service.PostCommentService;
import com.haibazo_bff_its_rct_webapi.utils.TokenUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final UserTempRepository userTempRepository;
    private final TokenUtil tokenUtil;

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostCommentResponse> add(Long postId, AddPostCommentRequest request, String authorizationHeader) {
        // Tìm bài viết theo postId
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));

        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = null;
        Long haibazoAccountId; // Khai báo biến haibazoAccountId

        if (token != null) {
            haibazoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
            userResponse = tokenUtil.getUserByHaibazoAccountId(haibazoAccountId);
        } else {
            haibazoAccountId = null;
        }

        // Tạo đối tượng PostComment
        PostComment postComment = new PostComment();
        postComment.setContent(request.getContent());
        postComment.setPost(post);

        // Nếu người dùng đã đăng nhập, sử dụng thông tin của họ
        if (userResponse != null) {
            User user = userRepository.findByHaibazoAccountId(haibazoAccountId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "haitibaAccountId", haibazoAccountId.toString()));
            postComment.setUser(user);
        } else {
            // Nếu chưa xác thực, tạo UserTemp
            UserTemp userTemp = new UserTemp();
            userTemp.setFullName(request.getFullName());
            userTemp.setEmail(request.getEmail());
            userTemp.setAvatar(null);
            userTempRepository.save(userTemp);

            postComment.setUserTemp(userTemp);
        }

        // Lưu comment vào cơ sở dữ liệu
        PostComment savedPostComment = postCommentRepository.save(postComment);

        // Tạo phản hồi
        ItsRctPostCommentResponse response = new ItsRctPostCommentResponse(
                savedPostComment.getId(),
                savedPostComment.getContent(),
                savedPostComment.getUser(),
                savedPostComment.getUserTemp(),
                savedPostComment.getPost().getId(),
                savedPostComment.getCreatedAt(),
                savedPostComment.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.CREATED.getCode(), ApiError.CREATED.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<ItsRctPostCommentResponse> postComment(Long id) {

        PostComment postComment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PostComment", "id", id.toString()));

        ItsRctPostCommentResponse response = new ItsRctPostCommentResponse(
                postComment.getId(),
                postComment.getContent(),
                postComment.getUser(),
                postComment.getUserTemp(),
                postComment.getPost().getId(),
                postComment.getCreatedAt(),
                postComment.getUpdatedAt()
        );

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<List<ItsRctPostCommentResponse>> getByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));

        List<PostComment> postComments = postCommentRepository.findAllByPost(post);
        List<ItsRctPostCommentResponse> response = postComments.stream()
                .map(postComment -> new ItsRctPostCommentResponse(
                        postComment.getId(),
                        postComment.getContent(),
                        postComment.getUser(),
                        postComment.getUserTemp(),
                        postComment.getPost().getId(),
                        postComment.getCreatedAt(),
                        postComment.getUpdatedAt()
                )).toList();

        return new APICustomize<>(ApiError.OK.getCode(), ApiError.OK.getMessage(), response);
    }

    @Override
    @CircuitBreaker(name = "haibazo-bff-its-rct-webapi")
    public APICustomize<String> delete(Long id, String authorizationHeader) {
        // Lấy JWT từ header
        String token = tokenUtil.extractToken(authorizationHeader);
        ItsRctUserResponse userResponse = null;

        if (token != null) {
            Long haibazoAccountId = tokenUtil.getHaibazoAccountIdFromToken(token);
            userResponse = tokenUtil.getUserByHaibazoAccountId(haibazoAccountId);
        }

        // Tìm comment theo id
        PostComment postComment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PostComment", "id", id.toString()));

        // Kiểm tra quyền hạn
        boolean isAdmin = userResponse != null && userResponse.getRole().contains("ROLE_ADMIN");
        boolean isOwner = userResponse != null && postComment.getUser() != null && postComment.getUser().getId().equals(userResponse.getId());

        if (!isAdmin && !isOwner) {
            throw new ErrorPermissionException();
        }

        // Xóa comment khỏi cơ sở dữ liệu
        postCommentRepository.delete(postComment);

        return new APICustomize<>(ApiError.NO_CONTENT.getCode(), ApiError.NO_CONTENT.getMessage(), "Successfully deleted post comment with id = " + postComment.getId());
    }
}

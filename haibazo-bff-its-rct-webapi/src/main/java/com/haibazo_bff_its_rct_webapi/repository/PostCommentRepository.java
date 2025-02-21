package com.haibazo_bff_its_rct_webapi.repository;

import com.haibazo_bff_its_rct_webapi.model.Post;
import com.haibazo_bff_its_rct_webapi.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findAllByPost(Post post);
}

package com.haibazo_bff_its_rct_webapi.repository;
import com.haibazo_bff_its_rct_webapi.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}

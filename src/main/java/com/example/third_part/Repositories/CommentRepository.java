package com.example.third_part.Repositories;

import com.example.third_part.Models.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
}


package com.springboot.blog.service;

import com.springboot.blog.dto.CommentDto;
import com.springboot.blog.entity.Comment;

import java.util.List;

public interface CommentService
{
    CommentDto createComment(long postId, CommentDto commentDto);

    List<CommentDto> commentsForPost(long postId);

    CommentDto singleCommentForPost(long postId, long commentId);

    CommentDto updateComment(long postId, long id, CommentDto commentDto);

    String deleteComment(long postId, long id);
}

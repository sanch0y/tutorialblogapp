package com.springboot.blog.service.impl;

import com.springboot.blog.dto.CommentDto;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogApiException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService
{
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto)
    {
        Post postOwnsComment = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        Comment commentToSave = dtoToEntity(commentDto);
        commentToSave.setPost(postOwnsComment);
        Comment savedComment = commentRepository.save(commentToSave);
        return entityToDto(savedComment);
    }

    @Override
    public List<CommentDto> commentsForPost(long postId) {
        Post postOwnsComments = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        List<Comment> commentsForPost = commentRepository.findByPostId(postId);

        List<CommentDto> commentDtosForPost = commentsForPost.stream()
                .map(eachComment -> entityToDto(eachComment)).collect(Collectors.toList());

        return commentDtosForPost;
    }

    @Override
    public CommentDto singleCommentForPost(long postId, long commentId) {
        Post postOwnsComment = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment commentUnderPost = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if(!commentUnderPost.getPost().getId().equals(postOwnsComment.getId()))
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Comment does not belong to this post");

        //Java 8 Style
        /*List<Comment> commentsForPost = commentRepository.findByPostId(postOwnsComment.getId());
        CommentDto commentUnderPostDto = commentsForPost.stream()
                .filter(eachComment -> eachComment.getId() == commentId)
                .map(filteredComment -> entityToDto(filteredComment))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        return commentUnderPostDto;*/
        //Java 8 Style

        return entityToDto(commentUnderPost);
    }

    @Override
    public CommentDto updateComment(long postId, long id, CommentDto commentDto)
    {
        Post postOwnsComment = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment commentToUpdate = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        if(!commentToUpdate.getPost().getId().equals(postOwnsComment.getId()))
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Comment does not belong to this post");

        commentToUpdate.setName(commentDto.getName());
        commentToUpdate.setEmail(commentDto.getEmail());
        commentToUpdate.setBody(commentDto.getBody());
        Comment updatedComment = commentRepository.save(commentToUpdate);

        return entityToDto(updatedComment);
    }

    @Override
    public String deleteComment(long postId, long id) {
        Post postOwnsComment = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment commentToDelete = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        if(!commentToDelete.getPost().getId().equals(postOwnsComment.getId()))
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Comment does not belong to this post");

        commentRepository.deleteById(id);

        return String.format("Comment with id %s, under the Post with id %s was deleted successfully", id, postId);
    }

    private Comment dtoToEntity(CommentDto dtoToConvert){
        Comment entityToReturn = modelMapper.map(dtoToConvert, Comment.class);

        /*entityToReturn.setName(dtoToConvert.getName());
        entityToReturn.setEmail(dtoToConvert.getEmail());
        entityToReturn.setBody(dtoToConvert.getBody());*/

        return entityToReturn;
    }

    private CommentDto entityToDto(Comment entityToConvert){

        CommentDto entityToReturn = modelMapper.map(entityToConvert, CommentDto.class);

        /*dtoToReturn.setId(entityToConvert.getId());
        dtoToReturn.setName(entityToConvert.getName());
        dtoToReturn.setEmail(entityToConvert.getEmail());
        dtoToReturn.setBody(entityToConvert.getBody());*/

        return entityToReturn;
    }
}

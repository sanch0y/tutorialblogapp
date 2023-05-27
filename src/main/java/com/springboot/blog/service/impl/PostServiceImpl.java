package com.springboot.blog.service.impl;

import com.springboot.blog.dto.PostDto;
import com.springboot.blog.dto.PostResponse;
import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.AuthService;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PostDto createPost(PostDto postDto)
    {
        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        Post post = dtoToEntity(postDto);
        post.setCategory(category);
        Post savedPost = postRepository.save(post);

        return entityToDto(savedPost);
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir)
    {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> pageablePosts = postRepository.findAll(pageable);
        List<Post> allPosts = pageablePosts.getContent();

        List<PostDto> content = allPosts.stream()
                .map(eachPost -> entityToDto(eachPost)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse(content,
                pageablePosts.getNumber(),
                pageablePosts.getSize(),
                pageablePosts.getTotalElements(),
                pageablePosts.getTotalPages(),
                pageablePosts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return entityToDto(post);
    }

    @Override
    public PostDto updatePost(long id, PostDto postDto) {

        Post postToUpdate = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        postToUpdate.setTitle(postDto.getTitle());
        postToUpdate.setContent(postDto.getContent());
        postToUpdate.setDescription(postDto.getDescription());
        postToUpdate.setCategory(category);

        Post updatedPost = postRepository.save(postToUpdate);

        return entityToDto(updatedPost);
    }

    @Override
    public String deletePost(long id) {
        postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", id));
        postRepository.deleteById(id);

        return String.format("Post with id %s was deleted successfully", id);
    }

    @Override
    public List<PostDto> getPOstByCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));

        List<Post> posts = postRepository.findByCategoryId(categoryId);

        return posts.stream().map(eachPost ->  entityToDto(eachPost)).collect(Collectors.toList());
    }


    private Post dtoToEntity(PostDto dtoToConvert){

        Post entityToReturn = modelMapper.map(dtoToConvert, Post.class);

        /*entityToReturn.setTitle(dtoToConvert.getTitle());
        entityToReturn.setDescription(dtoToConvert.getDescription());
        entityToReturn.setContent(dtoToConvert.getContent());*/

        return entityToReturn;
    }

    private PostDto entityToDto(Post entityToConvert){

        PostDto dtoToReturn = modelMapper.map(entityToConvert, PostDto.class);

        /*dtoToReturn.setId(entityToConvert.getId());
        dtoToReturn.setTitle(entityToConvert.getTitle());
        dtoToReturn.setDescription(entityToConvert.getDescription());
        dtoToReturn.setContent(entityToConvert.getContent());*/

        return dtoToReturn;
    }
}

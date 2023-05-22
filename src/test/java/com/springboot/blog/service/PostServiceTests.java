package com.springboot.blog.service;

import com.springboot.blog.dto.PostDto;
import com.springboot.blog.dto.PostResponse;
import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Page<Post> page;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    private PostDto requestDto;

    private PostDto responseDto;

    private Category category;

    @BeforeEach
    public void setPostWIthCategory()
    {
        category = Category.builder()
                .id(1)
                .name("category1")
                .description("description1")
                .build();

        requestDto = PostDto.builder()
                .title("PostTitle1")
                .description("PostDescription1")
                .content("PostContent1")
                .categoryId(category.getId())
                .build();

        post = Post.builder()
                .id(1L)
                .title("PostTitle1")
                .description("PostDescription1")
                .content("PostContent1")
                .category(category)
                .build();

        responseDto = PostDto.builder()
                .id(1)
                .title("PostTitle1")
                .description("PostDescription1")
                .content("PostContent1")
                .categoryId(post.getCategory().getId())
                .build();
    }

    @DisplayName("Business_Logic_01")
    @Test //1//BDD//Positive
    public void givenPostDtoObjectAndPostObject_whenSavePost_thenReturnPostDtoObject()
    {
        //given - precondition or setup
        given(categoryRepository.findById(category.getId()))
                .willReturn(Optional.of(category));

        given(modelMapper.map(requestDto, Post.class))
                .willReturn(post);

        given(modelMapper.map(post, PostDto.class))
                .willReturn(responseDto);

        given(postRepository.save(post))
                .willReturn(post);

        //when - action or behaviour
        PostDto savedPostDto = postService.createPost(requestDto);

        //then - verify the output
        assertThat(savedPostDto).isNotNull();
        assertEquals(responseDto, savedPostDto);
    }

    @DisplayName("Business_Logic_02")
    @Test //2//BDD//Negative
    public void givenPostDtoObjectAndPostObject_whenSavePost_thenThrowsException()
    {
        //given - precondition or setup
        given(categoryRepository.findById(category.getId()))
                .willReturn(Optional.ofNullable(null));

        //when - action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> {
            postService.createPost(requestDto);
        });

        //then - verify the output
        verify(modelMapper, never()).map(any(PostDto.class), any(Post.class));
        verify(modelMapper, never()).map(any(Post.class), any(PostDto.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @DisplayName("Business_Logic_03")
    @Test //3//BDD//Positive
    public void givenPostDtoList_whenGetAllPosts_thenReturnPostDtoLists()
    {
        //given - precondition or setup
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";
        int totalElements = 3;
        int totalPages = 1;
        boolean lastPage = true;

        List<Post> expectedPosts = makePostList(totalElements);

        List<PostDto> expectedPostDtos = makePostDtoList(totalElements);

        PostResponse expectedResponse = PostResponse.builder()
                .content(expectedPostDtos)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElement(totalElements)
                .totalPage(totalPages)
                .last(lastPage)
                .build();

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());

        /*Start Stubbing for Repository Business Logic Start*/
        given(postRepository.findAll(pageable))
                .willReturn(page);
        /*End Stubbing for Repository Business Logic End*/

        /*Start Stubbing for Page Start*/
        given(page.getContent())
                .willReturn(expectedPosts);
        given(page.getNumber())
                .willReturn(pageNo);
        given(page.getSize())
                .willReturn(pageSize);
        given(page.getTotalElements())
                .willReturn(Long.valueOf(totalElements));
        given(page.getTotalPages())
                .willReturn(totalPages);
        given(page.isLast())
                .willReturn(lastPage);
        /*End Stubbing for Page End*/

        /*Start Stubbing for EntityToDto Start*/
        given(modelMapper.map(any(Post.class), any()))
                .willReturn(expectedPostDtos.get(0), expectedPostDtos.get(1), expectedPostDtos.get(2));
        /*End Stubbing for EntityToDto End*/

        //when - action or behaviour
        PostResponse actualResponse = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        //then - verify the output
        assertEquals(expectedResponse, actualResponse);
        assertEquals(actualResponse.getContent().size(), totalElements);
        verify(postRepository, times(1)).findAll(pageable);
    }

    @DisplayName("Business_Logic_04")
    @Test //4//BDD//Negative
    public void givenPostDtoList_whenGetAllPosts_thenReturnEmptyLists()
    {
        //given - precondition or setup
        int pageNo = 0;
        int pageSize = 1;
        String sortBy = "id";
        String sortDir = "asc";
        int totalElements = 0;
        int totalPages = 1;
        boolean lastPage = true;

        List<Post> expectedPosts = makePostList(totalElements);

        List<PostDto> expectedPostDtos = makePostDtoList(totalElements);

        PostResponse expectedResponse = PostResponse.builder()
                .content(expectedPostDtos)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElement(totalElements)
                .totalPage(totalPages)
                .last(lastPage)
                .build();

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());

        /*Start Stubbing for Repository Business Logic Start*/
        given(postRepository.findAll(pageable))
                .willReturn(page);
        /*End Stubbing for Repository Business Logic End*/

        /*Start Stubbing for Page Start*/
        given(page.getContent())
                .willReturn(expectedPosts);
        given(page.getNumber())
                .willReturn(pageNo);
        given(page.getSize())
                .willReturn(pageSize);
        given(page.getTotalElements())
                .willReturn(Long.valueOf(totalElements));
        given(page.getTotalPages())
                .willReturn(totalPages);
        given(page.isLast())
                .willReturn(lastPage);
        /*End Stubbing for Page End*/

        /*Start Stubbing for EntityToDto Start (We don't need this in Negative scenario)*/
//        BDDMockito.given(modelMapper.map(any(Post.class), any()))
//                .willReturn(expectedPostDtos);
        /*End Stubbing for EntityToDto End*/

        //when - action or behaviour
        PostResponse actualResponse = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        //then - verify the output
        assertEquals(expectedResponse, actualResponse);
        assertEquals(actualResponse.getContent().size(), totalElements);
        verify(postRepository, times(1)).findAll(pageable);
    }

    @DisplayName("Business_Logic_05")
    @Test //5//BDD//Positive
    public void givenPostIdAndPostObject_whenFindById_thenReturnSpecificPostDtoObject()
    {
        //given - precondition or setup
//        BDDMockito.given(categoryRepository.findById(category.getId()))  //(We don't need this in Find by id positive scenario)
//                .willReturn(Optional.of(category));

        given(modelMapper.map(post, PostDto.class))
                .willReturn(responseDto);

        given(postRepository.findById(1L))
                .willReturn(Optional.of(post));

        //when - action or behaviour
        PostDto actualPostDto = postService.getPostById(1L);

        //then - verify the output
        assertThat(actualPostDto).isNotNull();
        assertEquals(responseDto, actualPostDto);
    }

    @DisplayName("Business_Logic_06")
    @Test //6//BDD//Negative
    public void givenPostIdAndPostObject_whenFindById_thenReturnsException()
    {
        //given - precondition or setup
//        BDDMockito.given(categoryRepository.findById(category.getId()))  //(We don't need this in Find by id negative scenario)
//                .willReturn(Optional.of(category));

//        BDDMockito.given(modelMapper.map(post, PostDto.class))   //(We don't need this in Find by id negative scenario)
//                .willReturn(responseDto);

        given(postRepository.findById(2L))
                .willReturn(Optional.empty());

        //when - action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> {
            postService.getPostById(2L);
        });

        //then - verify the output
        verify(modelMapper, never()).map(any(Post.class), any(PostDto.class));
    }

    @DisplayName("Business_Logic_07")
    @Test //7//BDD//Positive
    public void givenPostDtoObjectAndPostId_whenUpdatePost_thenReturnPostDtoObject()
    {
        Category updatedCategory = Category.builder()  //Updated category
                .id(2)
                .name("updatedCategory2")
                .description("updatedCategoryDescription2")
                .build();

        requestDto = PostDto.builder()
                .title("updatedPostTitle")
                .description("updatedPostDescription")
                .content("updatedPostContent")
                .categoryId(updatedCategory.getId())
                .build();

        responseDto = PostDto.builder()
                .id(1L)
                .title("updatedPostTitle")
                .description("updatedPostDescription")
                .content("updatedPostContent")
                .categoryId(updatedCategory.getId())
                .build();

        Post updatedPost = Post.builder()
                .id(1L)
                .title("updatedPostTitle")
                .description("updatedPostDescription")
                .content("updatedPostContent")
                .category(updatedCategory)
                .build();

        //given - precondition or setup
        given(postRepository.findById(1L))
                .willReturn(Optional.of(post));

        given(categoryRepository.findById(requestDto.getCategoryId()))  //Updated one
                .willReturn(Optional.of(updatedCategory));

        given(modelMapper.map(updatedPost, PostDto.class))
                .willReturn(responseDto);

        given(postRepository.save(any(Post.class)))
                .willReturn(updatedPost);  //If I write like "BDDMockito.given(postRepository.save(updatedPost)).willReturn(updatedPost);" it will give me error.
                                           // If you directly pass the specific updatedPost instance without using a matcher, Mockito will try to match the exact
                                           // argument value during verification, which may not match the actual argument value used during the method call in
                                           // PostServiceImpl class. That's why it will give an error.

        //when - action or behaviour
        PostDto actualPostDto = postService.updatePost(1L, requestDto);

        //then - verify the output
        assertThat(actualPostDto).isNotNull();
        assertEquals(responseDto, actualPostDto);
    }

    @DisplayName("Business_Logic_08")
    @Test //8//BDD//Negative
    public void givenPostDtoObjectAndPostId_whenUpdatePost_thenReturnPostException()
    {
        //given - precondition or setup
        given(postRepository.findById(2L)).willReturn(Optional.empty());

        //when - action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(2L, requestDto);
        });

        //then - verify the output
        verify(categoryRepository, never()).findById(any(Long.class));
        verify(postRepository, never()).save(any(Post.class));
        verify(modelMapper, never()).map(any(Post.class), any(PostDto.class));
    }

    @DisplayName("Business_Logic_09")
    @Test //9//BDD//Negative
    public void givenPostDtoObjectAndPostId_whenUpdatePost_thenReturnCategoryException()
    {
        Category updatedCategory = Category.builder()  //Updated category
                .id(2)
                .name("updatedCategory2")
                .description("updatedCategoryDescription2")
                .build();

        requestDto = PostDto.builder()
                .title("updatedPostTitle")
                .description("updatedPostDescription")
                .content("updatedPostContent")
                .categoryId(updatedCategory.getId())
                .build();

        //given - precondition or setup
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        given(categoryRepository.findById(requestDto.getCategoryId()))  //Need Updated one
                .willReturn(Optional.empty());

        //when - action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> {
            postService.updatePost(1L, requestDto);
        });

        //then - verify the output
        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
        verify(modelMapper, never()).map(any(Post.class), any(PostDto.class));
    }

    @DisplayName("Business_Logic_10")
    @Test //10//BDD//Positive
    public void givenPostId_whenDeleteById_thenReturnDeletedConfirmationMessageAndDeletesInternally()
    {
        given(postRepository.findById(1L))
                .willReturn(Optional.of(post));

        willDoNothing().given(postRepository).deleteById(1L);

        //when - action or behaviour
        String actualDeletedMessage = postService.deletePost(1L);

        //then - verify the output
        verify(postRepository).findById(1L);
        verify(postRepository, times(1)).deleteById(1L);
        assertEquals(String.format("Post with id %s was deleted successfully", 1L), actualDeletedMessage);
    }

    @DisplayName("Business_Logic_11")
    @Test //11//BDD//Negative
    public void givenPostId_whenDeleteById_thenReturnException()
    {
        given(postRepository.findById(2L))
                .willReturn(Optional.empty());

        //when - action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> {
            postService.deletePost(2L);
        });

        //then - verify the output
        verify(postRepository, never()).deleteById(2L);
    }



    /*Utility Methods Onwards*/
    private List<Post> makePostList(int instances)
    {
        List<Post> posts = new ArrayList<>();

        for(int i = 1; i <= instances; i++)
        {
            Post post = Post.builder()
                    .id(Long.valueOf(i))
                    .title("PostTitle"+i)
                    .description("PostDescription"+i)
                    .content("PostContent"+i)
                    .category(category)
                    .build();

            posts.add(post);
        }

        return posts;
    }

    private List<PostDto> makePostDtoList(int instances)
    {
        List<PostDto> postDtos = new ArrayList<>();

        for(int i = 1; i <= instances; i++)
        {
            PostDto postDto = PostDto.builder()
                    .id(i)
                    .title("PostTitle"+i)
                    .description("PostDescription"+i)
                    .content("PostContent"+i)
                    .categoryId(category.getId())
                    .build();

            postDtos.add(postDto);
        }

        return postDtos;
    }



}

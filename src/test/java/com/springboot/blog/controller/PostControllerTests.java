package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.dto.PostDto;
import com.springboot.blog.dto.PostResponse;
import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.security.JwtAuthenticationFilter;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.PostService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.*;

import static org.mockito.BDDMockito.given;

@AutoConfigureMockMvc(addFilters = false) //This will disable the security of Jwt Token & CSRF token
@WebMvcTest(controllers = PostController.class) //Only load the required controller. Avoid loading the service and repository controller.
public class PostControllerTests
{
    @Autowired
    private MockMvc mockMvc; //To call REST APIs.

    @MockBean  //Tells Spring to create this Mock instance of PostService and add it to the application context so that it is injected into EmployeeController.
    private PostService postService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

//    @MockBean
//    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Post postEntity;

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

        postEntity = Post.builder()
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
                .categoryId(postEntity.getCategory().getId())
                .build();
    }

    @DisplayName("Controller_Logic_01")
    @Test //1//BDD//Positive//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithTestJwtToken_whenSavePost_thenReturnPostObject() throws Exception
    {
//        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
//                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        /*Don't uncomment Start*/
        //.willAnswer(...): This is a method that specifies the behavior that should occur when the mocked method is
        // called with the specified arguments.
        //
        //invocationOnMock -> invocationOnMock.getArgument(0): This is a lambda expression that defines the behavior to
        // be executed when the mocked method is called. It retrieves the first argument (0 index) passed to the method
        // call and returns it as the result.
        /*Don't uncomment End*/

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) responseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(responseDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(responseDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(responseDto.getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId", CoreMatchers.is((int) responseDto.getCategoryId())));
    }

    @DisplayName("Controller_Logic_02")
    @Test //2//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithTitleSizeLessThanMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle("P");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"Post title must have at least 4 characters\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_03")
    @Test //3//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithEmptyTitle_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle("");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_04")
    @Test //4//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithNullTitle_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle(null);

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }


    @DisplayName("Controller_Logic_05")
    @Test //5//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithDescriptionSizeLessThanMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription("D");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"Post description must have at least 10 characters\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_06")
    @Test //6//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithEmptyDescription_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription("");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_07")
    @Test //7//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithNullDescription_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription(null);

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_08")
    @Test //8//BDD//Positive//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithContentSizeMostMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent("C");
        responseDto.setContent("C");

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) responseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(responseDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(responseDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(responseDto.getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId", CoreMatchers.is((int) responseDto.getCategoryId())));
    }

    @DisplayName("Controller_Logic_09")
    @Test //9//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithEmptyContent_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent("");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"content\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_10")
    @Test //10//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithNullContent_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent(null);

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"content\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }


    @Disabled("This test case is ignored for now.")
    @DisplayName("Controller_Logic_11")
    @Test //11//BDD//Negative//This test case will work when commenting the line "@AutoConfigureMockMvc(addFilters = false)" on the top of the class
    public void givenPostDtoObjectAndNoJwtToken_whenSavePost_thenReturn403Forbidden() throws Exception
    {
        //given - precondition or setup
        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("Controller_Logic_12")
    @Test //12//BDD//Positive//This test case will work when remove commenting for the line "@AutoConfigureMockMvc(addFilters = false)" on the top of the class
    public void givenPostDtoList_whenGetAllPosts_thenReturnListOfPostObjects() throws Exception
    {
        //given - precondition or setup
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";
        int totalElements = 3;
        int totalPages = 1;
        boolean lastPage = true;

        List<PostDto> expectedPostDtos = makePostDtoList(totalElements);

        PostResponse expectedResponse = PostResponse.builder()
                .content(expectedPostDtos)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElement(totalElements)
                .totalPage(totalPages)
                .last(lastPage)
                .build();

        String jwtToken = "Bearer " + "Test Token";

        //given - precondition or setup
        given(postService.getAllPosts(pageNo, pageSize, sortBy, sortDir))
                .willReturn(expectedResponse);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/v1/posts")
                .header("Authorization", jwtToken)
                .param("pageNo", String.valueOf(pageNo))
                .param("pageSize", String.valueOf(pageSize))
                .param("sortBy", sortBy)
                .param("sortDir", sortDir)
                .contentType(MediaType.APPLICATION_JSON));

        //then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(expectedResponse.getContent().size())))

                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", CoreMatchers.is((int) expectedResponse.getContent().get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title", CoreMatchers.is(expectedResponse.getContent().get(0).getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].description", CoreMatchers.is(expectedResponse.getContent().get(0).getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].content", CoreMatchers.is(expectedResponse.getContent().get(0).getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].categoryId", CoreMatchers.is((int) expectedResponse.getContent().get(0).getCategoryId())))

                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].id", CoreMatchers.is((int) expectedResponse.getContent().get(2).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].title", CoreMatchers.is(expectedResponse.getContent().get(2).getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].description", CoreMatchers.is(expectedResponse.getContent().get(2).getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].content", CoreMatchers.is(expectedResponse.getContent().get(2).getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].categoryId", CoreMatchers.is((int) expectedResponse.getContent().get(2).getCategoryId())))

                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo", CoreMatchers.is(expectedResponse.getPageNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize", CoreMatchers.is(expectedResponse.getPageSize())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElement", CoreMatchers.is((int) expectedResponse.getTotalElement())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPage", CoreMatchers.is(expectedResponse.getTotalPage())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last", CoreMatchers.is(expectedResponse.isLast())));
    }

    @DisplayName("Controller_Logic_13")
    @Test //13//BDD//Negative//This test case will work when uncommenting the line "@AutoConfigureMockMvc(addFilters = false)" on the top of the class
    public void givenPostDtoList_whenGetAllPosts_thenReturnEmptyList() throws Exception
    {
        //given - precondition or setup
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";
        int totalElements = 0;
        int totalPages = 0;
        boolean lastPage = true;

        List<PostDto> expectedPostDtos = makePostDtoList(totalElements);

        PostResponse expectedResponse = PostResponse.builder()
                .content(expectedPostDtos)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElement(totalElements)
                .totalPage(totalPages)
                .last(lastPage)
                .build();

        String jwtToken = "Bearer " + "Test Token";

        //given - precondition or setup
        given(postService.getAllPosts(pageNo, pageSize, sortBy, sortDir))
                .willReturn(expectedResponse);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/v1/posts")
                .header("Authorization", jwtToken)
                .param("pageNo", String.valueOf(pageNo))
                .param("pageSize", String.valueOf(pageSize))
                .param("sortBy", sortBy)
                .param("sortDir", sortDir)
                .contentType(MediaType.APPLICATION_JSON));

        //then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().equals(""));
    }

    @DisplayName("Controller_Logic_14")
    @Test //14//BDD//Positive//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostIdAndPostDtoObject_whenGetPostById_thenReturnSpecificPost() throws Exception
    {
        long postId = 1;

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.getPostById(postId))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/posts/{postId}", postId)
                .header("Authorization", jwtToken)
                .header("X-API-VERSION", "1"));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) responseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(responseDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(responseDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(responseDto.getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId", CoreMatchers.is((int) responseDto.getCategoryId())));
    }

    @DisplayName("Controller_Logic_15")
    @Test //15//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostIdAndPostDtoObject_whenGetPostById_thenReturnNotFoundError() throws Exception
    {
        long postId = 2;

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.getPostById(postId))
                .willThrow(new ResourceNotFoundException("Post", "Id", postId));

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/posts/{postId}", postId)
                .header("Authorization", jwtToken)
                .header("X-API-VERSION", "1")
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Post not found with Id : '"+postId+"'")));
    }

    @DisplayName("Controller_Logic_16")
    @Test //16//BDD//Positive//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostIdAndPostDtoObject_whenUpdatePost_thenReturnUpdatedPost() throws Exception
    {
        long postId = 1;

        category = Category.builder()
                .id(2)
                .name("category2")
                .description("description2")
                .build();

        requestDto = PostDto.builder()
                .title("UpdatedPostTitle1")
                .description("UpdatedPostDescription1")
                .content("UpdatedPostContent1")
                .categoryId(category.getId())
                .build();

        responseDto = PostDto.builder()
                .id(1)
                .title("UpdatedPostTitle1")
                .description("UpdatedPostDescription1")
                .content("UpdatedPostContent1")
                .categoryId(category.getId())
                .build();

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.updatePost(postId, requestDto))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) postId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(responseDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(responseDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(responseDto.getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId", CoreMatchers.is((int) responseDto.getCategoryId())));
    }

    @DisplayName("Controller_Logic_17")
    @Test //17//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostIdAndPostDtoObject_whenUpdatePost_thenReturnNotFoundError() throws Exception
    {
        long postId = 2;

        category = Category.builder()
                .id(2)
                .name("category2")
                .description("description2")
                .build();

        requestDto = PostDto.builder()
                .title("UpdatedPostTitle1")
                .description("UpdatedPostDescription1")
                .content("UpdatedPostContent1")
                .categoryId(category.getId())
                .build();

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.updatePost(postId, requestDto))
                .willThrow(new ResourceNotFoundException("Post", "Id", postId));

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Post not found with Id : '"+postId+"'")));
    }

    @DisplayName("Controller_Logic_18")
    @Test //18//BDD//Positive//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostId_whenDeletePostById_thenReturnDeletedMessage() throws Exception
    {
        long postId = 1;
        String dltConfirmationMsg = String.format("Post with id %s was deleted successfully", postId);

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.deletePost(postId))
                .willReturn(dltConfirmationMsg);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                .header("Authorization", jwtToken));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().equals(dltConfirmationMsg));
    }

    @DisplayName("Controller_Logic_19")
    @Test //19//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostId_whenDeletePostById_thenReturnNotFoundError() throws Exception
    {
        long postId = 1;
        String dltConfirmationMsg = String.format("Post with id %s was deleted successfully", postId);

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.deletePost(postId))
                .willThrow(new ResourceNotFoundException("Post", "Id", postId));

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                .header("Authorization", jwtToken));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Post not found with Id : '"+postId+"'")));
    }

    /*Utility Methods Onwards*/
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

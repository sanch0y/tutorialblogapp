package com.springboot.blog.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.dto.LoginDto;
import com.springboot.blog.dto.PostDto;
import com.springboot.blog.dto.PostResponse;
import com.springboot.blog.dto.RegisterDto;
import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.repository.helper.RepositoryTruncation;
import com.springboot.blog.service.AuthService;
import com.springboot.blog.service.impl.AuthServiceImpl;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Autowired
    private RepositoryTruncation repositoryTruncation;

    private Post postEntity;

    private PostDto requestDto;

    private PostDto responseDto;

    private RegisterDto registerAdminDto;

    private RegisterDto registerDto;

    private LoginDto loginDto;

    private Category category;

    @BeforeEach
    void setup()
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

        registerDto = RegisterDto.builder()
                .name("Tamanna Tabassum")
                .username("orthi")
                .email("orthi@gmail.com")
                .password("orthi123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("orthi@gmail.com")
                .password("orthi123")
                .build();

        userRepository.deleteAll();
        repositoryTruncation.truncateTable("categories");
        repositoryTruncation.truncateTable("posts");
    }


    @DisplayName("Integration_Test_01")
    @Test
    //1//BDD//Positive//
    public void givenPostDtoObjectWithCategoryAndJwtToken_whenSavePost_thenReturnPostObject() throws Exception
    {
        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

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

    @DisplayName("Integration_Test_02")
    @Test
    //2//BDD//Negative//
    public void givenPostDtoObjectWithCategoryAndJwtTokenAndNoAdminAccess_whenSavePost_thenReturnUnauthorizedError() throws Exception
    {
        authService.register(registerDto);

        String jwtToken = "Bearer " + authService.login(loginDto);

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Access Denied")));
    }

    @DisplayName("Integration_Test_03")
    @Test
    //3//BDD//Negative//
    public void givenPostDtoObjectWithCategoryAndWrongJwtToken_whenSavePost_thenReturnPostObject() throws Exception
    {
        String jwtToken = "Bearer " + "Wrong Token";

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("An Authentication object was not found in the SecurityContext")));
    }

    @DisplayName("Controller_Logic_04")
    @Test //4//BDD//Negative//
    public void givenPostDtoObjectWithTitleSizeLessThanMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle("P");

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"Post title must have at least 4 characters\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_05")
    @Test //5//BDD//Negative//
    public void givenPostDtoObjectWithEmptyTitle_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle("");

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"must not be empty\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_06")
    @Test //6//BDD//Negative//
    public void givenPostDtoObjectWithNullTitle_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle(null);

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"must not be empty\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }


    @DisplayName("Controller_Logic_07")
    @Test //7//BDD//Negative//
    public void givenPostDtoObjectWithDescriptionSizeLessThanMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription("D");

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"Post description must have at least 10 characters\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_08")
    @Test //8//BDD//Negative//
    public void givenPostDtoObjectWithEmptyDescription_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription("");

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"must not be empty\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_09")
    @Test //9//BDD//Negative//
    public void givenPostDtoObjectWithNullDescription_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription(null);

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"must not be empty\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_10")
    @Test //10//BDD//Positive//
    public void givenPostDtoObjectWithContentSizeMostMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent("C");
        responseDto.setContent("C");

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

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

    @DisplayName("Controller_Logic_11")
    @Test //9//BDD//Negative//
    public void givenPostDtoObjectWithEmptyContent_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent("");

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"content\":\"must not be empty\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_12")
    @Test //12//BDD//Negative//
    public void givenPostDtoObjectWithNullContent_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent(null);

        /*Validation error message Start*/
        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"content\":\"must not be empty\"}";
        /*Validation error message End*/

        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }


    @DisplayName("Integration_Test_13")
    @Test
    //13//BDD//Negative//
    public void givenPostDtoObjectWithJwtTokenAndWrongCategoryId_whenSavePost_thenReturnNotFoundError() throws Exception
    {
        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        /*Don't save category*/

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Category not found with id : '"+requestDto.getCategoryId()+"'")));
    }

    @DisplayName("Controller_Logic_14")
    @Test //14//BDD//Positive//
    public void givenPostDtoList_whenGetAllPosts_thenReturnListOfPostObjects() throws Exception
    {
        /*Authentication Preprocedure Start*/
        authServiceImpl.registerAdmin(registerDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";
        int totalElements = 3;
        int totalPages = 1;
        boolean lastPage = true;

        //Save category firstly
        categoryRepository.save(category);
        //Save posts secondly
        List<Post> postsToSave = makePostList(totalElements);
        postRepository.saveAll(postsToSave);

        /*Mocking for verifying results Start*/
        List<PostDto> expectedPostDtos = makePostDtoList(totalElements);

        PostResponse expectedResponse = PostResponse.builder()
                .content(expectedPostDtos)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElement(totalElements)
                .totalPage(totalPages)
                .last(lastPage)
                .build();
        /*Mocking for verifying results End*/

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

    @DisplayName("Controller_Logic_15")
    @Test //15//BDD//Negative//
    public void givenPostDtoList_whenGetAllPosts_thenReturnEmptyList() throws Exception
    {
        /*Authentication Preprocedure Start*/
        authServiceImpl.registerAdmin(registerDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String sortDir = "asc";
        int totalElements = 0;
        int totalPages = 0;
        boolean lastPage = true;

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(Collections.EMPTY_LIST)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize", CoreMatchers.is(pageSize)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElement", CoreMatchers.is(totalElements)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPage", CoreMatchers.is(totalPages)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last", CoreMatchers.is(lastPage)));
    }


    /*Utility Methods Onwards*/
    private List<Post> makePostList(int instances)
    {
        List<com.springboot.blog.entity.Post> posts = new ArrayList<>();

        for(int i = 1; i <= instances; i++)
        {
            com.springboot.blog.entity.Post post = com.springboot.blog.entity.Post.builder()
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

package com.springboot.blog.repository.Integration;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest //This annotation automatically configure in memory database.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //Required for Integration Test. This will replace H2 DB & use actual DB
public class PostRepositoryIntegrationTests
{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    private Post post;

    private Category category;

    @BeforeEach
    public void setPost()
    {
        category = insertOrGetCategory();
        post = Post.builder()
                .title("PostTitle1")
                .description("PostDescription1")
                .content("PostContent11")
                .category(category)
                .build();
    }

    @DisplayName("Repository_01")
    @Test //1//BDD
    public void givenPostObject_whenSave_thenReturnSavedPost()
    {
        //given & when - action or behaviour
        Post savedPost = postRepository.save(post);

        //then - verify the output
        Post verifyPost = postRepository.findById(savedPost.getId()).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", post.getId()));

        assertNotNull(verifyPost);
        assertNotNull(verifyPost.getCategory());
        assertEquals(savedPost.getId(), verifyPost.getId());
        assertThat(savedPost.getId()).isGreaterThan(0);
    }

    @DisplayName("Repository_02")
    @Test //2//BDD
    public void givenPostList_whenFindAllWithoutPagination_thenReturnListOfPost()
    {
        //given - precondition or setup
        List<Post> expectedEntities = createPosts(15);
        postRepository.saveAll(expectedEntities);

        //when - action or behaviour
        List<Post> savedPosts = postRepository.findAll();


        //then - verify the output
        assertThat(savedPosts).containsExactlyElementsOf(expectedEntities);
        assertEquals(15, savedPosts.size());

    }

    @DisplayName("Repository_03")
    @Test //3//BDD//Positive
    public void givenPostId_whenFindById_thenReturnSpecificPost()
    {
        //given - precondition or setup
        Post savedPost = postRepository.save(post);

        //when - action or behaviour
        Optional<Post> specificPostById = postRepository.findById(savedPost.getId());

        //then - verify the output
        assertThat(specificPostById).isPresent();
        assertEquals(savedPost.getId(), specificPostById.get().getId());
        assertEquals(savedPost, specificPostById.get());
    }

    @DisplayName("Repository_04")
    @Test //4//BDD
    public void givenPostObjectAndId_whenUpdatePost_thenReturnUpdatedPost()
    {
        //given - precondition or setup
        Post savedPost = postRepository.save(post);

        //when - action or behaviour
        Post beforeUpdate = new Post();
        beforeUpdate.setTitle(savedPost.getTitle());
        beforeUpdate.setDescription(savedPost.getDescription());
        beforeUpdate.setContent(savedPost.getContent());

        Post postToUpdate = postRepository.findById(savedPost.getId()).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", savedPost.getId()));
        postToUpdate.setTitle("UpdatedPostTitle");
        postToUpdate.setDescription("UpdatedPostDescription");
        postToUpdate.setContent("UpdatedPostContent");

        Post updatedPost = postRepository.save(postToUpdate);

        //then - verify the output
        assertThat(updatedPost).isNotNull();
        assertEquals(postToUpdate.getId(), updatedPost.getId());
        assertEquals(postToUpdate.getTitle(), updatedPost.getTitle());
        assertEquals(postToUpdate.getDescription(), updatedPost.getDescription());
        assertEquals(postToUpdate.getContent(), updatedPost.getContent());

        assertNotEquals(beforeUpdate.getTitle(), updatedPost.getTitle());
        assertNotEquals(beforeUpdate.getDescription(), updatedPost.getDescription());
        assertNotEquals(beforeUpdate.getContent(), updatedPost.getContent());
    }

    @DisplayName("Repository_05")
    @Test //5//BDD
    public void givenPostObject_whenDeletePost_thenReturnNull()
    {
        //given - precondition or setup
        Post savedPost = postRepository.save(post);

        //when - action or behaviour
        postRepository.delete(savedPost);
        Post deletedPost = postRepository.findById(savedPost.getId()).orElseGet(() -> null);

        //then - verify the output
        assertThat(deletedPost).isNull();
    }


    /*Utility Methods Onwards*/
    private Category insertOrGetCategory()
    {
        Category category = Category.builder()
                .name("category1")
                .description("description1")
                .build();

        Category findCategory = categoryRepository.findFirstByOrderById().orElseGet(() -> null);

        return findCategory != null ? findCategory : categoryRepository.save(category);
    }

    private List<Post> createPosts(int instances)
    {
        List<Post> posts = new ArrayList<>();

        for(int i = 1; i <= instances; i++)
        {
           Post post = Post.builder()
                    .title("PostTitle"+i)
                    .description("PostDescription"+i)
                    .content("PostContent1"+i)
                    .category(category)
                    .build();

            posts.add(post);
        }

        return posts;
    }



}

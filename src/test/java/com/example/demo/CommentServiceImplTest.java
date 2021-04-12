package com.example.demo;
import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UnapprovedCommentRepository;
import com.example.demo.service.CommentServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Calendar;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UnapprovedCommentRepository unapprovedCommentRepository;

    private CommentServiceImpl commentService;

    private Optional<Comment> testComment;

    @Before
    public void init() {
        commentService = new CommentServiceImpl(commentRepository,
                                                unapprovedCommentRepository);
        testComment = Optional.of(new Comment("Message", 4,
                                      true, new Post(), new User()));
        when(commentRepository.findById(1)).thenReturn(testComment);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getCommentTest() {
        Comment room = commentService.getComment(1);

        assertNotNull(room);
        commentService.getComment(2);
    }

    @Test
    public void updateCommentTest() {
        Comment adminComment = new Comment("Old message", 5 , true,
                                           new Post(), new User());
        Comment nonAdminComment = new Comment("Old message", 5 , true,
                                              new Post(), new User());
        CommentDTO commentDTO = new CommentDTO(1, "New message", 5,
                                               Calendar.getInstance(), Calendar.getInstance(),
                                        1, 1);

        commentService.updateComment(adminComment, commentDTO, true);
        commentService.updateComment(nonAdminComment, commentDTO, false);
        assertEquals(adminComment.getMessage(), "New message");
        assertEquals(nonAdminComment.getMessage(), "Old message");
    }
}

package com.example.demo;
import com.example.demo.controller.UserController;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.NotEnoughRightException;
import com.example.demo.repository.*;
import com.example.demo.service.PostService;
import com.example.demo.service.PostServiceImpl;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class IntegrationTest {

    @Mock
    private static UserRepository userRepository;

    @Mock
    private static RoleRepository roleRepository;

    @Mock
    private static VerificationTokenRepository verificationTokenRepository;

    @Mock
    private static ResetCodeRepository resetCodeRepository;

    @Mock
    private static PostRepository postRepository;

    @Mock
    private static UnapprovedPostRepository unapprovedPostRepository;

    @Mock
    private static GameRepository gameRepository;

    @Mock
    private Principal principalTrader;

    private List<User> testTraders;

    private Optional<User> testTrader;

    private Optional<User> testReader;

    @Configuration
    static class ContextConfiguration {

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public UserService userService() {
            UserService userService = new UserServiceImpl(userRepository,
                    roleRepository, verificationTokenRepository, resetCodeRepository,
                    bCryptPasswordEncoder());
            return userService;
        }

        @Bean
        public PostService postService() {
            PostService postService = new PostServiceImpl(postRepository,
                    unapprovedPostRepository, gameRepository);
            return postService;
        }

        @Bean
        public UserController userController() {
            UserController userController = new UserController(userService(),
                                                               postService());
            return userController;
        }
    }

    @Autowired
    private UserController userController;

    @Before
    public void init() {
        User trader1 = new User("First","First","First",
                          "first@gmail.com", new Role("ROLE_TRADER"));
        User trader2 = new User("Second","Second","Second",
                          "second@gmail.com", new Role("ROLE_TRADER"));
        User trader3 = new User("Third","Third","Third",
                          "third@gmail.com", new Role("ROLE_TRADER"));
        User trader4 = new User("Fourth","Fourth","Fourth",
                          "fourth@gmail.com", new Role("ROLE_TRADER"));
        User trader5 = new User("Fifth","Fifth","Fifth",
                          "fifth@gmail.com", new Role("ROLE_TRADER"));

        testTraders = new ArrayList<>();
        testTraders.add(trader1);
        testTraders.add(trader2);
        testTraders.add(trader3);
        testTraders.add(trader4);
        testTraders.add(trader5);
        testTrader = Optional.of(new User("Trader","Trader",
                                 "Trader","trader@gmail.com",
                                           new Role("ROLE_TRADER")));
        testReader = Optional.of(new User("Reader","Reader",
                                 "Reader","reader@gmail.com",
                                           new Role("ROLE_READER")));
        when(principalTrader.getName()).thenReturn("Trader");
        when(userRepository.findByEmailAndEnabledTrue("Trader")).thenReturn(testTrader);
        when(userRepository.findByIdAndEnabledTrue(1)).thenReturn(testTrader);
        when(userRepository.findByIdAndEnabledTrue(2)).thenReturn(testReader);
        when(userRepository.findAllNonReaders()).thenReturn(testTraders);
        when(userRepository.findRatingByUser(testTraders.get(0))).thenReturn(3.1);
        when(userRepository.findRatingByUser(testTraders.get(1))).thenReturn(2.9);
        when(userRepository.findRatingByUser(testTraders.get(2))).thenReturn(3.8);
        when(userRepository.findRatingByUser(testTraders.get(3))).thenReturn(4.4);
        when(userRepository.findRatingByUser(testTraders.get(4))).thenReturn(4.1);
    }

    @Test
    public void getAllTradersTest() {
        List<UserDTO> results;

        results = userController.getAllTraders(4.5, 3.0, null, 1, 2);
        assertEquals(results.size(), 2);
    }

    @Test(expected = NotEnoughRightException.class)
    public void deleteUserTest() {
        userController.deleteUser(1, principalTrader);
        userController.deleteUser(2, principalTrader);
    }
}

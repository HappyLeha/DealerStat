package com.example.demo.service;
import com.example.demo.dto.NewPasswordDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.InvalidPasswordException;
import com.example.demo.exception.ResourceAlreadyExistException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnknownServerException;
import com.example.demo.repository.ResetCodeRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final ResetCodeRepository resetCodeRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${emailAddress}")
    private  String emailAddress;

    @Value("${emailPassword}")
    private  String emailPassword;

    @Value("${adminPassword}")
    private String adminPassword;

    @Value("${confirmSubject}")
    private String confirmSubject;

    @Value("${confirmMessage}")
    private String confirmMessage;

    @Value("${codeSubject}")
    private String codeSubject;

    @Value("${codeMessage}")
    private String codeMessage;

    @Value("${successConfirmSubject}")
    private String successConfirmSubject;

    @Value("${successConfirmMessage}")
    private String successConfirmMessage;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           ResetCodeRepository resetCodeRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.resetCodeRepository = resetCodeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    public void init() {
        Role role;

        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_TRADER"));
            roleRepository.save(new Role("ROLE_READER"));
            log.info("Roles were created.");
        }
        role = roleRepository.findByName("ROLE_ADMIN");
        if (!userRepository.existsByRoleAndEnabledTrue(role)) {
            User user = new User(null, null ,
                    bCryptPasswordEncoder.encode(adminPassword),
                    emailAddress, role);

            user.setEnabled(true);
            userRepository.save(user);
            log.info("User " + user + " was created.");
        }
        deleteExpiryTokensAndCodes();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)  {
        if (userRepository.findByEmailAndEnabledTrue(username).isPresent()) {
            return userRepository.findByEmailAndEnabledTrue(username).get();
        } else {
            log.info("User with email " + username + " doesn't exist!");
            throw new ResourceNotFoundException("User with this email doesn't exist!");
        }
    }

    @Override
    public void createUser(UserDTO userDTO)  {
        User user;
        Role role;
        VerificationToken verificationToken;

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.info("User with email " + userDTO.getEmail() + " already exist!");
            throw new ResourceAlreadyExistException("User with this email already exist!");
        }
        role = roleRepository.findByName("ROLE_READER");
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user = Mapper.convertToUser(userDTO, role);
        userRepository.save(user);
        verificationToken = new VerificationToken(user);
        verificationTokenRepository.save(verificationToken);
        confirmMessage += verificationToken.getToken();
        sendEmail(confirmSubject, confirmMessage, user.getEmail());
        deleteAsync(Calendar.DAY_OF_MONTH, 1);
        log.info("User " + user + " has been created.");
    }

    @Override
    public void createCode(String email) {
        User user;
        ResetCode resetCode;

        user = getUserByEmailAndEnabled(email);
        resetCode = user.getResetCode();
        if (resetCode == null) {
            resetCode = new ResetCode(user);
            resetCodeRepository.save(resetCode);
            deleteAsync(Calendar.MINUTE, 15);
        }
        codeMessage += " " + resetCode.getCode();
        sendEmail(codeSubject, codeMessage, email);
        log.info("ResetCode " + resetCode + " has been created.");
    }

    @Override
    public void confirmUser(String token) {
        Optional<VerificationToken> optionalToken =
                verificationTokenRepository.findFirstByToken(token);

        if (optionalToken.isPresent()) {
            VerificationToken verificationToken = optionalToken.get();
            User user = verificationToken.getUser();

            user.setEnabled(true);
            userRepository.save(user);
            verificationTokenRepository.delete(verificationToken);
            sendEmail(successConfirmSubject, successConfirmMessage, user.getEmail());
            log.info("User " + user + " has been confirmed.");
        } else {
            log.info("VerificationToken " + token + " doesn't exist!");
            throw new ResourceNotFoundException("This token doesn't exist!");
        }
    }

    @Override
    public void confirmCode(NewPasswordDTO newPasswordDTO) {
        Optional<ResetCode> optionalCode = resetCodeRepository.findByCode(
                newPasswordDTO.getCode());
        ResetCode resetCode;
        User user;

        if (!optionalCode.isPresent()) {
            log.info("Code " + newPasswordDTO.getCode() + " is wrong.");
            throw new ResourceNotFoundException("This code is wrong");
        }
        resetCode = optionalCode.get();
        user = resetCode.getUser();
        user.setPassword(bCryptPasswordEncoder.encode(newPasswordDTO.
                getNewPassword()));
        userRepository.save(user);
        resetCodeRepository.delete(resetCode);
        log.info("Password for user " + user + " has been changed.");
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(int id) {
        Optional<User> optionalUser = userRepository.findByIdAndEnabledTrue(id);
        User user;

        if (!optionalUser.isPresent()) {
            log.info("User with id " + id + " doesn't exist!");
            throw new ResourceNotFoundException("User with this id doesn't exist!");
        }
        user = optionalUser.get();
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmailAndEnabled(String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndEnabledTrue(email);

        if (!optionalUser.isPresent()) {
            log.info("User with email " + email + " doesn't exist!");
            throw new ResourceNotFoundException(
                    "User with this email doesn't exist!");
        }
        return optionalUser.get();
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmailAndPassword(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmailAndEnabledTrue(email);
        User user;

        if (!optionalUser.isPresent()) {
            log.info("User with email " + email + " doesn't exist.");
            throw new ResourceNotFoundException("User with this email doesn't exist.");
        }
        user = optionalUser.get();
        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        else {
            log.info("This password is wrong!");
            throw new InvalidPasswordException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        for (User user: users) {
            calculateRating(user);
        }
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllReaders() {
        return userRepository.findAllReaders();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllTraders() {
        return userRepository.findAllNonReaders();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllTradersByGames(List<Integer> idList) {
        return userRepository.findUserByGames(idList);
    }

    @Override
    public List<User> filterTraders(List<User> users, double max, double min,
                                    int skip, int limit) {
        for (User user: users) {
            calculateRating(user);
        }
        users = users.stream().filter(user -> user.getRating() != null)
                .filter(user -> user.getRating() >= min)
                .filter(user -> user.getRating() <= max)
                .sorted((user1, user2) -> {
                    if (user1.getRating() < user2.getRating()) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }).collect(Collectors.toList());
        if (skip != 0 ) {
            users = users.stream().skip(skip).collect(Collectors.toList());
        }
        if (limit != 0) {
            users = users.stream().limit(limit).collect(Collectors.toList());
        }
        return users;
    }

    @Override
    public boolean isAdmin(User user) {
        return (user.getRole().getName().equals("ROLE_ADMIN"));
    }

    @Override
    public void updateUser(User user, UserDTO userDTO) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        log.info("User " + user + " has been updated.");
    }

    @Override
    public void changeRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName);

        user.setRole(role);
        userRepository.save(user);
        log.info("Role of User " + user + " has been changed.");
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
        log.info("User " + user + " has been deleted.");
    }

    @Override
    @Transactional(readOnly = true)
    public void calculateRating(User user) {
        if (!user.getRole().getName().equals("ROLE_READER")) {
            user.setRating(userRepository.findRatingByUser(user));
        }
    }

    @Async
    public void deleteAsync(int period, int value) {
        TaskScheduler scheduler;
        ScheduledExecutorService localExecutor = Executors
                .newSingleThreadScheduledExecutor();
        scheduler = new ConcurrentTaskScheduler(localExecutor);
        Runnable runnable = this::deleteExpiryTokensAndCodes;
        Calendar calendar = Calendar.getInstance();
        Date date;

        calendar.add(period, value);
        date = calendar.getTime();
        scheduler.schedule(runnable, date);
    }

    public void deleteExpiryTokensAndCodes() {
        verificationTokenRepository.deleteByExpiryDateBeforeCurrent();
        userRepository.deleteByTokenIsNullAndEnabledFalse();
        resetCodeRepository.deleteByExpiryDateBeforeCurrent();
        log.info("Expiry codes and tokens have been deleted.");
    }

    private void sendEmail(String subject, String message, String to)  {
        Properties props;
        Session session;
        MimeMessage mimeMessage;

        try {
            props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAddress, emailPassword);
                }
            });
            mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(emailAddress));
            mimeMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            Transport.send(mimeMessage);
            log.info("Email with text " + message + " was sent to address " +  to
                    + ".");
        }
        catch (MessagingException e) {
            log.error("Email with text " + message + "wasn't sent!");
            throw new UnknownServerException();
        }
    }
}

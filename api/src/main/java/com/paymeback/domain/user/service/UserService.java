package com.paymeback.domain.user.service;

import com.paymeback.common.exception.BusinessException;
import com.paymeback.common.exception.ErrorCode;
import com.paymeback.user.domain.RawPassword;
import com.paymeback.user.domain.User;
import com.paymeback.domain.user.dto.SignUpRequest;
import com.paymeback.domain.user.dto.UserResponse;
import com.paymeback.user.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(SignUpRequest request) {
        validateDuplicateEmail(request.email());
        validatePassword(request.password(), request.confirmPassword());

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.createUser(
            request.email(),
            encodedPassword,
            request.name()
        );

        User savedUser = userRepository.save(user);
        log.info("새 사용자가 생성되었습니다. email: {}", savedUser.email());

        return UserResponse.from(savedUser);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void verifyUser(String email) {
        User user = findByEmail(email);
        User verified = user.verifyEmail();
        userRepository.save(verified);
        log.info("사용자 이메일 인증이 완료되었습니다. email: {}", email);
    }


    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    private void validateDuplicateEmail(String email) {
        if (isEmailDuplicate(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validatePassword(String password, String confirmPassword) {

        RawPassword rawPassword = new RawPassword(password);
        RawPassword confirm = new RawPassword(confirmPassword);

        rawPassword.matchOrThrow(confirm);
    }
}
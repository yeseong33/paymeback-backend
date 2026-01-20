package com.paymeback.user.adapter;


import com.paymeback.user.domain.User;
import com.paymeback.user.entity.UserEntity;
import com.paymeback.user.repository.UserJpaRepository;
import com.paymeback.user.repository.UserRepositoryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository repo;

    @Override
    public User save(User user) {
        var saved = repo.save(UserJpaMapper.toEntity(user));
        return UserJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email).map(UserJpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

}
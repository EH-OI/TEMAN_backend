package TEMAN.repository;

import TEMAN.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //이메일로 유저 찾기
    Optional<User> findUserByEmail(String email);

    //로그인 아이디로 유저 찾기 - 소셜 로그인 등 아이디 기반 조회
    Optional<User> findUserByLoginId(String loginId);

    //회원가입 시 이메일 중복 확인
    public boolean existsByEmail(String email);

    //회원가입 시 아이디 중복 확인
    public boolean existsByLoginId(String loginId);
}

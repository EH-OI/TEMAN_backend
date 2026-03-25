package TEMAN.domain;

import TEMAN.domain.enums.CountryEnum;
import TEMAN.domain.enums.ProviderEnum;
import TEMAN.domain.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@DynamicUpdate

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    @Email(message = "This is not a valid email format.")
    private String email;

    @Column(unique = true)
    @NotBlank
    private String loginId;

    @Column
    @NotBlank
    private String fullName;

    private String password;

    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column
    //@NotNull
    private CountryEnum countryEnum;

    @Column
    private String phone;

    private String profileImageUrl;

    //관심사 선택
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interests")
    private List<String> interests = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column
    private RoleEnum roleEnum;

    @Enumerated(EnumType.STRING)
    @Column
    private ProviderEnum providerEnum;

    @Column
    private String socialId;

    //기존 유저인지 여부
    @Column
    private boolean isOriginalUser;

    @Builder
    public User(String email, String loginId, String fullName, String password, Integer age, CountryEnum countryEnum, String phone, String profileImageUrl, List<String> interests, RoleEnum roleEnum, ProviderEnum providerEnum, String socialId, boolean isOriginalUser) {
        this.email = email;
        this.loginId = loginId;
        this.fullName = fullName;
        this.password = password;
        this.age = age;
        this.countryEnum = countryEnum;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.roleEnum = roleEnum;
        this.providerEnum = providerEnum;
        this.socialId = socialId;
        this.isOriginalUser = isOriginalUser;
    }

    public void updateInterests(List<String> interests) {
        if(interests != null && interests.size()>5) {
            throw new IllegalArgumentException("You can select up to 5 interests.");
        }
        this.interests.clear();
        if(interests != null) {
            this.interests.addAll(interests);
        }
    }

    public void updatePassword(String tempPassword) {
        this.password = tempPassword;
    }

}

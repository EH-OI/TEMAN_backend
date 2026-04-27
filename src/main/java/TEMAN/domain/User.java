package TEMAN.domain;

import TEMAN.domain.enums.CountryEnum;
import TEMAN.domain.enums.GenderEnum;
import TEMAN.domain.enums.ProviderEnum;
import TEMAN.domain.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
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
    private Boolean isOriginalUser;

    @Column
    private Boolean agreeTerms;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_genders", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private List<GenderEnum> genders = new ArrayList<>();

    @Column
    private Boolean showGender;

    @Column
    private LocalDate birthday;

    @Column
    private String bio;

    @Column
    private String instagramId;

    @Builder
    public User(String email, String loginId, String fullName, String password, Integer age, CountryEnum countryEnum, String phone, String profileImageUrl, List<String> interests, RoleEnum roleEnum, ProviderEnum providerEnum, String socialId, Boolean isOriginalUser, Boolean agreeTerms, List<GenderEnum> genders, Boolean showGender, LocalDate birthday, String bio, String instagramId) {
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
        this.agreeTerms = agreeTerms;
        this.genders = (genders != null) ? genders : new ArrayList<>();
        this.interests = (interests != null) ? interests : new ArrayList<>();
        this.showGender = showGender;
        this.birthday = birthday;
        this.bio = bio;
        this.instagramId = instagramId;
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

    public void completeOnboarding(String fullName, LocalDate birthday, List<GenderEnum> genders, Boolean showGender, String bio, String instagramId, List<String> interests) {
        this.agreeTerms = true;
        this.fullName = fullName;
        this.birthday = birthday;
        this.showGender = showGender;
        this.bio = bio;
        this.instagramId = instagramId;
        this.genders.clear();
        if(genders != null) this.genders.addAll(genders);
        this.interests.clear();
        if(interests != null) this.interests.addAll(interests);
    }

    public void updateProfile(String fullName, String bio, String instagramId, List<String> interests, List<GenderEnum> genders, Boolean showGender, String profileImageUrl) {

        if (fullName != null) this.fullName = fullName;
        if (bio != null) this.bio = bio;
        if (instagramId != null) this.instagramId = instagramId;
        if (showGender != null) this.showGender = showGender;

        // 프사 변경이 있을 때만 업데이트
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;

        // 리스트 업데이트
        if (interests != null) {
            this.interests.clear();
            this.interests.addAll(interests);
        }
        if (genders != null) {
            this.genders.clear();
            this.genders.addAll(genders);
        }
    }
}

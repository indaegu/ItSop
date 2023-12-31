package kosta.main.users.entity;
import jakarta.persistence.*;
import kosta.main.dibs.entity.Dib;
import kosta.main.exchangehistories.entity.ExchangeHistory;
import kosta.main.global.audit.Auditable;
import kosta.main.blockedusers.entity.BlockedUser;
import kosta.main.images.entity.Image;
import kosta.main.users.dto.UserCreateDto;
import kosta.main.users.dto.UserUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(unique = true, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image_id", referencedColumnName = "imageId")
    private Image profileImage; // 사용자의 프로필 이미지

    @Builder.Default
    @Column(length = 20, nullable = false)
    private UserStatus userStatus = UserStatus.ACTIVATE;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BlockedUser> blockedUsers = new ArrayList<>(); // 클래스 이름을 단수형으로 변경

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExchangeHistory> exchangeHistories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dib> dibs = new ArrayList<>();


    public User updateUser(UserUpdateDto userUpdateDto) {
        this.userStatus = !nullCheck(userUpdateDto.getUserStatus()) ? userUpdateDto.getUserStatus(): this.userStatus;
        this.address = !nullCheck(userUpdateDto.getAddress()) ? userUpdateDto.getAddress(): this.address;
        this.phone = !nullCheck(userUpdateDto.getPhone()) ? userUpdateDto.getPhone() : this.phone;
        this.password = !nullCheck(userUpdateDto.getPassword()) ? userUpdateDto.getPassword() : this.password;
//        this.profileImage = !nullCheck(userUpdateDto.getProfileImage()) ? userUpdateDto.getProfileImage() : this.profileImage;
        this.name = !nullCheck(userUpdateDto.getName()) ? userUpdateDto.getName() : this.name;
        return this;
    }

    private boolean nullCheck(UserStatus userStatus) {
        return userStatus == null;
    }
    private boolean nullCheck(String string) {
        return string == null;
    }

    public enum UserStatus{
        ACTIVATE, BANNED ,DELETED
    }
    // 게터와 세터
    // 생략...

    public static User createUser(UserCreateDto userCreateDto){
        return User.builder()
                .name(userCreateDto.getName())
                .password(userCreateDto.getPassword())
                .email(userCreateDto.getEmail())
                .phone(userCreateDto.getPhone())
                .address(userCreateDto.getAddress())
                .build();
    }

    public void deleteUser(){
        this.userStatus = UserStatus.DELETED;
    }
    public void addBlockedUser(BlockedUser blockedUser){
        this.blockedUsers.add(blockedUser);
    }

}

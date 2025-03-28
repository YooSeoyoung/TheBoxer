package com.dw.TheBoxer.model;

import com.dw.TheBoxer.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "story")
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "add_time")
    private LocalDateTime addTime;

    @ManyToOne
    @JoinColumn(name = "user_name")
    private User user; // 유저 개인은 피드를 생성함.

    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility; // 피드는 유저 개인의 것이니 다른 사람과 기본 공유 설정은 안 됨.

    @OneToMany(mappedBy = "story")
    private List<Comment> comments;

    @OneToMany(mappedBy = "story")
    private List<Great> greats;

    @Column(name = "alert_sent", nullable = false)
    private Boolean alertSent; // 알람 발송 여부. 기본 false. 레포지토리에서 설정
}

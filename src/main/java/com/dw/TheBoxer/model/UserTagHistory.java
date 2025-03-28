package com.dw.TheBoxer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "user_tag_history")
public class UserTagHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tag_name")
    private Tag tag;  // 유저가 사용한 태그

    @ManyToOne
    @JoinColumn(name = "user_name")
    private User user;

    @Column(name = "used_time")
    private LocalDateTime usedTime;
}

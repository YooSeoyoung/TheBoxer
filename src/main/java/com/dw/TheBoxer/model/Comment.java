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
@Table(name = "comment")
public class Comment { // 댓글 기능

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "add_date")
<<<<<<< HEAD
    private LocalDateTime addDate; // 작성시간
=======
    private LocalDateTime addDate; // 작성 및 수정시간
    
>>>>>>> 39ac10673c54e7f8686b8685d0450766faa96e6c

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Album album;

    @ManyToOne
    @JoinColumn(name = "user_name")
    private User user;


}

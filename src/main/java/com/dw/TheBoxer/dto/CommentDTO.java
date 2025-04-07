package com.dw.TheBoxer.dto;

import com.dw.TheBoxer.model.Album;
import com.dw.TheBoxer.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTO {

    private Long id;
    private String content;
    private LocalDateTime addDate;
    private Long albumId;
    private String username;
}

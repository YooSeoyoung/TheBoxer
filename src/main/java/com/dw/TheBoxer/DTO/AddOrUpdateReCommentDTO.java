package com.dw.TheBoxer.DTO;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AddOrUpdateReCommentDTO {
    private String content;
    private LocalDateTime addDate;
    private Long commentId;
}

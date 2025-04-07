package com.dw.TheBoxer.dto;

import com.dw.TheBoxer.enums.AlarmType;
import com.dw.TheBoxer.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AlramDTO{
    private Long id;
    private String username;
    private AlarmType alarmType;
    private String message;
    private LocalDateTime addDate;
    private boolean isRead;
    private String weatherInfo;
}

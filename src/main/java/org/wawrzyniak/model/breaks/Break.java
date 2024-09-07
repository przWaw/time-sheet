package org.wawrzyniak.model.breaks;

import lombok.*;

import java.time.DayOfWeek;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class Break {
    private DayOfWeek day;
    private int duration;
    private int order;
    private String breakLocation;
}

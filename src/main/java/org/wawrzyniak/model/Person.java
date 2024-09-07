package org.wawrzyniak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wawrzyniak.model.breaks.Break;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    private String name;
    private int maxWorkingTime;
    private int availableWorkingTime;
    private List<Break> availableBreaks;

    public Person createCopy() {
        return new Person(name, maxWorkingTime, availableWorkingTime, availableBreaks);
    }
}

package org.wawrzyniak.model.breaks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wawrzyniak.model.Person;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakSuperposition {
    private Break breakDef;
    private List<Person> possiblePersons;

    public BreakSuperposition createCopy() {
        return new BreakSuperposition(breakDef, possiblePersons.stream().map(Person::createCopy).collect(Collectors.toList()));
    }
}

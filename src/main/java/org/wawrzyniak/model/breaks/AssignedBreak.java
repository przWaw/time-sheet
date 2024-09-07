package org.wawrzyniak.model.breaks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wawrzyniak.model.Person;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedBreak {
    private Break breakDef;
    private Person assignedPerson;
}

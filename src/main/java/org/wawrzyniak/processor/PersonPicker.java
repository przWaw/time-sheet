package org.wawrzyniak.processor;

import org.wawrzyniak.model.Person;
import org.wawrzyniak.model.breaks.AssignedBreak;
import org.wawrzyniak.model.breaks.Break;

import java.util.List;

public interface PersonPicker {
    List<AssignedBreak> assignPersonsToBreaks(List<Break> allBreaks, List<Person> availablePersons) throws Exception;
}

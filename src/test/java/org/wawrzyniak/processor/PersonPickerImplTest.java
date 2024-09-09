package org.wawrzyniak.processor;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.wawrzyniak.model.Person;
import org.wawrzyniak.model.breaks.AssignedBreak;
import org.wawrzyniak.model.breaks.Break;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(Parameterized.class)
class PersonPickerImplTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[10][0];
    }

    public PersonPickerImplTest() {
    }

    private final PersonPicker personPicker = new PersonPickerImpl();

    @Test
    void assignPersonsToBreaksNoLists() throws Exception {
        //given
        List<Break> breakList = null;
        List<Person> availablePersons = null;

        //when
        List<AssignedBreak> assignedBreaks = personPicker.assignPersonsToBreaks(breakList, availablePersons);

        //then
        checkCorrectionOfTimesheet(breakList, assignedBreaks);

    }

    @Test
    void assignPersonsToBreaksEmptyList() throws Exception {
        //given
        List<Break> breakList = new ArrayList<>();
        List<Person> availablePersons = new ArrayList<>();

        //when
        List<AssignedBreak> assignedBreaks = personPicker.assignPersonsToBreaks(breakList, availablePersons);

        //then
        checkCorrectionOfTimesheet(breakList, assignedBreaks);
    }

    @Test
    void assignPersonsToBreaks1Breaks1Person() throws Exception {
        //given
        List<Break> breakList = new ArrayList<>();
        List<Person> availablePersons = new ArrayList<>();

        breakList.add(Break.builder().day(DayOfWeek.MONDAY).duration(15).order(0).build());
        availablePersons.add(Person.builder().name("Kowalski").availableBreaks(breakList).maxWorkingTime(20).availableWorkingTime(20).build());

        //when
        List<AssignedBreak> assignedBreaks = personPicker.assignPersonsToBreaks(breakList, availablePersons);

        //then
        checkCorrectionOfTimesheet(breakList, assignedBreaks);

    }

    @Test
    void assignPersonsToBreaks5Breaks4Person() throws Exception {
        //given
        List<Break> breakList = new ArrayList<>();
        List<Person> availablePersons = new ArrayList<>();

        breakList.add(Break.builder().day(DayOfWeek.MONDAY).duration(15).order(0).build());
        breakList.add(Break.builder().day(DayOfWeek.MONDAY).duration(15).order(1).build());
        breakList.add(Break.builder().day(DayOfWeek.MONDAY).duration(15).order(2).build());
        breakList.add(Break.builder().day(DayOfWeek.MONDAY).duration(15).order(3).build());
        breakList.add(Break.builder().day(DayOfWeek.MONDAY).duration(15).order(4).build());

        availablePersons.add(Person.builder().name("A").availableBreaks(breakList).maxWorkingTime(30).availableWorkingTime(30).build());
        availablePersons.add(Person.builder().name("B").availableBreaks(breakList).maxWorkingTime(30).availableWorkingTime(30).build());
        availablePersons.add(Person.builder().name("C").availableBreaks(breakList).maxWorkingTime(30).availableWorkingTime(30).build());
        availablePersons.add(Person.builder().name("D").availableBreaks(breakList).maxWorkingTime(30).availableWorkingTime(30).build());



        //when
        List<AssignedBreak> assignedBreaks = personPicker.assignPersonsToBreaks(breakList, availablePersons);

        //then
        checkCorrectionOfTimesheet(breakList, assignedBreaks);


    }

    private void checkCorrectionOfTimesheet(List<Break> breaks, List<AssignedBreak> assignedBreaks) {
        assertEquals(breaks.size(), assignedBreaks.size());

        checkIfAllBreaksHavePerson(assignedBreaks);

        checkIfSumOfTimeOfBreaksIsEqual(breaks, assignedBreaks);

        checkIfPersonsDontHaveToMuchBreaks(assignedBreaks);
    }

    private static void checkIfPersonsDontHaveToMuchBreaks(List<AssignedBreak> assignedBreaks) {
        List<Person> personList = assignedBreaks.stream().map(AssignedBreak::getAssignedPerson).distinct().toList();
        for(Person person : personList) {
            int sumOfDurations = assignedBreaks.stream().filter(assignedBreak -> assignedBreak.getAssignedPerson().equals(person)).map(AssignedBreak::getBreakDef).mapToInt(Break::getDuration).sum();
            boolean notToMuch = sumOfDurations <= person.getMaxWorkingTime();
            String message = "Person " + person.getName() + " have " + sumOfDurations + " minutes of worktime vs " + person.getMaxWorkingTime() + " max";
            assertTrue(notToMuch, message);
        }
    }

    private static void checkIfSumOfTimeOfBreaksIsEqual(List<Break> breaks, List<AssignedBreak> assignedBreaks) {
        int expectedDuration = breaks.stream().mapToInt(Break::getDuration).sum();
        int actualDuration = assignedBreaks.stream().map(AssignedBreak::getBreakDef).mapToInt(Break::getDuration).sum();
        assertEquals(expectedDuration, actualDuration);
    }

    private static void checkIfAllBreaksHavePerson(List<AssignedBreak> assignedBreaks) {
        List<AssignedBreak> emptyBreaks = assignedBreaks.stream().filter(assignedBreak -> assignedBreak.getAssignedPerson() == null).toList();
        assertEquals(0, emptyBreaks.size());
    }

}
package org.wawrzyniak.processor;

import org.wawrzyniak.model.Person;
import org.wawrzyniak.model.breaks.AssignedBreak;
import org.wawrzyniak.model.breaks.Break;
import org.wawrzyniak.model.breaks.BreakSuperposition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class PersonPickerImpl implements PersonPicker{

    private final Random random = new Random();
    private final Logger log = Logger.getLogger(getClass().getName());

    @Override
    public List<AssignedBreak> assignPersonsToBreaks(List<Break> allBreaks, List<Person> availablePersons) throws Exception {
        var waveFunction = this.createWaveFunction(allBreaks, availablePersons);

        for (int i = 0; i < 10000; i++) {
            var collapsedFunction = collapseWaveFunction(waveFunction, random);
            if (Objects.nonNull(collapsedFunction)) {
                log.log(Level.INFO, "{} attempts to success", i);
                return collapsedFunction;
            }
        }

        return null;
    }

    private List<BreakSuperposition> createWaveFunction(List<Break> breaks, List<Person> persons) throws Exception {
        var waveFunction = new ArrayList<BreakSuperposition>();

        breaks.forEach(breakDef -> {
            var possiblePersons = persons.stream()
                    .filter(person -> person.getAvailableBreaks().contains(breakDef))
                    .toList();
            waveFunction.add(
                    BreakSuperposition.builder()
                        .possiblePersons(possiblePersons)
                        .breakDef(breakDef)
                        .build());
        });

        validateFunction(waveFunction);

        return waveFunction;
    }

    private void validateFunction(List<BreakSuperposition> breaks) throws Exception {
        var illegalBreaks = new ArrayList<BreakSuperposition>();

        breaks.forEach(breakSuperposition -> {
            if (breakSuperposition.getPossiblePersons().isEmpty()) {
                illegalBreaks.add(breakSuperposition);
            }
        });

        if (!illegalBreaks.isEmpty()) {
            throw new Exception("Breaks without possible persons exists!!!");
        }
    }

    private List<AssignedBreak> collapseWaveFunction(List<BreakSuperposition> originalWaveFunction, Random random) {
        var waveFunction = originalWaveFunction.stream().map(BreakSuperposition::createCopy).collect(Collectors.toList());

        var collapsedBreaks = new ArrayList<AssignedBreak>();

        while (!waveFunction.isEmpty() &&
                waveFunction.stream()
                        .filter(breakSuperposition -> breakSuperposition.getPossiblePersons().isEmpty())
                        .toList()
                        .isEmpty()
        ) {
            var breakSuperposition = waveFunction.get(random.nextInt(waveFunction.size()));
            var person = breakSuperposition.getPossiblePersons().get(random.nextInt(breakSuperposition.getPossiblePersons().size()));
            collapsedBreaks.add(new AssignedBreak(breakSuperposition.getBreakDef(), person));
            person.setAvailableWorkingTime(person.getAvailableWorkingTime() - breakSuperposition.getBreakDef().getDuration());
            waveFunction.remove(breakSuperposition);
            removePersonFromIllegalBreaks(waveFunction, collapsedBreaks, breakSuperposition, person);
        }

        if (!waveFunction.isEmpty()) {
            return null;
        }

        return collapsedBreaks;
    }

    private void removePersonFromIllegalBreaks(List<BreakSuperposition> toCollapse, List<AssignedBreak> collapsed, BreakSuperposition chosenBreak, Person chosenPerson) {
        // no same breaks
        toCollapse.stream()
                .filter(bSup ->
                        bSup.getBreakDef().getDay() == chosenBreak.getBreakDef().getDay() &&
                        bSup.getBreakDef().getOrder() == chosenBreak.getBreakDef().getOrder() &&
                        bSup.getPossiblePersons().contains(chosenPerson)
                ).forEach(bSup -> bSup.getPossiblePersons().remove(chosenPerson));

        // no longer breaks than available time
        toCollapse.stream()
                .filter(bSup ->
                        bSup.getBreakDef().getDuration() > chosenPerson.getAvailableWorkingTime()
                ).forEach(bSup -> bSup.getPossiblePersons().remove(chosenPerson));

        var personBreaks = collapsed.stream()
                .filter(assignedBreak -> assignedBreak.getAssignedPerson() == chosenPerson)
                .map(AssignedBreak::getBreakDef).toList();

        personBreaks.forEach(personBreak -> {
            // no 3 breaks in line, check for neighbouring
            if (!personBreaks.stream()
                    .filter(personBreakInner ->
                            personBreakInner.getDay() == personBreak.getDay() &&
                            personBreakInner.getOrder() == personBreak.getOrder() - 1
                    ).toList().isEmpty()
            ) {
                toCollapse.stream().filter(bSup ->
                        (bSup.getBreakDef().getDay() == personBreak.getDay() &&
                        bSup.getBreakDef().getOrder() == personBreak.getOrder() + 1) ||
                        (bSup.getBreakDef().getDay() == personBreak.getDay() &&
                        bSup.getBreakDef().getOrder() == personBreak.getOrder() - 2)
                ).forEach(bSup -> bSup.getPossiblePersons().remove(chosenPerson));
            }
            if (!personBreaks.stream()
                    .filter(personBreakInner ->
                            personBreakInner.getDay() == personBreak.getDay() &&
                            personBreakInner.getOrder() == personBreak.getOrder() + 1
                    ).toList().isEmpty()
            ) {
                toCollapse.stream().filter(bSup ->
                        (bSup.getBreakDef().getDay() == personBreak.getDay() &&
                        bSup.getBreakDef().getOrder() == personBreak.getOrder() - 1) ||
                        (bSup.getBreakDef().getDay() == personBreak.getDay() &&
                        bSup.getBreakDef().getOrder() == personBreak.getOrder() + 2)
                ).forEach(bSup -> bSup.getPossiblePersons().remove(chosenPerson));
            }
            // // no 3 breaks in line, check checker selected
            if (!personBreaks.stream()
                    .filter(personBreakInner ->
                            personBreakInner.getDay() == personBreak.getDay() &&
                            personBreakInner.getOrder() == personBreak.getOrder() - 2
                    ).toList().isEmpty()
            ) {
                toCollapse.stream().filter(bSup ->
                        (bSup.getBreakDef().getDay() == personBreak.getDay() &&
                        bSup.getBreakDef().getOrder() == personBreak.getOrder() - 1)
                ).forEach(bSup -> bSup.getPossiblePersons().remove(chosenPerson));
            }
            if (!personBreaks.stream()
                    .filter(personBreakInner ->
                            personBreakInner.getDay() == personBreak.getDay() &&
                            personBreakInner.getOrder() == personBreak.getOrder() + 2
                    ).toList().isEmpty()
            ) {
                toCollapse.stream().filter(bSup ->
                        (bSup.getBreakDef().getDay() == personBreak.getDay() &&
                        bSup.getBreakDef().getOrder() == personBreak.getOrder() + 1)
                ).forEach(bSup -> bSup.getPossiblePersons().remove(chosenPerson));
            }
            // no two long breaks a day
            if (personBreaks.contains(personBreak.withDuration(15))) {
                toCollapse.stream().filter(bSup ->
                        bSup.getBreakDef() == personBreak.withDuration(15)
                ).forEach(bSup -> bSup.getPossiblePersons().remove(chosenPerson));
            }
        });
    }
}

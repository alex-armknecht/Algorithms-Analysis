package main.csp;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Objects;


/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSPSolver {

    // Backtracking CSP Solver
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
        List<LocalDate> schedule = new ArrayList<LocalDate>();
        List<MeetingDomain> domains = new ArrayList<MeetingDomain>();
        for (int i = 0; i < nMeetings; i++) {
            domains.add(new MeetingDomain(rangeStart, rangeEnd));
        }
        nodeConsistency (domains, constraints); 
        arcConsistency (domains, constraints);
        return backtrack(schedule, domains, nMeetings, 0, constraints);
    }
    
    
    // Filtering Operations
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Enforces node consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *unary* constraints! 
     */
    public static void nodeConsistency (List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
        List<MeetingDomain> updatedDomList = new ArrayList<>();
        for (DateConstraint cont : constraints ) {
            if (cont.arity() == 1) {
                Set<LocalDate> unaryDoms = varDomains.get(((UnaryDateConstraint)(cont)).L_VAL).domainValues;
                Set<LocalDate> workingDoms = new HashSet<>();
                for (LocalDate date : unaryDoms) {
                    if (cont.isSatisfiedBy(date, ((UnaryDateConstraint)(cont)).R_VAL)) {
                        workingDoms.add(date);
                    }
                }
                varDomains.get(((UnaryDateConstraint)(cont)).L_VAL).domainValues = workingDoms;        
            }
        }   
        varDomains = updatedDomList;
    } 
    
    /**
     * Enforces arc consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *binary* constraints using the AC-3 algorithm! 
     */
    public static void arcConsistency (List<MeetingDomain> varDomains, Set<DateConstraint> constraints) {
        List<Arc> csp = makeArcs(constraints);
        Set<Arc> arcQueue= new HashSet<>(csp);
        while(! arcQueue.isEmpty()) {
            Arc currentArc = arcQueue.iterator().next();
            arcQueue.remove(currentArc);
            if (removeInconsisVals(currentArc.CONSTRAINT, varDomains.get(currentArc.HEAD), varDomains.get(currentArc.TAIL))) {
                for (Arc arc : csp) {
                    if (arc.HEAD == currentArc.TAIL) {
                        arcQueue.add(arc);
                    }
                }
            }
        }
    }
    
    /**
     * Private helper class organizing Arcs as defined by the AC-3 algorithm, useful for implementing the
     * arcConsistency method.
     * [!] You may modify this class however you'd like, its basis is just a suggestion that will indeed work.
     */
    private static class Arc {
        
        public final DateConstraint CONSTRAINT;
        public final int TAIL, HEAD;
        
        /**
         * Constructs a new Arc (tail -> head) where head and tail are the meeting indexes
         * corresponding with Meeting variables and their associated domains.
         * @param tail Meeting index of the tail
         * @param head Meeting index of the head
         * @param c Constraint represented by this Arc.
         * [!] WARNING: A DateConstraint's isSatisfiedBy method is parameterized as:
         * isSatisfiedBy (LocalDate leftDate, LocalDate rightDate), meaning L_VAL for the first
         * parameter and R_VAL for the second. Be careful with this when creating Arcs that reverse
         * direction. You may find the BinaryDateConstraint's getReverse method useful here.
         */
        public Arc (int tail, int head, DateConstraint c) {
            this.TAIL = tail;
            this.HEAD = head;
            this.CONSTRAINT = c;
        }
        
        @Override
        public boolean equals (Object other) {
            if (this == other) { return true; }
            if (this.getClass() != other.getClass()) { return false; }
            Arc otherArc = (Arc) other;
            return this.TAIL == otherArc.TAIL && this.HEAD == otherArc.HEAD && this.CONSTRAINT.equals(otherArc.CONSTRAINT);
        }
        
        @Override
        public int hashCode () {
            return Objects.hash(this.TAIL, this.HEAD, this.CONSTRAINT);
        }
        
        @Override
        public String toString () {
            return "(" + this.TAIL + " -> " + this.HEAD + ")";
        }
        
    }
    
    /**
     * Private recursive helper method to perform backtracking on a CSP to be used in the Solve method. 
     * @param schedule List to hold the added dates that satisfies each of the constraints for each 
     * of the n meetings, indexed by the variable they satisfy, or null if no solution exists.
     * @param domains List of MeetingDomain objects, that contain the date domains for each meeting.
     * @param numMeetings Integer number of meetings that must be scheduled, indexed from 0 to n-1
     * @param currentMeeting Integer index value of the meeting that the method is focused on.
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return schedule List of dates that satisfies each of the constraints for each 
     * of the n meetings, indexed by the variable they satisfy, or null if no solution exists.
     */
    private static List<LocalDate> backtrack(List<LocalDate> schedule, List<MeetingDomain> domains, int numMeetings, int currentMeeting, Set<DateConstraint> constraints) {
        if (schedule.size() == numMeetings) { return schedule; }
        Set<LocalDate> domainDates = domains.get(currentMeeting).domainValues;
        for (LocalDate date : domainDates) {
            schedule.add(date);
            if (checkConst(currentMeeting, schedule, date, constraints)) {
                List<LocalDate> result = backtrack(schedule, domains, numMeetings, currentMeeting + 1, constraints);
                if (result != null) { return result; }
            }
            schedule.remove(currentMeeting);
        }
        return null;  
    }
    
    /**
     * Private helper method to check to see if a date works with all it's applicable constraints.
     * @param currentMeeting Integer index value of the meeting that the method is focused on.
     * @param schedule List to hold the added dates that satisfies each of the constraints for each 
     * of the n meetings, indexed by the variable they satisfy, or null if no solution exists.
     * @param currDate LocalDate value that is to be checked with the applicable constraints.
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return Boolean value that is true if the date passes all applicable constraints, false otherwise. 
     */
    private static boolean checkConst(int currentMeeting, List<LocalDate> schedule, LocalDate currDate, Set<DateConstraint> constraints) {
        for (DateConstraint cont : constraints) {
            if (cont.arity() == 1) {
                if (((UnaryDateConstraint)(cont)).L_VAL < schedule.size() && ((UnaryDateConstraint)(cont)).L_VAL == currentMeeting) {
                    if (! cont.isSatisfiedBy(currDate, ((UnaryDateConstraint)(cont)).R_VAL)) { return false; }
                }
            }
            if (cont.arity() == 2) {
                if (((BinaryDateConstraint)(cont)).L_VAL < schedule.size()  && ((BinaryDateConstraint)(cont)).R_VAL < schedule.size()) {
                    if (((BinaryDateConstraint)(cont)).L_VAL == currentMeeting) {
                        if (! cont.isSatisfiedBy(currDate, schedule.get(((BinaryDateConstraint)(cont)).R_VAL))) { return false; }
                    }
                    if (((BinaryDateConstraint)(cont)).R_VAL == currentMeeting) {
                        if (! cont.isSatisfiedBy(schedule.get(((BinaryDateConstraint)(cont)).L_VAL), currDate)) { return false; }
                    }
                }
            }
        }
        return true;
    } 
    
    /**
     * Private helper method for arcConsistency that a constraint graph by creating 
     * all the Arc objects and adding them to a list.
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return List of Arc objects that represent a constraint graph for the CSP.
     */
    private static List<Arc> makeArcs(Set<DateConstraint> constraints) {
        List<Arc> arcs = new ArrayList<Arc>();
        for (DateConstraint cont : constraints) {
            if (cont.arity() == 2 ) {
                arcs.add( new Arc (((BinaryDateConstraint)(cont)).L_VAL, ((BinaryDateConstraint)(cont)).R_VAL, cont));
                arcs.add( new Arc (((BinaryDateConstraint)(cont)).R_VAL, ((BinaryDateConstraint)(cont)).L_VAL, ((BinaryDateConstraint) cont).getReverse()));
            }
        }
        return arcs;
    }
    
    /**
     * Private helper method for arcConsistency that removes an arc's domains that fail the constraint.
     * @param cont DateConstraint that the Arc will be tested with.
     * @param head The Head node of the current Arc thats being tested.
     * @param tail The Tail node of the current Arc thats being tested.
     * @return Boolean value that is true if a date is invalid and removed from domain, false otherwise.
     */
    private static boolean removeInconsisVals(DateConstraint cont, MeetingDomain head, MeetingDomain tail) {
        boolean removed = false;
        boolean domWorks = false;
        Set<LocalDate> tailDoms = new HashSet<>(tail.domainValues);
        Set<LocalDate> headDoms = new HashSet<>(head.domainValues);
        for (LocalDate tDate : tailDoms) {
           domWorks = false;
            for (LocalDate hDate : headDoms) {
                if (cont.isSatisfiedBy(tDate, hDate)) {
                    domWorks = true;
                    break;
                }
            }
            if (! domWorks) {
                tail.domainValues.remove(tDate);
                removed = true;
            }
        }
        return removed;
    }
}

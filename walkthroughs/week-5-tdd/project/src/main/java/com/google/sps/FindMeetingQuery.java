// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public final class FindMeetingQuery {

    // Sorts a list of events.
    private List<Event> sort(List<Event> events) {

        Comparator<Event> time = (Event event1, Event event2) -> TimeRange.ORDER_BY_START.compare(event1.getWhen(), event2.getWhen());
        Collections.sort(events, time);
        return events;
    }

    // Returns true if an attendee is not available.
    private boolean isAttendeeBusy(Collection<String> eventAttendees, Collection<String>attendees) {

        for (String person : attendees) {
            if (eventAttendees.contains(person))
                return true;
        }

        return false;
    }

    // Finds the time slots in which attendees of the events have another meeting which causes a time conflict.
    private List<Event> getConflictingTimeSlots(Collection<Event> events, Collection<String> attendees) {

        List<Event> conflictingTimeSlots = new ArrayList<>();

        for (Event e : events) {
            Collection<String> eventAttendees = e.getAttendees();

            if (isAttendeeBusy(eventAttendees,attendees))
                conflictingTimeSlots.add(e);
        }
        return (conflictingTimeSlots = sort(conflictingTimeSlots));
    }

    /* Finds an avaible time slot in which a meeting can occur given a List of times the meeting cannot
    *  be held and the duration of the meeting.
    */
    private Collection<TimeRange> getOpenTimeSlots(List<Event> conflictingTimeSlots, long duration) {

        Collection<TimeRange> OpenTimeSlots = new ArrayList<TimeRange>();
        int possibleEventRange = TimeRange.START_OF_DAY;

        for (Event conflict : conflictingTimeSlots) {

            int conflictStartTime = conflict.getWhen().start();
            int conflictEndTime = conflict.getWhen().end();

            if (possibleEventRange + duration <= conflictStartTime) {

                OpenTimeSlots.add(TimeRange.fromStartEnd(possibleEventRange, conflictStartTime, false));
                possibleEventRange = conflictEndTime;

            }

            else if (possibleEventRange < conflictEndTime)
                possibleEventRange = conflictEndTime;
        }

        // If there is time around the end of the day add it to the list of open slots.
        if (TimeRange.END_OF_DAY - possibleEventRange >= duration) 
            OpenTimeSlots.add(TimeRange.fromStartEnd(possibleEventRange, TimeRange.END_OF_DAY, true));

        return OpenTimeSlots;
    }

    /* This method finds avaible time slots given an event class indicating when people are busy
    *  and a MeetingRequest class indicating the details of the meeting. 
    */
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        
        long duration = request.getDuration();
        Collection<TimeRange> OpenTimeSlots = new ArrayList<TimeRange>();

        // If the meeting duration is longer than 24 hours it cannot be scheduled.
        if (duration > TimeRange.WHOLE_DAY.duration()) {
        return OpenTimeSlots;
        }

        // If there are not any attendees the meeting can be all day.
        if (request.getAttendees().isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
        }

        Collection<String> attendees = request.getAttendees();

        // Find the conflicting time slots and then use that to find the open time slots.
        List<Event> conflictingTimeSlots = getConflictingTimeSlots(events, attendees);   
        OpenTimeSlots = getOpenTimeSlots(conflictingTimeSlots, duration);

        return OpenTimeSlots;
    }
}
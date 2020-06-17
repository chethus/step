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

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

public final class FindMeetingQuery {

    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        // Get request duration, mandatory attendees, and optional attendees.
        long duration = request.getDuration();
        Collection<String> mandatoryAttendees = request.getAttendees();
        Collection<String> allAttendees = new ArrayList<String>();
        allAttendees.addAll(mandatoryAttendees);
        allAttendees.addAll(request.getOptionalAttendees());

        // Sort events by start time.
        Comparator<Event> EVENT_BY_START 
            = (a,b) -> Integer.compare(a.getWhen().start(), b.getWhen().start());
        ArrayList<Event> eventsList = new ArrayList(events);
        Collections.sort(eventsList, EVENT_BY_START);

        // Prepare lists for mandatory and optional time ranges.
        ArrayList<TimeRange> mandatoryTimes = new ArrayList<>();
        ArrayList<TimeRange> optionalTimes = new ArrayList<>();

        // Start time of current open window for mandatory and optional time ranges.
        int mandatoryStart = 0;
        int optionalStart = 0;

        for (Event e: eventsList) {
            // If the day is over or the next open window begins the next day, break.
            // Mandatory start will always be less than optional start since it is more restricted.
            if (mandatoryStart >= 1440 || e.getWhen().start() >= 1440) {
                break;
            }

            // If some attendees (including optional) have this event.
            if (!Collections.disjoint(allAttendees, e.getAttendees())) {
                // End the current optional window and save it if it's long enough.
                int curEnd = e.getWhen().start();
                if (curEnd - optionalStart >= duration) {
                    TimeRange optionalSlot = TimeRange.fromStartEnd(optionalStart, curEnd, false);
                    optionalTimes.add(optionalSlot);
                }

                // Start the new window at the end of the event.
                optionalStart = Math.max(optionalStart, e.getWhen().end());

                // If the event also includes mandatory attendees, update the mandatory window and start time too.
                if (!Collections.disjoint(mandatoryAttendees, e.getAttendees())) {
                    if (curEnd - mandatoryStart >= duration) {
                        TimeRange mandatorySlot = TimeRange.fromStartEnd(mandatoryStart, curEnd, false);
                        mandatoryTimes.add(mandatorySlot);
                    }
                    mandatoryStart = Math.max(mandatoryStart, e.getWhen().end());
                }
            }
        }

        // Check the last window created by the end of the day.
        if (1440 - mandatoryStart >= duration) {
            mandatoryTimes.add(TimeRange.fromStartEnd(mandatoryStart, 1440, false));

            // If the mandatory window is large enough, check if the optional window is as well (it's always shorter).
            if (1440 - optionalStart >= duration) {
                optionalTimes.add(TimeRange.fromStartEnd(optionalStart, 1440, false));
            }
        }

        // If no one can attend the event, return no time ranges.
        if (optionalTimes.size() == 0 && mandatoryAttendees.size() == 0) {
            return new ArrayList<TimeRange>();
        }
        
        // If there are time ranges that work for optional attendees, return them.
        return optionalTimes.size() > 0 ? optionalTimes : mandatoryTimes;
    }
}

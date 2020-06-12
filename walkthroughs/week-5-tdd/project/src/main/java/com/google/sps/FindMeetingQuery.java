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
        long duration = request.getDuration();
        Collection<String> mandatoryAttendees = request.getAttendees();
        Collection<String> allAttendees = new ArrayList<String>();
        allAttendees.addAll(mandatoryAttendees);
        allAttendees.addAll(request.getOptionalAttendees());

        Comparator<Event> EVENT_BY_START 
            = (a,b) -> Integer.compare(a.getWhen().start(), b.getWhen().start());
        ArrayList<Event> eventsList = new ArrayList(events);
        Collections.sort(eventsList, EVENT_BY_START);

        ArrayList<TimeRange> mandatoryTimes = new ArrayList<>();
        ArrayList<TimeRange> optionalTimes = new ArrayList<>();
        int mandatoryStart = 0;
        int optionalStart = 0;
        for (Event e: eventsList) {
            if (mandatoryStart >= 1440 || e.getWhen().start() >= 1440) {
                break;
            }
            if (!Collections.disjoint(allAttendees, e.getAttendees())) {
                int curEnd = e.getWhen().start();
                if (curEnd - optionalStart >= duration) {
                    TimeRange optionalSlot = TimeRange.fromStartEnd(optionalStart, curEnd, false);
                    optionalTimes.add(optionalSlot);
                }
                optionalStart = Math.max(optionalStart, e.getWhen().end());
                if (!Collections.disjoint(mandatoryAttendees, e.getAttendees())) {
                    if (curEnd - mandatoryStart >= duration) {
                        TimeRange mandatorySlot = TimeRange.fromStartEnd(mandatoryStart, curEnd, false);
                        mandatoryTimes.add(mandatorySlot);
                    }
                    mandatoryStart = Math.max(mandatoryStart, e.getWhen().end());
                }
            }
        }
        if (1440 - mandatoryStart >= duration) {
            mandatoryTimes.add(TimeRange.fromStartEnd(mandatoryStart, 1440, false));
            if (1440 - optionalStart >= duration) {
                optionalTimes.add(TimeRange.fromStartEnd(optionalStart, 1440, false));
            }
        }
        if (optionalTimes.size() == 0 && mandatoryAttendees.size() == 0) {
            return new ArrayList<TimeRange>();
        }
        return optionalTimes.size() > 0 ? optionalTimes : mandatoryTimes;
    }
}

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
        Collection<String> meetingAttendees = request.getAttendees();
        Collection<String> optionalAttendees = request.getOptionalAttendees();

        Comparator<Event> EVENT_BY_START 
            = (a,b) -> Integer.compare(a.getWhen().start(), b.getWhen().start());
        Collections.sort(new ArrayList(events), EVENT_BY_START);

        ArrayList<TimeRange> meetingTimes = new ArrayList<>();
        ArrayList<TimeRange> optionalTimes = new ArrayList<>();
        int curStart = 0;
        for (Event e: events) {
            if (curStart >= 1440 || e.getWhen().start() >= 1440) {
                break;
            }
            if (!Collections.disjoint(meetingAttendees, e.getAttendees())) {
                int curEnd = e.getWhen().start();
                TimeRange t = TimeRange.fromStartEnd(curStart, curEnd, false);
                if (curEnd - curStart >= duration) {
                    if (Collections.disjoint(optionalAttendees, e.getAttendees())) {
                        optionalTimes.add(t);
                    }
                    meetingTimes.add(t);
                }
                curStart = Math.max(curStart, e.getWhen().end());
            }
        }
        if (1440 - curStart >= duration) {
            optionalTimes.add(TimeRange.fromStartEnd(curStart, 1440, false));
            meetingTimes.add(TimeRange.fromStartEnd(curStart, 1440, false));
        }
        return optionalTimes.size() > 0 ? optionalTimes : meetingTimes;
    }
}

package com.tecomgroup.qos.domain.recording.xml;

/**
 * Created by uvarov.m on 20.01.2016.
 */

import com.tecomgroup.qos.domain.recording.Event;
import com.tecomgroup.qos.domain.recording.Schedule;
import java.util.UUID;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@XmlRootElement(name = "Config")
@XmlAccessorType(XmlAccessType.FIELD)
public class Config {

    public Config() {}

    public static Config fromEntities(List<Schedule> scheduleList) {
        Config config = new Config();
        List<TaskGroup> groups = new ArrayList<>();
        config.setTaskGroup(groups);

        for(Schedule schedule: scheduleList) {
            TaskGroup group = new TaskGroup();
            group.setName(UUID.randomUUID().toString());

            TaskGroup.Task task = new TaskGroup.Task();
            task.setKey(schedule.getTaskKey());

            List<TaskGroup.Task> tasks = new ArrayList<>();
            tasks.add(task);

            group.setTask(tasks);
            groups.add(group);

            if(schedule.getEventList() != null) {
                List<TaskGroup.Event> events = new ArrayList<>();
                for (Event event : schedule.getEventList()) {
                    TaskGroup.Event eventDto = new TaskGroup.Event();
                    eventDto.setBegin(event.getStartDateTime());
                    eventDto.setEnd(event.getEndDateTime());
                    events.add(eventDto);
                }
                group.setEvent(events);
            }
        }
        return config;
    }

    public static Config fromEntity(Schedule schedule, List<String> taskKeys) {
        Config config = new Config();
        TaskGroup group = new TaskGroup();
        group.setName(UUID.randomUUID().toString());

        List<TaskGroup.Task> tasks = new ArrayList<>();
        for(String taskKey: taskKeys) {
            TaskGroup.Task task = new TaskGroup.Task();
            task.setKey(taskKey);
            tasks.add(task);
        }
        group.setTask(tasks);

        if(schedule.getEventList() != null) {
            List<TaskGroup.Event> events = new ArrayList<>();
            for (Event event : schedule.getEventList()) {
                TaskGroup.Event eventDto = new TaskGroup.Event();
                eventDto.setBegin(event.getStartDateTime());
                eventDto.setEnd(event.getEndDateTime());
                events.add(eventDto);
            }
            group.setEvent(events);
        }

        List<TaskGroup> groups = new ArrayList<>();
        groups.add(group);
        config.setTaskGroup(groups);

        return config;
    }

    @XmlElementWrapper(name = "timetable_scheduler")
    @XmlElement(name = "task_group")
    private List<TaskGroup> taskGroup;

    public List<TaskGroup> getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(List<TaskGroup> taskGroup) {
        this.taskGroup = taskGroup;
    }

    /*
        TaskGroup class
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TaskGroup {

        @XmlAttribute(name = "name", required = true)
        private String name;

        @XmlElementWrapper(name = "timetable")
        @XmlElement(name = "event")
        private List<Event> event = new ArrayList<>();


        @XmlElementWrapper(name = "tasks")
        @XmlElement(name = "task")
        private List<Task> task = new ArrayList<>();

        public List<Task> getTask() {
            return task;
        }

        public List<Event> getEvent() {
            return event;
        }

        public void setEvent(List<Event> event) {
            this.event = event;
        }

        public void setTask(List<Task> task) {
            this.task = task;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /*
            Event class
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Event {
            @XmlAttribute(name = "begin")
            private String begin;

            @XmlAttribute(name = "end")
            private String end;

            public String getBegin() {
                return begin;
            }

            public String getEnd() {
                return end;
            }

            public void setBegin(String begin) {
                this.begin = begin;
            }

            public void setEnd(String end) {
                this.end = end;
            }
        }

        /*
            Task class
         */

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Task {
            @XmlAttribute(name = "key")
            private String key;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }

}

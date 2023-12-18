package com.example.blitz.Models;

import java.util.Map;

public class Groups {
    String groupId, groupName;
    Map<String, Boolean> members;

    public Groups(String groupName, Map<String, Boolean> members) {
        this.groupName = groupName;
        this.members = members;
    }

    public Groups() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public void addMember(String userId) {
        members.put(userId, true);
    }
}

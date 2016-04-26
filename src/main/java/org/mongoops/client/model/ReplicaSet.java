package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true, value = {
    "disabled" } )
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplicaSet {

    private String _id;
    private List<ReplicaSetMember> members;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<ReplicaSetMember> getMembers() {

        if (members == null) {
            members = new ArrayList<>();
        }
        return members;
    }

    public void setMembers(List<ReplicaSetMember> members) {
        this.members = members;
    }

    public void removeMember(String host) {

        for (int i = 0; i < members.size(); i++) {
            ReplicaSetMember member = members.get(i);
            if (member.getHost().equals(host)) {
                members.remove(i);
                break;
            }
        }
    }
}

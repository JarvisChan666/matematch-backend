package com.jvc.matematch.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamJoinRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long teamId;

        private String password;


}

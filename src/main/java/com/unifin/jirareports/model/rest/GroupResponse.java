package com.unifin.jirareports.model.rest;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GroupResponse {
        public String self;
        public int maxResults;
        public int startAt;
        public int total;
        public boolean isLast;
        public ArrayList<UserGroup> values;
}

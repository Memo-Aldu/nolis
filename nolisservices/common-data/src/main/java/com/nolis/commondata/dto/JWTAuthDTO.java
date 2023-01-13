package com.nolis.commondata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class JWTAuthDTO {
    private String access_token;
    private ArrayList<String> authorities;
    private String subject;
}

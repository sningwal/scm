package com.contactmanager.contactmanager.payload.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactSearchForm {
    private String field;
    private String value;

}
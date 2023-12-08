package com.jvc.matematch.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    protected int pageSize = 10;

    protected int pageNum = 1;
}

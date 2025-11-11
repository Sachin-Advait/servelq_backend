package com.gis.servelq.dto;

import com.gis.servelq.models.Counter;
import lombok.Data;

@Data
public class CounterDTO {
    private String id;
    private String name;
    private String code;

    public static CounterDTO fromEntity(Counter counter) {
        CounterDTO dto = new CounterDTO();
        dto.setId(counter.getId());
        dto.setName(counter.getBranch().getName());
        dto.setCode(counter.getCode());
        return dto;
    }
}

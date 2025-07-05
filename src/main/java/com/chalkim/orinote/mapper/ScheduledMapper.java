package com.chalkim.orinote.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.chalkim.orinote.dto.ScheduledJobDto;
import com.chalkim.orinote.model.ScheduledJob;

@Mapper(componentModel = "spring")
public interface ScheduledMapper {
    ScheduledMapper INSTANCE = Mappers.getMapper(ScheduledMapper.class);

    ScheduledJobDto scheduledJobToScheduledJobDto(ScheduledJob scheduledJob);
}

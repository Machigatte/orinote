package com.chalkim.orinote.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.chalkim.orinote.dto.SummaryCreateDto;
import com.chalkim.orinote.dto.SummaryUpdateDto;
import com.chalkim.orinote.model.Summary;

@Mapper(componentModel = "spring")
public interface SummaryMapper {
    SummaryMapper INSTANCE = Mappers.getMapper( SummaryMapper.class );
 
    SummaryCreateDto summaryToSummaryCreateDto(Summary summary);
    SummaryUpdateDto summaryToSummaryUpdateDto(Summary summary);
}

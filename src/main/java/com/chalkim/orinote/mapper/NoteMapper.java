package com.chalkim.orinote.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.chalkim.orinote.dto.NoteDto;
import com.chalkim.orinote.model.Note;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    NoteDto noteToNoteCreateDto(Note note);
}

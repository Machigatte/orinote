package com.chalkim.orinote.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.chalkim.orinote.dto.NoteCreateDto;
import com.chalkim.orinote.dto.NoteUpdateDto;
import com.chalkim.orinote.model.Note;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    NoteCreateDto noteToNoteCreateDto(Note note);
    NoteUpdateDto noteToNoteUpdateDto(Note note);
}

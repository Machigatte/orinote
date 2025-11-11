package com.chalkim.orinote.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.chalkim.orinote.dto.note.CreateNoteDto;
import com.chalkim.orinote.dto.note.NoteDetailDto;
import com.chalkim.orinote.dto.note.NoteListDto;
import com.chalkim.orinote.dto.note.UpdateNoteDto;
import com.chalkim.orinote.model.Note;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    // DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "user", ignore = true)
    Note toEntity(CreateNoteDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "user", ignore = true)
    Note toEntity(UpdateNoteDto dto);

    // Entity → DTO
    NoteDetailDto toDto(Note note);

    List<NoteListDto> toDtoList(List<Note> notes);
}

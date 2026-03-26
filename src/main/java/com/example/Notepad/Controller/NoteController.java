package com.example.Notepad.Controller;

import com.example.Notepad.Model.Note;
import com.example.Notepad.Repository.NoteRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository repo;

    // Constructor Injection
    public NoteController(NoteRepository repo) {
        this.repo = repo;
    }

    // Create a new note
    @PostMapping
    public ResponseEntity<?> createNote(
            @RequestBody Note note,
            Authentication auth) {

        String username = auth.getName();

        note.setUsername(username);

        Note savedNote = repo.save(note);
        return ResponseEntity.ok(savedNote);
    }

    // Get all notes for logged-in user
    @GetMapping
    public ResponseEntity<?> getNotes(Authentication auth) {

        String username = auth.getName();

        List<Note> notes = repo.findByUsername(username);
        return ResponseEntity.ok(notes);
    }

    // ✅ Delete note by ID (only if it belongs to logged-in user)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(
            @PathVariable Long id,
            Authentication auth) {

        String username = auth.getName();

        Optional<Note> noteOpt = repo.findById(id);

        if (noteOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Note not found");
        }

        Note note = noteOpt.get();

        //  user can delete only their own note
        if (!note.getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        repo.delete(note);

        return ResponseEntity.ok("Note deleted successfully");
    }
}
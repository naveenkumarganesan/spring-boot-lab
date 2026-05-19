package com.example.library.member;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping
    public List<MemberDTO> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public MemberDTO getById(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<MemberDTO> create(@Valid @RequestBody MemberDTO dto) {
        MemberDTO saved = service.create(dto);
        return ResponseEntity.created(URI.create("/members/" + saved.getId())).body(saved);
    }
}

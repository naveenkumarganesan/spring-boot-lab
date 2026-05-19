package com.example.library.member;

import com.example.library.common.MemberNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<MemberDTO> listAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public MemberDTO get(Long id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }

    public MemberDTO create(MemberDTO dto) {
        Member m = new Member(
                dto.getName(),
                dto.getEmail(),
                dto.getRegisteredOn() != null ? dto.getRegisteredOn() : LocalDate.now()
        );
        return toDto(repository.save(m));
    }

    private MemberDTO toDto(Member m) {
        MemberDTO dto = new MemberDTO();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setEmail(m.getEmail());
        dto.setRegisteredOn(m.getRegisteredOn());
        return dto;
    }
}

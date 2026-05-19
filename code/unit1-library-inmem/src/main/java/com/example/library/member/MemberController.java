package com.example.library.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final List<Member> members = new ArrayList<>(List.of(
            new Member(1L, "Asha Rao", "asha@example.com", LocalDate.of(2024, 1, 10)),
            new Member(2L, "Vikram Singh", "vikram@example.com", LocalDate.of(2024, 3, 22))
    ));
    private final AtomicLong idGenerator = new AtomicLong(3);

    @GetMapping
    public List<Member> list() {
        return members;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getById(@PathVariable Long id) {
        return findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Member> create(@RequestBody Member member) {
        member.setId(idGenerator.getAndIncrement());
        if (member.getRegisteredOn() == null) {
            member.setRegisteredOn(LocalDate.now());
        }
        members.add(member);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> update(@PathVariable Long id, @RequestBody Member updated) {
        Optional<Member> existing = findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        Member m = existing.get();
        m.setName(updated.getName());
        m.setEmail(updated.getEmail());
        return ResponseEntity.ok(m);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = members.removeIf(m -> m.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Optional<Member> findById(Long id) {
        return members.stream().filter(m -> m.getId().equals(id)).findFirst();
    }
}

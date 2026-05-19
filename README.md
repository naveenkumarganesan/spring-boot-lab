# Spring Boot — 4-Day Seminar

A hands-on Spring Boot seminar designed for 3rd-year engineering students. Two units, 4 days, 12 labs, ending with a containerized Library Management System.

## Audience

Mixed Java background. The baseline assumes Java syntax and basic OOP only; stretch tasks in every lab keep stronger students engaged.

## Pre-seminar install checklist

Students must install the following before Day 1:

1. **JDK 21** — `java --version` should report 21.x. ([Adoptium Temurin 21](https://adoptium.net/))
2. **IntelliJ IDEA Community Edition** — free, best Spring support of the free IDEs.
3. **Git** — `git --version` should work.
4. **Docker Desktop** — required for Lab 12.
5. **A GitHub account** — capstone submission.

Maven is bundled with IntelliJ; a separate install is not required.

## Day-by-day overview

See [`curriculum.md`](curriculum.md).

## Lab handouts

See the [`labs/`](labs/) directory. Twelve labs, three per day.

## Reference code

- [`code/unit1-library-inmem/`](code/unit1-library-inmem/) — Unit 1 finished state.
- [`code/unit2-library-jpa/`](code/unit2-library-jpa/) — Unit 2 finished state.

Each lab's checkpoint is captured as a git tag (e.g., `lab-04-end`). To jump to the end-of-Lab-4 state:

```bash
cd code/unit1-library-inmem
git checkout lab-04-end
```

## Capstone evaluation

See [`evaluation.md`](evaluation.md).

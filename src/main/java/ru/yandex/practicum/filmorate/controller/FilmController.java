package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate dayOfCreationCinema = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.debug("Получен фильм для добавления: {}", film);
        filmValidator(film); // Вызов валидации
        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Фильм {} успешно добавлен", film);

        // Возвращаем статус 201 (Created) и добавленный фильм
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Не введен Id фильма");
            throw new ValidationException("id фильма не может быть пустым");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());

            // Обновляем данные фильма
            oldFilm.setName(film.getName());
            log.info("Название фильма {} изменено", oldFilm);
            oldFilm.setDescription(film.getDescription());
            log.info("Описание фильма {} изменено", oldFilm);
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.info("Дата выхода фильма {} изменена", oldFilm);
            oldFilm.setDuration(film.getDuration());
            log.info("Длительность фильма {} изменена", oldFilm);

            // Возвращаем статус 200 (OK) и обновленный фильм
            return ResponseEntity.ok(oldFilm);
        }
        log.error("Фильм с id = {} не найден", film.getId());
        throw new UserNotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    private void filmValidator(@RequestBody Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не заполнено при добавлении");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма слишком длинное при добавлении");
            throw new ValidationException("Слишком длинное описание");
        }
        if (film.getReleaseDate().isBefore(dayOfCreationCinema)) {
            log.error("Указана неверная дата выходи фильма при добавлении");
            throw new ValidationException("Указана неверная дата");
        }
        if (film.getDuration() < 0) {
            log.error("При добавлении фильма длительность указана меньше 0");
            throw new ValidationException("Длительность фильма не может быть меньше 0");
        }
    }
}

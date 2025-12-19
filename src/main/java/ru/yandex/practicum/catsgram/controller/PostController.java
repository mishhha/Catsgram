package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.SortOrder;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {

    PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public Collection<Post> findAll(
        @RequestParam(defaultValue = "asc") String sort,
        @RequestParam(defaultValue = "0") int from,
        @RequestParam(defaultValue = "10") int size
    ) {

        if(from < 0) {
            throw new ParameterNotValidException(String.valueOf(from),
                "Значение не может быть отрицательным."
            );
        }

        if(size <= 0) {
            throw new ParameterNotValidException(String.valueOf(size),
                "Некорректный размер выборки. Размер должен быть больше нуля."
            );
        }

        SortOrder sortOrder = SortOrder.from(sort);

        if(sortOrder == null) {
            throw new ParameterNotValidException(sort,
                "Недопустимое значение параметра sort. Допустимые значения: asc, desc, ascending, descending."
            );
        }

        return postService.findAll(sortOrder, from, size);
    }

    @GetMapping("/{id}")
    public Post findPostById(@PathVariable long id) {
        return postService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorHandler.ErrorResponse handlerNotFound(final NotFoundException e) {
        return new ErrorHandler.ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorHandler.ErrorResponse handlerDuplicatedData(final DuplicatedDataException e) {
        return new ErrorHandler.ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorHandler.ErrorResponse handlerConditionsNotMet(final ConditionsNotMetException e) {
        return new ErrorHandler.ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorHandler.ErrorResponse handlerParameterNotValid(final ParameterNotValidException e) {
        return new ErrorHandler.ErrorResponse(
            "Некорректное значение параметра " + "<"
                + e.getParametr() + ">:" + "<"
                + e.getReason() + ">"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorHandler.ErrorResponse handlerThrowable(final Throwable e) {
        return new ErrorHandler.ErrorResponse(
            "Произошла непредвиденная ошибка."
        );
    }

}
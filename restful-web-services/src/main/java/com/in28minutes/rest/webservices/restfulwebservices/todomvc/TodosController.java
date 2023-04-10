package com.in28minutes.rest.webservices.restfulwebservices.todomvc;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping(value = "/todos")
public class TodosController {
    private Set<Todo> todos = new HashSet<Todo>();

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<Collection<ResourceWithUrl>> listAll(){
        List<ResourceWithUrl> resourceWithUrls = todos.stream().map(todo -> toResource(todo)).collect(Collectors.toList());
        return new ResponseEntity<>(resourceWithUrls, HttpStatus.OK);
    }

    @RequestMapping(value ="/{todo-id}", method = RequestMethod.GET)
    public HttpEntity<ResourceWithUrl> getTodo(@PathVariable("todo-id") long id ){

        Optional<Todo> todoOptional = tryToFindById(id);

        if(!todoOptional.isPresent()){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return respondWithResource(todoOptional.get(), HttpStatus.OK);
    }

    private Optional<Todo> tryToFindById(long id){
        return todos.stream().filter(todo -> todo.getId() == id).findFirst();

    }

    @RequestMapping(method = RequestMethod.POST, headers = {"Content-type = application/json"})
    public HttpEntity<ResourceWithUrl> saveTodo(@RequestBody Todo todo){
        todo.setId(todos.size() + 1);
        todos.add(todo);
        return respondWithResource(todo, HttpStatus.CREATED);
    }

    @RequestMapping(method= RequestMethod.DELETE)
    public void deleteAllTodos(){
     todos.clear();
    }

    @RequestMapping(value = "/{todo-id}", method = RequestMethod.DELETE)
    public void deleteOneTodo(@PathVariable("todo-id") long id){
        Optional<Todo> todoOptional = tryToFindById(id);

        if(todoOptional.isPresent()){
            todos.remove(todoOptional.get());
        }

    }

    @RequestMapping(value = "/{todo-id}", method = RequestMethod.PATCH, headers = {"Content-type= application/json"})
    public HttpEntity<ResourceWithUrl> updateTodo(@PathVariable("todo-id") long id, @RequestBody Todo newTodo){
        Optional<Todo> todoOptional = tryToFindById(id);

        if(!todoOptional.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else if(newTodo == null){
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        todos.remove(todoOptional.get());
        Todo mergeTodo = todoOptional.get().merge(newTodo);
        todos.add(mergeTodo);
        return respondWithResource(mergeTodo, HttpStatus.OK);
    }

    private String getHref(Todo todo){
     return linkTo(methodOn(this.getClass()).getTodo(todo.getId())).withSelfRel().getHref();

    }
    private ResourceWithUrl toResource(Todo todo) {
        return new ResourceWithUrl(todo, getHref(todo));
    }

    private HttpEntity<ResourceWithUrl> respondWithResource(Todo todo, HttpStatus statusCode) {
        ResourceWithUrl resourceWithUrl = toResource(todo);
        return new ResponseEntity<>(resourceWithUrl, statusCode);
    }

}

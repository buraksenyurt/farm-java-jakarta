package com.lectures.rest;

import com.lectures.entity.Todo;
import com.lectures.event.TodoCreatedEvent;
import com.lectures.interceptor.LogExecutionTime;
import com.lectures.service.TodoService;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoRest {

    @Inject
    TodoService todoService;

    @Inject
    private Event<TodoCreatedEvent> todoEvent;

    /*
        Asenkron versiyon kullanımında WELD taraında aşağıdaki hatalar alınabilir.
    
     org.jboss.weld.exceptions.WeldException: WELD-001524: UnabletoloadproxyclassforbeanManagedBean[
    classorg.glassfish.jersey.ext.cdi1x.transaction.internal.WebAppExceptionHolder
]withqualifiers[
    
    @WaeQualifier@Any
]withclassclassorg.glassfish.jersey.ext.cdi1x.transaction.internal.WebAppExceptionHolder 
    
    Bu nedenle thread yönetimini Java ortamından değil de uygulama sunucusundan
    (bu örnek için Payara) talep etmemiz gerekiyor. Söz konusu resource'un
    getTodoAsync metodunda parametre olarak nasıl kullanıldığına dikkat edelim.
    
    */
    @Resource
    private ManagedExecutorService managedExecutor;

    @Path("{id}")
    @GET
    @LogExecutionTime
    public Todo getTodo(@PathParam("id") Long id) {
        // api/v1/todo/{id}
        return todoService.findTodoById(id);
    }

    @Path("list")
    @GET
    @LogExecutionTime
    public List<Todo> getTodos() {
        // api/v1/todo/list
        return todoService.getTodos();
    }

    @Path("async/list")
    @GET
    public void getTodoAsync(@Suspended final AsyncResponse asyncResponse) {
        // api/v1/todo/async/list
        CompletableFuture.supplyAsync(() -> {
            return todoService.getTodos();
        }, managedExecutor).thenAccept(result -> {
            asyncResponse.resume(Response.ok(result).build());
        }
        ).exceptionally(ex -> {
            asyncResponse.resume(Response.serverError().entity(ex.getMessage()).build());
            return null;
        });

    }

    @Path("new")
    @POST
    @LogExecutionTime
    public Response createTodo(Todo todo) {
        // api/v1/todo/new
        var created = todoService.createTodo(todo);
        var uri = UriBuilder.fromResource(TodoRest.class).path(created.getId().toString()).build();

        // Todo oluşturulduğunda bunu bildiren bir event fırlatıyoruz
        todoEvent.fire(new TodoCreatedEvent(created));

        return Response.created(uri).build();
    }

    @Path("update")
    @PUT
    @LogExecutionTime
    public Response updateTodo(Todo todo) {
        // api/v1/todo/update
        todoService.updateTodo(todo);
        return Response.ok(todo).build();
    }

    @Path("{id}")
    @DELETE
    @LogExecutionTime
    public Response deleteTodo(@PathParam("id") Long id) {
        // api/v1/todo/{id}
        todoService.deleteTodo(id);
        return Response.noContent().build();
    }

    @Path("status")
    @POST
    @LogExecutionTime
    public Response markAsComplete(@QueryParam("id") Long id) {
        Todo todo = todoService.findTodoById(id);
        todo.setIsCompleted(true);
        todoService.updateTodo(todo);

        return Response.ok(todo).build();
    }
}

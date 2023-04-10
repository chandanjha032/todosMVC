package com.in28minutes.rest.webservices.restfulwebservices.todomvc;

//import javax.xml.bind.annotation.XmlRootElement;
//@XmlRootElement
public class ResourceWithUrl<T> {

    private T content;
    private String url;


    public ResourceWithUrl() {
    }

    public ResourceWithUrl(T content, String url) {
        this.content = content;
        this.url = url;
    }

   // @JsonUnwrapped
    //@XmlAnyElement
    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

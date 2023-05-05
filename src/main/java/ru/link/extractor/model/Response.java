package ru.link.extractor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {

    private boolean isSuccess;
    private String result;

    public Response(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}

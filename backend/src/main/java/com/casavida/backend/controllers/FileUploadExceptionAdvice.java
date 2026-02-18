package com.casavida.backend.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.ResponseEntity;
import com.casavida.backend.payload.response.MessageResponse;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.EXPECTATION_FAILED)
                .body(new MessageResponse("El archivo es demasiado grande. El límite es 10MB."));
    }
}

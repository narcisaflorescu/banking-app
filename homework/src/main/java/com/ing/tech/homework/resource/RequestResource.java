package com.ing.tech.homework.resource;

import com.ing.tech.homework.dto.RequestDtoGet;
import com.ing.tech.homework.dto.RequestDtoPost;
import com.ing.tech.homework.dto.TransferDtoPost;
import com.ing.tech.homework.model.Request;
import com.ing.tech.homework.model.Transfer;
import com.ing.tech.homework.repository.RequestRepository;
import com.ing.tech.homework.repository.TransferRepository;
import com.ing.tech.homework.service.RequestService;
import com.ing.tech.homework.service.TransferService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.ing.tech.homework.security.AuthorizationRoles.ROLE_ADMIN;
import static com.ing.tech.homework.security.AuthorizationRoles.ROLE_USER;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
@Secured({ROLE_ADMIN})

public class RequestResource {

    private RequestService requestService;
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<RequestDtoGet>> getAllRequests(){
        return ResponseEntity.ok(requestService.findAll());
    }

}

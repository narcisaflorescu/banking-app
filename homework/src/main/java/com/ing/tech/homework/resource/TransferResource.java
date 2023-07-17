package com.ing.tech.homework.resource;


import com.ing.tech.homework.dto.TransferDtoGet;
import com.ing.tech.homework.dto.TransferDtoPost;
import com.ing.tech.homework.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ing.tech.homework.security.AuthorizationRoles.ROLE_ADMIN;


@RestController
@RequestMapping("/transfers")
@AllArgsConstructor
@Secured({ROLE_ADMIN})
public class TransferResource {
    private TransferService transferService;

    @GetMapping
    public ResponseEntity<List<TransferDtoGet>> getAllTransfers(){
        return ResponseEntity.ok(transferService.findAll());
    }




}

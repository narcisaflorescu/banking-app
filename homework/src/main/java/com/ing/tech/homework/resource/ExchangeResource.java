package com.ing.tech.homework.resource;

import com.ing.tech.homework.dto.ExchangeDtoGet;
import com.ing.tech.homework.service.ExchangeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ing.tech.homework.security.AuthorizationRoles.ROLE_ADMIN;

@RestController
@RequestMapping("/exchanges")
@AllArgsConstructor
@Secured({ROLE_ADMIN})
public class ExchangeResource {

    private ExchangeService exchangeService;
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<ExchangeDtoGet>> getAllRequests(){
        return ResponseEntity.ok(exchangeService.findAll());
    }
}

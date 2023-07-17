package com.ing.tech.homework.service;

import com.ing.tech.homework.dto.RequestDtoGet;
import com.ing.tech.homework.model.Request;
import com.ing.tech.homework.repository.RequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RequestService {
    private RequestRepository requestRepository;
    private ModelMapper modelMapper;

    public List<RequestDtoGet> findAll() {
        List<Request> requests = requestRepository.findAll();
        return requests.stream()
                .sorted(Comparator.comparing(Request::getDate))
                .map(e -> modelMapper.map(e, RequestDtoGet.class))
                .collect(Collectors.toList());
    }

}

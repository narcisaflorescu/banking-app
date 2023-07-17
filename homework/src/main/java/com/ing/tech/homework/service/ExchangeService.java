package com.ing.tech.homework.service;

import com.ing.tech.homework.dto.ExchangeDtoGet;
import com.ing.tech.homework.model.Exchange;
import com.ing.tech.homework.repository.ExchangeRepository;
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
public class ExchangeService {

    private ExchangeRepository exchangeRepository;
    private ModelMapper modelMapper;

    public List<ExchangeDtoGet> findAll() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        return exchanges.stream()
                .sorted(Comparator.comparing(Exchange::getDate))
                .map(e -> modelMapper.map(e, ExchangeDtoGet.class))
                .collect(Collectors.toList());
    }

}

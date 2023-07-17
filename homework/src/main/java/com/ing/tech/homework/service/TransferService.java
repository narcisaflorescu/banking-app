package com.ing.tech.homework.service;

import com.ing.tech.homework.dto.TransferDtoGet;
import com.ing.tech.homework.model.Transfer;
import com.ing.tech.homework.repository.TransferRepository;
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
public class TransferService {

    private TransferRepository transferRepository;
    private ModelMapper modelMapper;

    public List<TransferDtoGet> findAll() {
        List<Transfer> transfers = transferRepository.findAll();
        return transfers.stream()
                .sorted(Comparator.comparing(Transfer::getDate))
                .map(e -> modelMapper.map(e, TransferDtoGet.class))
                .collect(Collectors.toList());
    }

}
